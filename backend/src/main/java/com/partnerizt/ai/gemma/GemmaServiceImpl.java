package com.partnerizt.ai.gemma;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.partnerizt.model.ChatMessage;
import com.partnerizt.model.Companion;
import com.partnerizt.model.SenderType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class GemmaServiceImpl implements GemmaService {

    private static final Logger log = LoggerFactory.getLogger(GemmaServiceImpl.class);

    @Value("${ai.gemma.api-key:}")
    private String gemmaApiKey;

    @Value("${ai.gemma.api-endpoint:https://generativelanguage.googleapis.com/v1beta}")
    private String gemmaApiEndpoint;

    @Value("${ai.gemma.model-name:gemma-4-26b-a4b-it}")
    private String gemmaModelName;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GemmaServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // Constructor for dependency injection and testing
    public GemmaServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        if (this.gemmaApiKey == null || this.gemmaApiKey.isBlank()) {
            String keyFromEnv = findEnvVariable("GEMMA_API_KEY");
            if (keyFromEnv != null && !keyFromEnv.isBlank()) {
                this.gemmaApiKey = keyFromEnv;
                log.info("[GEMMA_CONFIG] Loaded GEMMA_API_KEY from environment/.env file");
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
                Path.of(System.getProperty("user.dir", "."), "..", ".env"),
                Path.of("backend", ".env")
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
    public GemmaVisionResult identifyImage(String imageData, String contextHint) {
        return identifyImage(imageData, contextHint, "req_" + UUID.randomUUID().toString().substring(0, 8));
    }

    @Override
    public GemmaVisionResult identifyImage(String imageData, String contextHint, String requestId) {
        if (imageData == null || imageData.trim().isEmpty()) {
            return new GemmaVisionResult("Unknown Subject", "N/A", "Unknown", 0.0, "No image data provided.");
        }

        String actualModel = (gemmaModelName != null && !gemmaModelName.isBlank()) ? gemmaModelName.trim() : "gemma-4-31b-it";
        String reqId = (requestId != null && !requestId.isBlank()) ? requestId : "req_" + UUID.randomUUID().toString().substring(0, 8);

        if (gemmaApiKey != null && !gemmaApiKey.isBlank()) {
            try {
                log.info("[GEMMA_CALLED] Mode: REAL_GEMMA");
                log.info("[GEMMA_CALL] requestId={} mode=REAL_GEMMA", reqId);
                log.info("[GEMMA_REQUEST] model={}", actualModel);

                // Run minimal text-only control test for connectivity verification
                runControlTextTest(actualModel, reqId);

                // Execute primary image + text identification
                GemmaVisionResult realResult = callNativeGemmaVisionApi(imageData, contextHint, actualModel, reqId);
                log.info("[GEMMA_RESULT] requestId={} name={} confidence={}", reqId, realResult.getName(), realResult.getConfidence());
                return realResult;
            } catch (Exception e) {
                log.warn("[GEMMA_FALLBACK] requestId={} reason={}", reqId, e.getMessage());

                // Fallback attempt to alternate Gemma 4 candidate (gemma-4-26b-a4b-it) if primary candidate fails
                if (!"gemma-4-26b-a4b-it".equalsIgnoreCase(actualModel)) {
                    try {
                        log.info("[GEMMA_RETRY] Attempting supported alternate candidate: gemma-4-26b-a4b-it");
                        GemmaVisionResult altResult = callNativeGemmaVisionApi(imageData, contextHint, "gemma-4-26b-a4b-it", reqId);
                        log.info("[GEMMA_RESULT] requestId={} name={} confidence={}", reqId, altResult.getName(), altResult.getConfidence());
                        return altResult;
                    } catch (Exception altEx) {
                        log.warn("[GEMMA_FALLBACK] Alternate candidate failed: {}", altEx.getMessage());
                    }
                }
            }
        } else {
            log.info("[GEMMA_FALLBACK] requestId={} reason=GEMMA_API_KEY is not configured", reqId);
        }

        GemmaVisionResult fallbackResult = fallbackVisionClassifier(imageData, contextHint, reqId);
        log.info("[GEMMA_RESULT] requestId={} name={} confidence={} (DEVELOPMENT_FALLBACK)",
                reqId, fallbackResult.getName(), fallbackResult.getConfidence());
        return fallbackResult;
    }

    private void runControlTextTest(String modelName, String requestId) {
        try {
            String safeUrl = getSafeEndpointUrl(modelName);
            log.info("[GEMMA_ENDPOINT] {}", safeUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String textPrompt = "Identify the animal in this sentence: A squirrel is sitting on the ground.";
            Map<String, Object> body = Map.of(
                    "contents", List.of(Map.of(
                            "role", "user",
                            "parts", List.of(Map.of("text", textPrompt))
                    )),
                    "generationConfig", Map.of("temperature", 0.7, "maxOutputTokens", 1024)
            );

            String nativeUrl = buildNativeEndpointUrl(modelName);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(nativeUrl, entity, String.class);

            log.info("[GEMMA_CONTROL_TEST] status={} responsePresent={}", response.getStatusCode(), response.getBody() != null);
        } catch (Exception e) {
            log.warn("[GEMMA_CONTROL_TEST] Text-only control test failed: {}", e.getMessage());
        }
    }

    @Override
    public String generateCharacterResponse(Companion companion, String userMessage, List<ChatMessage> history, String contextDiscoveryTitle) {
        String actualModel = (gemmaModelName != null && !gemmaModelName.isBlank()) ? gemmaModelName.trim() : "gemma-4-31b-it";
        if (gemmaApiKey != null && !gemmaApiKey.isBlank()) {
            try {
                log.info("[GEMMA_CALLED] Mode: REAL_GEMMA (Chat)");
                log.info("[GEMMA_REQUEST] model={}", actualModel);
                return callNativeGemmaChatApi(companion, userMessage, history, contextDiscoveryTitle, actualModel);
            } catch (Exception e) {
                log.warn("[GEMMA_FALLBACK] Primary model failed for chat: {}. Retrying with candidate gemma-4-26b-a4b-it", e.getMessage());
                if (!"gemma-4-26b-a4b-it".equalsIgnoreCase(actualModel)) {
                    try {
                        return callNativeGemmaChatApi(companion, userMessage, history, contextDiscoveryTitle, "gemma-4-26b-a4b-it");
                    } catch (Exception ex2) {
                        log.warn("[GEMMA_FALLBACK] Candidate chat model failed: {}", ex2.getMessage());
                    }
                }
            }
        }
        return null;
    }

    private String callNativeGemmaChatApi(Companion companion, String userMessage, List<ChatMessage> history, String contextDiscoveryTitle, String modelName) throws Exception {
        String nativeUrl = buildNativeEndpointUrl(modelName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String compName = (companion != null) ? companion.getName() : "Companion";
        String systemPrompt = (companion != null) ? companion.getSystemPrompt() : "You are a helpful outdoor learning companion.";
        if (contextDiscoveryTitle != null && !contextDiscoveryTitle.isBlank()) {
            systemPrompt += " The explorer recently discovered: " + contextDiscoveryTitle + ".";
        }

        List<Map<String, Object>> contents = new ArrayList<>();

        if (history != null && !history.isEmpty()) {
            // Include recent history (up to last 6 messages)
            int startIdx = Math.max(0, history.size() - 6);
            for (int i = startIdx; i < history.size(); i++) {
                ChatMessage msg = history.get(i);
                // Skip if this message is identical to the current userMessage to avoid duplicate turns
                if (i == history.size() - 1 && msg.getMessage().trim().equalsIgnoreCase(userMessage.trim())) {
                    continue;
                }
                String role = msg.getSender() == SenderType.USER ? "user" : "model";
                contents.add(Map.of("role", role, "parts", List.of(Map.of("text", msg.getMessage()))));
            }
        }

        // Current user question turn
        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userMessage))
        ));

        String systemInstructionText = String.format(
                "You are %s. %s Answer the explorer's question directly in character in 1 to 2 friendly, conversational sentences. " +
                "IMPORTANT: Output ONLY your direct spoken words. Do NOT include any checklists, self-critique, validation questions, multiple options, bullet points, meta thinking, or quotation marks.",
                compName, systemPrompt
        );

        Map<String, Object> body = new HashMap<>();
        body.put("systemInstruction", Map.of("parts", List.of(Map.of("text", systemInstructionText))));
        body.put("contents", contents);
        body.put("generationConfig", Map.of(
                "temperature", 0.7,
                "maxOutputTokens", 600
        ));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(nativeUrl, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode candidatesNode = root.path("candidates");
            if (candidatesNode.isArray() && !candidatesNode.isEmpty()) {
                JsonNode partsNode = candidatesNode.get(0).path("content").path("parts");
                StringBuilder fullTextBuilder = new StringBuilder();
                if (partsNode.isArray()) {
                    for (JsonNode part : partsNode) {
                        if (part.has("text")) {
                            fullTextBuilder.append(part.path("text").asText()).append("\n");
                        }
                    }
                }
                String text = fullTextBuilder.toString().trim();
                if (!text.isBlank()) {
                    log.info("[GEMMA_CHAT_RAW] {}", text);
                    String sanitized = sanitizeChatReply(text, compName);
                    log.info("[GEMMA_CHAT_SANITIZED] {}", sanitized);
                    return sanitized;
                }
            }
        }

        throw new RuntimeException("Gemma 4 chat API returned empty or invalid response.");
    }

    private String sanitizeChatReply(String rawText, String companionName) {
        if (rawText == null || rawText.isBlank()) return rawText;
        String text = rawText.trim();

        // 0. If the model output is an internal planning scratchpad with multiple candidate quotes, extract the best dialogue quote
        if (text.contains("Character Persona:") || text.contains("Constraints:") || text.contains("Topic:") ||
            text.contains("Locations:") || text.contains("Sentence 1:") || text.contains("Check constraints") ||
            text.toLowerCase().contains("wait, let's") || text.toLowerCase().contains("actually,")) {
            java.util.regex.Pattern candPattern = java.util.regex.Pattern.compile("[\"“]([A-Z][^\"”]{30,}?[.!?])[\"”]");
            java.util.regex.Matcher candMatcher = candPattern.matcher(text);
            List<String> candidates = new ArrayList<>();
            while (candMatcher.find()) {
                String c = candMatcher.group(1).trim();
                String lowerC = c.toLowerCase();
                if (!lowerC.contains("sentence") && !lowerC.contains("constraint") &&
                    !lowerC.contains("direct answer") && !lowerC.contains("checklist") &&
                    !lowerC.contains("persona:")) {
                    candidates.add(c);
                }
            }
            if (!candidates.isEmpty()) {
                return candidates.get(candidates.size() - 1);
            }
        }

        // 1. Strip markdown headers / draft markers like "**Draft 1:**", "Option 1:", etc.
        text = text.replaceAll("(?i)(?:^|\\n)\\s*(?:\\*\\*|\\*|#+)?\\s*(?:Draft|Option|Version|Candidate|Response|Answer|Final\\s*Response|Thinking|Checklist|Self-Evaluation)\\s*\\d*\\s*(?:\\*\\*|\\*|#+)?\\s*:?\\s*", " ");

        // 2. Strip leading / inline checklist Q&A evaluations: "* Direct answer? Yes.", "* Conversational? Yes.", "Friendly? Yes."
        text = text.replaceAll("(?i)(?:^|\\s|\\n)(?:\\*|-)?\\s*[a-zA-Z0-9\\s\\-]+(?:\\?|:)\\s*(?:Yes|No|Checked|Pass|Met|OK|Done|True|False)\\.?(?=\\s|$|\\n|\\*|-|\")", " ");

        // 3. Cut off at any secondary versions, deliberation, or self-critique
        java.util.regex.Pattern cutOffPattern = java.util.regex.Pattern.compile(
                "(?i)([\"']\\s*(Let's|Wait|Check|Count|Option|Draft|Version|Here's|Maybe|How about)|\\b(Let's go with|Let's try|Let's use|Let's refine|Let's make sure|\\*Count:\\*|\\*Wait|\\*Check constraints|\\*Final Polish:|\\*Draft\\s*\\d|\\*Option\\s*\\d|Option\\s*[2-9]:|Draft\\s*[2-9]:)\\b)"
        );
        java.util.regex.Matcher cutMatcher = cutOffPattern.matcher(text);
        if (cutMatcher.find() && cutMatcher.start() > 20) {
            text = text.substring(0, cutMatcher.start()).trim();
        }

        // 4. Handle quoted string followed by unquoted duplicate (or vice versa)
        // e.g. "You'll often find wild berries..." You'll often find wild berries...
        java.util.regex.Pattern quotePattern = java.util.regex.Pattern.compile("^[\"“]([\\s\\S]+?)[\"”](?:\\s*([\\s\\S]*))?$");
        java.util.regex.Matcher quoteMatcher = quotePattern.matcher(text.trim());
        if (quoteMatcher.matches()) {
            String insideQuotes = quoteMatcher.group(1).trim();
            String afterQuotes = (quoteMatcher.group(2) != null) ? quoteMatcher.group(2).trim() : "";
            if (afterQuotes.isBlank() ||
                insideQuotes.startsWith(afterQuotes.substring(0, Math.min(25, afterQuotes.length()))) ||
                afterQuotes.startsWith(insideQuotes.substring(0, Math.min(25, insideQuotes.length())))) {
                text = insideQuotes;
            } else if (afterQuotes.length() > insideQuotes.length()) {
                text = afterQuotes;
            } else {
                text = insideQuotes;
            }
        }

        // 5. Strip metadata, planning scratchpad, and checklist lines
        String[] lines = text.split("\\r?\\n");
        List<String> cleanLines = new ArrayList<>();
        for (String line : lines) {
            String trimmedLine = line.trim().replaceAll("^\\*+\\s*", "");
            String lower = trimmedLine.toLowerCase();
            if (lower.isBlank()) continue;
            if (lower.contains("? yes") || lower.contains("? no") || lower.contains(": yes") || lower.contains(": no")) {
                continue;
            }
            if (lower.startsWith("character persona:") ||
                lower.startsWith("constraints:") ||
                lower.startsWith("topic:") ||
                lower.startsWith("locations:") ||
                lower.startsWith("sentence 1:") ||
                lower.startsWith("sentence 2:") ||
                lower.startsWith("sentence 3:") ||
                lower.startsWith("check constraints") ||
                lower.startsWith("question:") ||
                lower.startsWith("user question:") ||
                lower.startsWith("user asks:") ||
                lower.startsWith("prompt:") ||
                lower.startsWith("persona:") ||
                lower.startsWith("specialization:") ||
                lower.startsWith("tone:") ||
                lower.startsWith("constraint:") ||
                lower.startsWith("role:") ||
                lower.startsWith("task:") ||
                lower.startsWith("guideline:") ||
                lower.startsWith("checklist:") ||
                lower.startsWith("rubric:") ||
                lower.startsWith("criteria:") ||
                lower.startsWith("direct answer") ||
                lower.startsWith("conversational") ||
                lower.startsWith("persona-consistent") ||
                lower.startsWith("system instruction:")) {
                continue;
            }
            cleanLines.add(trimmedLine);
        }
        if (!cleanLines.isEmpty()) {
            text = String.join(" ", cleanLines).trim();
        }

        // 6. Strip companion name prefix if present ("Birdo: ...")
        if (companionName != null && !companionName.isBlank()) {
            String prefix = companionName.trim() + ":";
            if (text.toLowerCase().startsWith(prefix.toLowerCase())) {
                text = text.substring(prefix.length()).trim();
            }
            if (text.startsWith(companionName + " (")) {
                int closeParen = text.indexOf(").");
                if (closeParen != -1 && closeParen + 2 < text.length()) {
                    text = text.substring(closeParen + 2).trim();
                } else {
                    int singleClose = text.indexOf(")");
                    if (singleClose != -1 && singleClose + 1 < text.length()) {
                        text = text.substring(singleClose + 1).trim();
                    }
                }
            }
        }

        // 7. Strip surrounding quotes & extra asterisks/whitespace
        text = text.replaceAll("^[\\\"'“”]+|[\\\"'“”]+$", "").trim();
        text = text.replaceAll("^\\*+|\\*+$", "").trim();
        text = text.replaceAll("\\s+", " ").trim();
        return text;
    }



    private GemmaVisionResult callNativeGemmaVisionApi(String imageData, String contextHint, String modelName, String requestId) throws Exception {
        String prompt = "You are an expert scientific vision classifier for an outdoor exploration and discovery application. " +
                "Carefully inspect the visual features in the image to identify the exact species, animal, bird, plant, flower, tree, rock, mineral, or architectural monument. " +
                "Return a STRICT JSON object with these exact keys: " +
                "\"name\" (common name of the primary subject in the photo), " +
                "\"scientificName\" (binomial or formal scientific name, or N/A), " +
                "\"category\" (one of: animal, bird, plant, architecture, geology, food, discovery), " +
                "\"confidence\" (float between 0.0 and 1.0 representing identification confidence based on image clarity), " +
                "\"description\" (1-2 sentence visual description of distinctive features visible in this photo). " +
                (contextHint != null && !contextHint.isBlank() ? "Context hint: " + contextHint : "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Map<String, Object>> parts = new ArrayList<>();
        parts.add(Map.of("text", prompt));

        String mimeType = "image/jpeg";
        int payloadLength = 0;

        if (imageData.startsWith("http://") || imageData.startsWith("https://")) {
            try {
                byte[] imgBytes = restTemplate.getForObject(imageData, byte[].class);
                if (imgBytes != null && imgBytes.length > 0) {
                    payloadLength = imgBytes.length;
                    String base64Data = Base64.getEncoder().encodeToString(imgBytes).replaceAll("\\s+", "");
                    if (imageData.toLowerCase().contains(".png")) mimeType = "image/png";
                    else if (imageData.toLowerCase().contains(".webp")) mimeType = "image/webp";
                    parts.add(Map.of("inlineData", Map.of("mimeType", mimeType, "data", base64Data)));
                } else {
                    parts.add(Map.of("text", "Context: Analyzing image at URL: " + imageData));
                }
            } catch (Exception e) {
                log.warn("Could not download image from URL: {}", e.getMessage());
                parts.add(Map.of("text", "Context: Analyzing image at URL: " + imageData));
            }
        } else {
            String base64Data = imageData;
            if (imageData.startsWith("data:")) {
                int commaIdx = imageData.indexOf(",");
                String meta = imageData.substring(5, commaIdx);
                if (meta.contains(";")) {
                    mimeType = meta.split(";")[0];
                }
                base64Data = imageData.substring(commaIdx + 1);
            }
            base64Data = base64Data.replaceAll("\\s+", "");
            payloadLength = base64Data.length();
            parts.add(Map.of("inlineData", Map.of("mimeType", mimeType, "data", base64Data)));
        }

        log.info("[GEMMA_IMAGE] mimeType={} payloadPresent=true payloadLength={}", mimeType, payloadLength);
        log.info("[GEMMA_ENDPOINT] {}", getSafeEndpointUrl(modelName));
        log.info("[GEMMA_REQUEST_SCHEMA]\nmodel={}\napiVersion=v1beta\nmimeType={}\nimageBytes={}\nparts={}\ngenerationConfig={temperature=0.7, maxOutputTokens=2048}",
                modelName, mimeType, payloadLength, parts.size());

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", parts
                )),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", 2048
                )
        );

        String nativeUrl = buildNativeEndpointUrl(modelName);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(nativeUrl, entity, String.class);

        log.info("[GEMMA_RESPONSE] requestId={} status={}", requestId, response.getStatusCode());

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode candidatesNode = root.path("candidates");
            if (candidatesNode.isArray() && !candidatesNode.isEmpty()) {
                JsonNode partsNode = candidatesNode.get(0).path("content").path("parts");
                StringBuilder fullTextBuilder = new StringBuilder();
                if (partsNode.isArray()) {
                    for (JsonNode part : partsNode) {
                        if (part.has("text")) {
                            fullTextBuilder.append(part.path("text").asText()).append("\n");
                        }
                    }
                }

                String rawText = fullTextBuilder.toString().trim();
                if (!rawText.isBlank()) {
                    log.info("[GEMMA_RAW_TEXT]\n{}", rawText);
                    GemmaVisionResult parsedResult = parseStructuredJsonOutput(rawText);
                    log.info("[GEMMA_PARSED] requestId={} name={} confidence={}",
                            requestId, parsedResult.getName(), parsedResult.getConfidence());
                    return parsedResult;
                }
            }
        }

        throw new RuntimeException("Gemma 4 vision API returned empty or invalid candidate content.");
    }



    private String getSafeEndpointUrl(String modelName) {
        String base = (gemmaApiEndpoint != null && !gemmaApiEndpoint.isBlank()) ? gemmaApiEndpoint.trim() : "https://generativelanguage.googleapis.com/v1beta";
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        String model = (modelName != null && !modelName.isBlank()) ? modelName.trim() : "gemma-4-31b-it";
        return base + "/models/" + model + ":generateContent";
    }

    private String buildNativeEndpointUrl(String modelName) {
        return getSafeEndpointUrl(modelName) + "?key=" + gemmaApiKey;
    }

    private GemmaVisionResult parseStructuredJsonOutput(String rawText) {
        try {
            String jsonStr = rawText.trim();
            if (jsonStr.contains("{") && jsonStr.contains("}")) {
                int start = jsonStr.indexOf("{");
                int end = jsonStr.lastIndexOf("}");
                jsonStr = jsonStr.substring(start, end + 1);
            }

            JsonNode node = objectMapper.readTree(jsonStr);
            String name = node.path("name").asText("Outdoor Specimen");
            String scientificName = node.path("scientificName").asText("N/A");
            String category = node.path("category").asText("discovery");
            double confidence = node.has("confidence") ? node.path("confidence").asDouble(0.85) : 0.85;
            String description = node.path("description").asText("");

            return new GemmaVisionResult(name, scientificName, category, confidence, description);
        } catch (Exception e) {
            log.warn("[GEMMA_PARSE_ERROR] Failed to parse model response: {}", e.getMessage());
            return new GemmaVisionResult("Uncertain Specimen", "N/A", "discovery", 0.45, rawText);
        }
    }

    private GemmaVisionResult fallbackVisionClassifier(String imageData, String contextHint, String requestId) {
        return new GemmaVisionResult(
                "Uncertain Specimen",
                "N/A",
                "discovery",
                0.45,
                "Development fallback: Real Gemma 4 API unavailable. Please configure GEMMA_API_KEY for live AI identification."
        );
    }
}
