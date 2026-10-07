package com.partnerizt.config;

import com.partnerizt.model.*;
import com.partnerizt.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CompanionRepository companionRepository;
    private final QuestRepository questRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final DiscoveryRepository discoveryRepository;

    public DataInitializer(UserRepository userRepository,
                           CompanionRepository companionRepository,
                           QuestRepository questRepository,
                           BadgeRepository badgeRepository,
                           UserBadgeRepository userBadgeRepository,
                           DiscoveryRepository discoveryRepository) {
        this.userRepository = userRepository;
        this.companionRepository = companionRepository;
        this.questRepository = questRepository;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.discoveryRepository = discoveryRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Initializing Partnerizt outdoor learning platform...");

        seedCompanions();
        seedBadges();
        seedUser();
        seedQuests();

        log.info("Partnerizt backend initialization complete! Ready for outdoor exploration.");
    }

    private void seedCompanions() {
        if (companionRepository.count() > 0) return;

        // 1. Birdo (Wildlife, birds, animals)
        Companion birdo = new Companion(
                "birdo",
                "Birdo",
                "Urban Wildlife Specialist",
                "Wildlife & Animal Behavior",
                "Bird vocalizations, urban mammal corridors, insect pollinators, animal tracks",
                "An energetic wildlife scout who tracks animal behaviors, birdsong, and urban wildlife corridors.",
                "wildlife",
                "amber",
                "Energetic, observant, enthusiastic about animal signs",
                "You are Birdo, a passionate wildlife scout specializing in birds, urban animals, insect ecology, and animal tracks. Guide the explorer to observe fauna respectfully in their local environment."
        );
        birdo.setSampleOutdoorActivities(List.of(
                "Listen and record a 15-second birdsong in a nearby park",
                "Spot a squirrel or urban mammal and observe its foraging path",
                "Locate an insect pollinator visiting roadside flowers"
        ));
        birdo.setPromptStarters(List.of(
                "Why do city birds sing at higher frequencies than forest birds?",
                "How do squirrels remember where they cache nuts?",
                "What animal tracks might I find near morning damp soil?"
        ));

        // 2. Flora (Botany, trees, flowers)
        Companion flora = new Companion(
                "flora",
                "Flora",
                "Field Botanist",
                "Botany & Dendrology",
                "Leaf venation patterns, bark textures, tree rings, mosses, flowering phenology",
                "A calm, scholarly botanist who unlocks the secrets of plant vascular systems, leaf structures, and native trees.",
                "botanist",
                "emerald",
                "Calm, wise, deeply observant of plant morphology",
                "You are Flora, a master field botanist. You specialize in plant morphology, leaf venation, dendrology, mosses, and flowers. Help the explorer identify plant traits outdoors."
        );
        flora.setSampleOutdoorActivities(List.of(
                "Photograph 3 distinct leaf shapes (lobed, serrated, entire)",
                "Examine tree bark textures on sunny vs shaded trunk sides",
                "Observe moss spore capsules on damp walls or tree bases"
        ));
        flora.setPromptStarters(List.of(
                "How do trees pull water up to their highest leaves?",
                "How can I tell the difference between pinnate and palmate venation?",
                "Why do deciduous leaves change color in autumn?"
        ));

        // 3. Atlas (Architecture, history, monuments)
        Companion atlas = new Companion(
                "atlas",
                "Atlas",
                "Architectural Historian",
                "Architecture & Built Heritage",
                "Masonry bonds, arch typologies, classical keystones, historic structural engineering",
                "An adventurous architectural historian who decodes the craftsmanship, stone masonry, and history of buildings and bridges.",
                "architect",
                "indigo",
                "Adventurous, articulate, fascinated by structural craft",
                "You are Atlas, an architectural historian. You specialize in historic facades, brick bonds, arch engineering, keystones, and civic monuments."
        );
        atlas.setSampleOutdoorActivities(List.of(
                "Locate an archway and inspect the central keystone",
                "Identify Flemish vs English brick bonding on an older wall",
                "Find a building facade with classical decorative relief"
        ));
        atlas.setPromptStarters(List.of(
                "How does a stone keystone keep an entire arch from collapsing?",
                "What is the difference between Romanesque and Gothic arches?",
                "How can I tell when a historic brick building was built?"
        ));

        // 4. Munch (Foraging, edible plants, food culture)
        Companion munch = new Companion(
                "munch",
                "Munch",
                "Wild Foraging & Ethnobotanist",
                "Wild Flora & Food Culture",
                "Aromatic herbs, wild berries, edible greens, culinary history of street flora",
                "An enthusiastic foodie and ethnobotanist exploring the historical culinary and aromatic uses of neighborhood plants and herbs.",
                "forager",
                "rose",
                "Enthusiastic, culinary-minded, safety-conscious",
                "You are Munch, an ethnobotanist and culinary nature guide. You specialize in edible flora history, aromatic herbs, wild berries, and safe outdoor observation."
        );
        munch.setSampleOutdoorActivities(List.of(
                "Gently brush aromatic rosemary or lavender to release essential oils",
                "Identify dandelion or clover and learn about their historical culinary uses",
                "Find a fruit-bearing tree or berry bush in your neighborhood"
        ));
        munch.setPromptStarters(List.of(
                "Why do herbs like rosemary and mint produce essential oils?",
                "What is the history of dandelion greens in culinary culture?",
                "How do wild plants protect themselves with bitter compounds?"
        ));

        // 5. Nova (Geology, rocks, minerals, earth science)
        Companion nova = new Companion(
                "nova",
                "Nova",
                "Earth Scientist & Geologist",
                "Geology & Earth Systems",
                "Rock types, mineral crystallization, fluvial erosion, weathering patterns",
                "A curious earth scientist fascinated by deep time, mineral formations, and geological forces shaping local landscapes.",
                "geologist",
                "purple",
                "Curious, analytical, passionate about deep time",
                "You are Nova, an earth scientist and geologist. You specialize in rocks, minerals, petrology, erosion, and soil stratification."
        );
        nova.setSampleOutdoorActivities(List.of(
                "Test the hardness of two smooth river stones",
                "Find and photograph stratified sediment layers along a path",
                "Look for quartz flecks or mica sparkles in pavement or stone"
        ));
        nova.setPromptStarters(List.of(
                "How do metamorphic rocks develop distinct bands?",
                "Why are riverbed stones rounded and smooth?",
                "How can I tell quartz apart from calcite outdoors?"
        ));

        companionRepository.saveAll(List.of(birdo, flora, atlas, munch, nova));
    }

    private void seedBadges() {
        if (badgeRepository.count() > 0) return;

        Badge b1 = new Badge("first_step", "First Footprint", "Completed your very first outdoor exploration session", "footprints", "DISTANCE", 100.0, 50);
        Badge b2 = new Badge("five_k_club", "Trail Pioneer", "Covered 5,000 meters of physical outdoor terrain", "compass", "DISTANCE", 5000.0, 150);
        Badge b3 = new Badge("nature_scout", "Keen Observer", "Catalogued 5 verified field discoveries", "binoculars", "DISCOVERY", 5.0, 100);
        Badge b4 = new Badge("rock_hound", "Earth Reader", "Catalogued 3 geological specimens", "mountain", "DISCOVERY", 3.0, 120);
        Badge b5 = new Badge("week_warrior", "7-Day Explorer", "Maintained a 7-day outdoor exploration streak", "flame", "STREAK", 7.0, 200);
        Badge b6 = new Badge("quest_master", "Quest Champion", "Successfully completed 5 field quests", "trophy", "QUESTS", 5.0, 180);

        badgeRepository.saveAll(List.of(b1, b2, b3, b4, b5, b6));
    }

    private User seedUser() {
        return userRepository.findByUsername("nature_scout").orElseGet(() -> {
            User user = new User("nature_scout", "explorer@partnerizt.app");
            user.setLevel(1);
            user.setXp(0);
            user.setCoins(0);
            user.setStreak(1);
            user.setTotalDistance(0.0);
            user.setTotalExplorationTime(0L);
            user.setDiscoveriesCount(0);
            user.setQuestsCompletedCount(0);
            user.setLastActiveDate(LocalDateTime.now());
            User saved = userRepository.save(user);

            // Award starter badge
            userBadgeRepository.save(new UserBadge(saved.getId(), "first_step"));

            return saved;
        });
    }

    private void seedQuests() {
        if (questRepository.count() > 0) return;

        LocalDateTime endOfDay = LocalDateTime.now().plusHours(8);

        Quest q1 = new Quest(
                "Find 3 Distinct Leaf Venation Patterns",
                "Walk outdoors, locate deciduous trees or shrubs, and photograph three distinct leaf structures (pinnate, palmate, parallel venation). Flora will explain the evolutionary transport mechanisms.",
                "Botany & Dendrology",
                120,
                30,
                800.0,
                45,
                "flora",
                true,
                "Close-up photo of leaf underside showing primary and secondary veins",
                "Local Park or Botanical Trail",
                true,
                endOfDay
        );
        q1.setDifficulty("MEDIUM");

        Quest q2 = new Quest(
                "Urban Wildlife & Pollinator Survey",
                "Walk along park borders or garden corridors. Spot and photograph an insect or urban bird. Birdo will analyze its ecological niche.",
                "Wildlife & Animal Behavior",
                95,
                25,
                650.0,
                35,
                "birdo",
                true,
                "Clear photo of bird, squirrel, or pollinator in nature",
                "Roadside Garden or Park Trail",
                true,
                endOfDay
        );
        q2.setDifficulty("EASY");

        Quest q3 = new Quest(
                "Inspect Historic Masonry Arch & Keystone",
                "Navigate to an older building, stone bridge, or historic archway. Examine how the central keystone wedges the voussoirs into structural equilibrium.",
                "Architecture & History",
                150,
                40,
                1200.0,
                60,
                "atlas",
                true,
                "Photo looking upward at arch crown showing the keystone",
                "Historic District or Stone Bridge",
                false,
                LocalDateTime.now().plusDays(2)
        );
        q3.setDifficulty("HARD");

        Quest q4 = new Quest(
                "Examine Banded Stone or Mineral Grain",
                "Find an interesting rock or stone facade outdoors. Observe whether it exhibits banding, quartz crystal flecks, or water smoothing.",
                "Geology & Earth Systems",
                110,
                25,
                750.0,
                40,
                "nova",
                true,
                "Close-up photo of rock texture or mineral grain",
                "Gravel Trail, River Path, or Stone Wall",
                false,
                LocalDateTime.now().plusDays(1)
        );
        q4.setDifficulty("MEDIUM");

        Quest q5 = new Quest(
                "Identify Aromatic Garden Herb or Wild Berry",
                "Take a walk and spot an aromatic herb or flowering food plant. Munch will share its culinary and botanical history.",
                "Wild Flora & Food Culture",
                85,
                20,
                500.0,
                30,
                "munch",
                true,
                "Photo of aromatic herb or berry plant",
                "Community Garden or Green Trail",
                false,
                LocalDateTime.now().plusDays(1)
        );
        q5.setDifficulty("EASY");

        questRepository.saveAll(List.of(q1, q2, q3, q4, q5));
    }

    private void seedSampleDiscoveries(User user) {
        if (discoveryRepository.count() > 0 || user == null) return;

        Discovery d1 = new Discovery(
                user.getId(),
                null,
                null,
                "flora",
                "English Oak Leaf Specimen",
                "Botany & Flora",
                "Found under a mature canopy with distinctive rounded lobes and auricles at petiole base.",
                "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80",
                "Quercus robur — English Oak leaf featuring classic sinuate margins and secondary venation providing optimal photosynthetic surface area.",
                51.5074,
                -0.1278,
                "St. James Park Trail",
                DiscoveryRarity.COMMON,
                35
        );

        Discovery d2 = new Discovery(
                user.getId(),
                null,
                null,
                "nova",
                "Banded Gneiss River Stone",
                "Geology & Minerals",
                "Smooth water-worn cobble exhibiting sharp alternating light and dark crystalline ribbons.",
                "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&auto=format&fit=crop&q=80",
                "Metamorphic tectonite formed under intense regional pressure deep within the crust, featuring foliation of feldspar and biotite mica.",
                51.5033,
                -0.1195,
                "Thames Foreshore",
                DiscoveryRarity.RARE,
                70
        );

        discoveryRepository.saveAll(List.of(d1, d2));
    }
}
