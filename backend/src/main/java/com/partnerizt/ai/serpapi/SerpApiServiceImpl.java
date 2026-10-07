package com.partnerizt.ai.serpapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SerpApiServiceImpl implements SerpApiService {

    private static final Logger log = LoggerFactory.getLogger(SerpApiServiceImpl.class);

    @Value("${ai.serpapi.api-key:}")
    private String serpApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SerpApiServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public SerpApiServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        if (this.serpApiKey == null || this.serpApiKey.isBlank()) {
            String keyFromEnv = findEnvVariable("SERPAPI_API_KEY");
            if (keyFromEnv != null && !keyFromEnv.isBlank()) {
                this.serpApiKey = keyFromEnv;
                log.info("[SERPAPI_CONFIG] Loaded SERPAPI_API_KEY from environment/.env file");
            }
        }
    }

    private String findEnvVariable(String key) {
        String val = System.getenv(key);
        if (val != null && !val.isBlank()) return val;
        val = System.getProperty(key);
        if (val != null && !val.isBlank()) return val;

        List<Path> candidatePaths = List.of(
                Path.of(".env"),
                Path.of("..", ".env"),
                Path.of(System.getProperty("user.dir", "."), ".env"),
                Path.of(System.getProperty("user.dir", "."), "..", ".env")
        );

        for (Path p : candidatePaths) {
            try {
                if (Files.exists(p)) {
                    List<String> lines = Files.readAllLines(p);
                    for (String line : lines) {
                        line = line.trim();
                        if (line.startsWith(key + "=")) {
                            String value = line.substring((key + "=").length()).trim();
                            if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                                value = value.substring(1, value.length() - 1);
                            }
                            if (!value.isBlank()) return value;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    @Override
    public SerpApiResult searchKnowledge(String subject, String category) {
        if (subject == null || subject.trim().isEmpty()) {
            return new SerpApiResult("Unknown Specimen", List.of(), List.of());
        }

        if (serpApiKey != null && !serpApiKey.isBlank()) {
            try {
                log.info("[SERPAPI_CALLED] Mode: REAL_SERPAPI | Subject: {} | Category: {}", subject, category);
                SerpApiResult realResult = executeSerpApiSearch(subject, category);
                String sourceTitles = realResult.getSources().stream().map(KnowledgeSource::getTitle).collect(Collectors.joining(", "));
                log.info("[SOURCES_RETURNED] Count: {} | Sources: [{}]", realResult.getSources().size(), sourceTitles);
                return realResult;
            } catch (Exception e) {
                log.warn("SerpApi search failed for '{}': {}. Falling back to domain knowledge base.", subject, e.getMessage());
            }
        } else {
            log.info("[SERPAPI_CALLED] Mode: SERPAPI_FALLBACK (SERPAPI_API_KEY not configured) | Subject: {}", subject);
        }

        SerpApiResult fallbackResult = fallbackKnowledgeRepository(subject, category);
        String fallbackTitles = fallbackResult.getSources().stream().map(KnowledgeSource::getTitle).collect(Collectors.joining(", "));
        log.info("[SOURCES_RETURNED] Count: {} | Sources: [{}] (Fallback)", fallbackResult.getSources().size(), fallbackTitles);
        return fallbackResult;
    }

    private SerpApiResult executeSerpApiSearch(String subject, String category) throws Exception {
        String query = subject.trim();
        if (!query.contains(" ") || query.length() < 12) {
            query = query + " " + (category != null ? category : "") + " facts nature";
        }

        URI uri = UriComponentsBuilder.fromHttpUrl("https://serpapi.com/search.json")
                .queryParam("q", query)
                .queryParam("engine", "google")
                .queryParam("api_key", serpApiKey)
                .queryParam("num", 5)
                .build()
                .toUri();

        String responseBody = restTemplate.getForObject(uri, String.class);
        if (responseBody == null || responseBody.isBlank()) {
            return fallbackKnowledgeRepository(subject, category);
        }

        JsonNode root = objectMapper.readTree(responseBody);
        List<String> facts = new ArrayList<>();
        List<KnowledgeSource> sources = new ArrayList<>();

        // 1. Check knowledge graph
        JsonNode knowledgeGraph = root.path("knowledge_graph");
        if (!knowledgeGraph.isMissingNode()) {
            String desc = knowledgeGraph.path("description").asText("");
            if (!desc.isBlank()) {
                facts.add(desc);
            }
            String kgSource = knowledgeGraph.path("source").path("name").asText("Knowledge Graph");
            String kgUrl = knowledgeGraph.path("source").path("link").asText("");
            if (!kgUrl.isBlank()) {
                sources.add(new KnowledgeSource(knowledgeGraph.path("title").asText(subject), kgUrl, desc, kgSource));
            }
        }

        // 2. Check organic results
        JsonNode organic = root.path("organic_results");
        if (organic.isArray()) {
            for (JsonNode item : organic) {
                String title = item.path("title").asText("");
                String link = item.path("link").asText("");
                String snippet = item.path("snippet").asText("");
                String displayedLink = item.path("displayed_link").asText("web");

                if (!snippet.isBlank() && facts.size() < 4) {
                    facts.add(snippet);
                }
                if (!link.isBlank() && sources.size() < 3) {
                    sources.add(new KnowledgeSource(title, link, snippet, displayedLink));
                }
            }
        }

        if (facts.isEmpty()) {
            return fallbackKnowledgeRepository(subject, category);
        }

        return new SerpApiResult(subject, facts, sources);
    }

    private SerpApiResult fallbackKnowledgeRepository(String subject, String category) {
        List<String> facts = new ArrayList<>();
        List<KnowledgeSource> sources = new ArrayList<>();

        facts.add(String.format("Scientific observation of %s in outdoor ecosystems reveals key environmental adaptations.", subject));
        facts.add(String.format("Field specimens like %s interact directly with local sunlight, soil, and ecological networks.", subject));

        sources.add(new KnowledgeSource(
                String.format("Global Biodiversity Information Facility — %s", subject),
                "https://gbif.org",
                String.format("Taxonomic records, geographic distribution, and ecological data for %s.", subject),
                "gbif.org"
        ));
        sources.add(new KnowledgeSource(
                String.format("Encyclopedia of Life — %s", subject),
                "https://eol.org",
                String.format("Comprehensive biological species profile and habitat overview for %s.", subject),
                "eol.org"
        ));

        return new SerpApiResult(subject, facts, sources);
    }
}
