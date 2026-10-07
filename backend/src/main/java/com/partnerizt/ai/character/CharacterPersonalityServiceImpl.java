package com.partnerizt.ai.character;

import com.partnerizt.ai.gemma.GemmaVisionResult;
import com.partnerizt.ai.serpapi.SerpApiResult;
import com.partnerizt.model.Companion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CharacterPersonalityServiceImpl implements CharacterPersonalityService {

    @Override
    public CharacterDiscoveryOutput formatDiscoveryOutput(Companion companion, GemmaVisionResult vision, SerpApiResult serpApi) {
        String companionId = (companion != null ? companion.getId().toLowerCase() : "flora");
        String name = (companion != null ? companion.getName() : "Flora");
        String subject = vision.getName();
        String sciName = vision.getScientificName();

        List<String> rawFacts = (serpApi != null && serpApi.getFacts() != null && !serpApi.getFacts().isEmpty())
                ? serpApi.getFacts()
                : List.of("Outdoor observation builds stronger long-term memory than indoor screen study.");

        List<String> facts = new ArrayList<>();
        for (String f : rawFacts) {
            String cf = cleanFactSnippet(f);
            if (!cf.isBlank()) facts.add(cf);
        }
        if (facts.isEmpty()) {
            facts.add("Outdoor observation builds stronger long-term memory than indoor screen study.");
        }

        String firstFact = facts.get(0);

        CharacterDiscoveryOutput out = new CharacterDiscoveryOutput();
        out.setCharacterName(name);
        out.setCuratedFacts(facts);

        switch (companionId) {
            case "birdo":
                out.setDomainTitle("Wildlife & Fauna");
                out.setCommentary(String.format("You found %s! 🐦 %s",
                        subject,
                        firstFact
                ));
                out.setDetailedExplanation(String.format(
                        "Great eyes out there! %s (%s) is an active species in this environment. %s",
                        subject, sciName, vision.getDescription()
                ));
                out.setSuggestedOutdoorChallenge(
                        "Here's a field challenge: Next time you're outside, watch it quietly for 30 seconds and note if other birds or animals react to its calls or movements!"
                );
                break;

            case "flora":
                out.setDomainTitle("Botany & Dendrology");
                out.setCommentary(String.format("Wonderful botanical find! 🌿 %s", firstFact));
                out.setDetailedExplanation(String.format(
                        "Notice the botanical traits of %s (%s). %s",
                        subject, sciName, vision.getDescription()
                ));
                out.setSuggestedOutdoorChallenge(
                        "Examine the leaf structure gently against the sky to trace its secondary venation corridors from petiole to margin."
                );
                break;

            case "atlas":
                out.setDomainTitle("Architecture & Heritage");
                out.setCommentary(String.format("A true triumph of craftsmanship! 🏛️ %s", firstFact));
                out.setDetailedExplanation(String.format(
                        "Look closely at the historic engineering of this %s. %s",
                        subject, vision.getDescription()
                ));
                out.setSuggestedOutdoorChallenge(
                        "Take a step back and examine the masonry bond pattern and how gravity is channeled down into the foundation."
                );
                break;

            case "munch":
                out.setDomainTitle("Food Culture & Ethnobotany");
                out.setCommentary(String.format("Look at that delicious botanical marvel! 🫐 %s", firstFact));
                out.setDetailedExplanation(String.format(
                        "From historical cooking to botanical adaptations, %s has fascinating cultural roots! %s",
                        subject, vision.getDescription()
                ));
                out.setSuggestedOutdoorChallenge(
                        "Gently brush your hand against the foliage to observe its natural aromatic essential oils without picking the plant."
                );
                // Critical Safety Disclaimer for Food/Foraging
                out.setSafetyDisclaimer(
                        "⚠️ Foraging Safety Note: Never ingest, pick, or consume wild plants, mushrooms, or berries based solely on AI identification! Always consult a certified local botanist."
                );
                break;

            case "nova":
            default:
                out.setDomainTitle("Geology & Earth Systems");
                out.setCommentary(String.format("Incredible geological find! ✨ %s", firstFact));
                out.setDetailedExplanation(String.format(
                        "You are holding deep time in your hands with this %s (%s). %s",
                        subject, sciName, vision.getDescription()
                ));
                out.setSuggestedOutdoorChallenge(
                        "Compare this specimen to a nearby stone to test grain texture, mineral flecks, and differential weathering."
                );
                break;
        }

        return out;
    }

    @Override
    public String generateChatReply(Companion companion, String userMessage, List<com.partnerizt.model.ChatMessage> history, SerpApiResult serpApi) {
        String companionId = (companion != null ? companion.getId().toLowerCase() : "flora");
        String lower = (userMessage != null ? userMessage.toLowerCase().trim() : "");

        String rawFact = (serpApi != null && serpApi.getFacts() != null && !serpApi.getFacts().isEmpty())
                ? serpApi.getFacts().get(0)
                : "";
        String factContext = "";
        String cleaned = cleanFactSnippet(rawFact);
        if (!cleaned.isBlank()) {
            factContext = " " + cleaned;
        }

        // Direct topic grounding
        if (lower.contains("crow") || lower.contains("crows") || lower.contains("morning") || lower.contains("noise") || lower.contains("caw")) {
            if ("birdo".equals(companionId)) {
                return "Crows are very social, intelligent birds that gather in large communal roosts overnight! When dawn arrives, their loud morning cawing is essentially a roll call and strategy session to check in with family members, defend territory, and announce good foraging spots.";
            }
        }

        if (lower.contains("snake") || lower.contains("lizard") || lower.contains("reptile")) {
            if ("birdo".equals(companionId)) {
                return "Great observation! Snakes and lizards share similar facial structures because they both belong to the reptile order Squamata. Both feature cranial scales instead of soft skin, flexible kinetic skulls, and Jacobson's organs (sensory pits and forked tongues) used to taste chemical scent particles in the air." + factContext;
            } else {
                return String.format("That's an interesting wildlife question! While my primary focus is %s, snakes and lizards share facial traits because they are both squamate reptiles with cranial scales and specialized olfactory sensory organs.%s", companion != null ? companion.getDomain() : "nature", factContext);
            }
        }

        if (lower.contains("gargoyle") || lower.contains("gargoyles") || lower.contains("grotesque")) {
            return "Historic gargoyles served a clever dual purpose! Beyond their mythical guardian symbolism, they were functional waterspouts engineered to shoot rainwater away from masonry walls to prevent mortar erosion.";
        }

        if (lower.contains("migrate") || lower.contains("migration") || lower.contains("fly south")) {
            return "Bird migration is an epic seasonal journey driven by food availability and daylight! In autumn, declining insect populations and dropping temperatures cue birds to navigate along flyways using celestial stars, landmarks, and geomagnetic orientation." + factContext;
        }

        if (lower.contains("leaf") || lower.contains("leaves") || lower.contains("color") || lower.contains("autumn") || lower.contains("fall")) {
            return "Leaves change color in autumn because shorter days and cooler temperatures cause trees to stop producing green chlorophyll. As the green pigments degrade, the hidden carotenoids (yellow and orange) and anthocyanins (red and purple) shine through!" + factContext;
        }

        if (lower.contains("arch") || lower.contains("arches") || lower.contains("building") || lower.contains("keystone") || lower.contains("masonry")) {
            return "Historic arches are brilliant engineering achievements! By angling tapered voussoir stones toward a central keystone, downward gravity is redirected into lateral compression thrust, allowing masonry arches to support massive loads without steel reinforcement.";
        }

        if (lower.contains("mushroom") || lower.contains("toadstool") || lower.contains("fungi") || lower.contains("death cap") || lower.contains("amanita")) {
            return "⚠️ Foraging Safety Note: Never consume wild mushrooms or unfamiliar fungi based solely on digital identification! Many toxic species look identical to edible ones. Always verify with a certified local mycologist before ingesting any wild specimen." + factContext;
        }

        if (lower.contains("mango")) {
            return "Yes! Green (unripe) mangoes are completely safe and widely eaten across Asian, Latin American, and Caribbean cuisines! They are tangy, firm, and packed with Vitamin C and pectin. They are delicious in salads (like Thai som tum), pickles, chutneys, or sprinkled with salt and chili. Just be sure to rinse any sticky sap from the stem area, as mango sap can irritate sensitive skin." + factContext;
        }

        if (lower.contains("berry") || lower.contains("berries") || lower.contains("forage") || lower.contains("foraging")) {
            return "You can often find wild berries like blackberries and mulberries thriving in urban micro-habitats such as park borders, sunny fence lines, and railway embankments! Just be sure to cross-check identification before foraging.";
        }

        // General companion persona contextual response
        switch (companionId) {
            case "birdo":
                return "That's a great question about wildlife! Animals and birds in our local surroundings continuously adapt their communication, flight paths, and foraging habits to urban spaces. Observing their daily routines teaches us how they thrive right alongside our neighborhoods!";
            case "flora":
                return "Botanical life in your local ecosystem adapts continuously to sunlight, soil moisture, and seasonal patterns! Examining bark textures, leaf venation, and root systems unlocks the living secrets of plant biology.";
            case "atlas":
                return "Architectural engineering and historic craftsmanship turn natural stone, brick, and timber into enduring human monuments! Examining how structural weight is carried into the ground reveals centuries of builders' ingenuity.";
            case "munch":
                return "Wild and cultivated edible plants connect cultural history, culinary science, and seasonal ecology! Exploring botanical ingredients shows how different cultures utilize nature's pantry for nourishment and flavor.";
            case "nova":
            default:
                return "Geological processes, mineral crystallization, and tectonic dynamics explain how landscapes and stones evolve over millions of years! Every rock and soil layer holds a chapter of planetary history.";
        }
    }


    private String cleanFactSnippet(String raw) {
        if (raw == null || raw.isBlank()) return "";
        String text = raw.trim();

        // Strip trailing ellipsis or dangling dots
        text = text.replaceAll("[\\.\\s…]+$", "").trim();

        // If the snippet was cut off mid-sentence (ends without a complete clause), find the last sentence terminator
        int lastSentenceEnd = -1;
        for (int i = text.length() - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '.' || c == '!' || c == '?') {
                lastSentenceEnd = i;
                break;
            }
        }

        if (lastSentenceEnd != -1 && lastSentenceEnd >= 25) {
            text = text.substring(0, lastSentenceEnd + 1).trim();
        } else {
            if (!text.endsWith(".") && !text.endsWith("!") && !text.endsWith("?")) {
                text = text + ".";
            }
        }

        return text;
    }
}

