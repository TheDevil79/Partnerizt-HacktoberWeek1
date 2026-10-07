import {
  Character,
  CharacterId,
  CharacterMessage,
  Discovery,
  ExplorationSession,
  IdentificationResult,
  Quest,
  QuestCategory,
  UserStats,
} from '../types';
import {
  MOCK_CHARACTERS,
  MOCK_QUESTS,
  MOCK_USER_STATS,
} from './mockData';

const rawBaseUrl = (import.meta.env.VITE_API_BASE_URL || import.meta.env.VITE_API_URL || 'http://localhost:8080').trim();
// Base URL represents only the backend origin (e.g. "http://localhost:8080" or "https://partnerizt-backend.onrender.com")
const API_BASE_URL = rawBaseUrl.replace(/\/+$/, '').replace(/\/api\/v1$/, '');
const IDENTIFICATION_TIMEOUT_MS = 90000;

// Domain-tailored AI chat responses for resilient offline / fallback mode
const CHARACTER_RESPONSES: Record<
  CharacterId,
  (q: string) => { text: string; activity: string; sources: Array<{ title: string; source: string; url?: string }> }
> = {
  birdo: (q: string) => {
    const qLower = q.toLowerCase();
    if (qLower.includes('snake') || qLower.includes('lizard') || qLower.includes('reptile')) {
      return {
        text: `Snakes and lizards share similar facial structures because they both belong to the order Squamata! Both have kinetic skulls (flexible jaw joints), sensory pits or forked tongues linked to the Jacobson's organ for tasting air molecules, and protective cranial scales instead of soft skin.`,
        activity: `Look closely along warm sunny rocks or tree roots to spot small garden lizards basking to regulate their body temperature!`,
        sources: [
          { title: 'Herpetological Review — Squamate Cranial Anatomy', source: 'herpconservation.org' },
          { title: 'Smithsonian National Zoo — Reptile Biology', source: 'nationalzoo.si.edu' },
        ],
      };
    }
    if (qLower.includes('migrate') || qLower.includes('migration') || qLower.includes('fly south')) {
      return {
        text: `Birds migrate primarily in search of food and nesting grounds! As daylight shortens and seasonal temperatures drop, insects and seeds become scarce, prompting birds to travel along ancient flyways guided by celestial stars, landmarks, and the Earth's magnetic field.`,
        activity: `Look up at dusk to observe if high-flying bird flocks are moving in structured V-formations!`,
        sources: [
          { title: 'Cornell Lab of Ornithology — Bird Migration Guide', source: 'allaboutbirds.org' },
          { title: 'Audubon — Flyways & Migration Routes', source: 'audubon.org' },
        ],
      };
    }
    return {
      text: `Regarding "${q}": Wildlife in your immediate environment adapts remarkably to seasonal shifts, food availability, and urban topography. Observing their behavioral cues teaches us how species thrive alongside human settlements!`,
      activity: `Next time you're outside, watch any wild bird or animal quietly for 30 seconds and note how it surveys its surroundings before moving!`,
      sources: [
        { title: 'Audubon Field Guide — Wildlife Behavior', source: 'audubon.org' },
        { title: 'Cornell Lab of Ornithology — Urban Ecology', source: 'allaboutbirds.org' },
      ],
    };
  },
  flora: (q: string) => {
    const qLower = q.toLowerCase();
    if (
      qLower.includes('mushroom') ||
      qLower.includes('fungi') ||
      qLower.includes('eat') ||
      qLower.includes('edible') ||
      qLower.includes('safe') ||
      qLower.includes('poison') ||
      qLower.includes('toxic')
    ) {
      return {
        text: `⚠️ Wild Foraging Safety Warning: Never eat wild red mushrooms or unfamiliar fungi! Many red-capped mushrooms (like the fly agaric / Amanita muscaria) contain potent toxins and ibotenic acid. While mushrooms are ecologically connected to tree root systems via mycorrhizal networks, wild mushrooms should NEVER be consumed based on visual cues alone.`,
        activity: `Observe the gills and stem ring of the mushroom carefully without touching or harvesting it!`,
        sources: [
          { title: 'Royal Botanic Gardens, Kew — Fungal Biology & Toxicity', source: 'kew.org' },
          { title: 'North American Mycological Association — Field Safety', source: 'namyco.org' },
        ],
      };
    }
    if (
      qLower.includes('leaf') ||
      qLower.includes('leaves') ||
      qLower.includes('color') ||
      qLower.includes('autumn') ||
      qLower.includes('fall')
    ) {
      return {
        text: `Leaves change color in autumn because declining sunlight and cooler temperatures signal deciduous trees to stop producing chlorophyll. As the green pigments decompose, the carotenoids (orange/yellow) and anthocyanins (red/purple) already present in the leaf cells are finally revealed!`,
        activity: `Collect three differently colored fallen leaves and compare the intensity of their leaf veins under a magnifying glass!`,
        sources: [
          { title: 'Royal Botanic Gardens, Kew — Autumn Leaf Chemistry', source: 'kew.org' },
          { title: 'Botany One — Carotenoid & Anthocyanin Dynamics', source: 'botany.one' },
        ],
      };
    }
    if (
      qLower.includes('photosynthesis') ||
      qLower.includes('sunlight') ||
      qLower.includes('tree') ||
      qLower.includes('plant')
    ) {
      return {
        text: `Plants harness sunlight through chlorophyll molecules in their chloroplasts to convert carbon dioxide and water into glucose and oxygen! This miraculous process powers virtually all terrestrial food webs.`,
        activity: `Find a broadleaf plant facing the sun and observe how its leaves are angled to capture maximum solar energy!`,
        sources: [
          { title: 'Royal Botanic Gardens, Kew — Plant Physiology', source: 'kew.org' },
          { title: 'Botany One — Botanical Adaptations', source: 'botany.one' },
        ],
      };
    }
    return {
      text: `Regarding "${q}": Botanical life in your local ecosystem adapts continuously to sunlight, soil moisture, and seasonal patterns. Understanding how plants interact with their environment unlocks the secrets of living nature!`,
      activity: `Take a moment during your walk to notice the texture of the bark and leaf arrangements on the nearest native tree!`,
      sources: [
        { title: 'Royal Botanic Gardens, Kew — Botanical Science', source: 'kew.org' },
        { title: 'Global Biodiversity Information Facility', source: 'gbif.org' },
      ],
    };
  },
  atlas: (q: string) => {
    const qLower = q.toLowerCase();
    if (qLower.includes('taj mahal') || qLower.includes('agra') || qLower.includes('shah jahan')) {
      return {
        text: `The Taj Mahal is approximately 370+ years old! Commissioned in 1632 by Mughal Emperor Shah Jahan as a mausoleum for his beloved wife Mumtaz Mahal, the main marble mausoleum was completed around 1648, with the surrounding complex and minarets finalized by 1653. It is crafted from white Makrana marble featuring intricate pietra dura inlays and symmetrical Indo-Islamic architecture.`,
        activity: `Look up high-resolution architectural plans of the Taj Mahal to admire the perfect octagonal symmetry of its central chamber!`,
        sources: [
          { title: 'UNESCO World Heritage Centre — Taj Mahal', source: 'unesco.org' },
          { title: 'Architectural Heritage Review — Mughal Engineering', source: 'sah.org' },
        ],
      };
    }
    if (qLower.includes('arch') || qLower.includes('arches') || qLower.includes('keystone') || qLower.includes('masonry')) {
      return {
        text: `Historic arches are triumphs of compression physics! By angling voussoir stones inward toward the central keystone, downward gravitational weight is converted into lateral thrust, keeping the structure rock solid for centuries without tensile mortar or steel.`,
        activity: `Walk around an older brick building and count how many different window lintel and masonry styles you can identify!`,
        sources: [
          { title: 'Society of Architectural Historians — Keystone Mechanics', source: 'sah.org' },
          { title: 'Historic England — Masonry Arch Guidelines', source: 'historicengland.org.uk' },
        ],
      };
    }
    return {
      text: `Atlas's Architectural Notes on "${q}": Architectural engineering transforms natural stone, brick, and timber into resilient human monuments that endure across generations!`,
      activity: `Walk around an older building and examine the masonry patterns and how structural weight is carried into the ground!`,
      sources: [
        { title: 'Society of Architectural Historians — Structural Mechanics', source: 'sah.org' },
        { title: 'Historic England — Architectural Heritage Guidelines', source: 'historicengland.org.uk' },
      ],
    };
  },
  munch: (q: string) => {
    const qLower = q.toLowerCase();
    if (
      qLower.includes('preserve') ||
      qLower.includes('preservation') ||
      qLower.includes('ancient') ||
      qLower.includes('refrigerat') ||
      qLower.includes('storage')
    ) {
      return {
        text: `Ancient civilizations preserved fruits using several ingenious methods: drying in the sun (like raisins, figs, and dates), submerging fruits in honey or thick sugar syrups, fermenting them into wines, vinegars, and ciders, or pickling them in salt brine and vinegar! Clay amphorae and sealed subterranean root cellars also kept temperatures low and prevented oxidation.`,
        activity: `Check your pantry for naturally sun-dried fruits (like figs or apricots) and observe how dehydration concentrates natural sugars!`,
        sources: [
          { title: 'Culinary History Society — Ancient Food Preservation', source: 'foodhistory.org' },
          { title: 'FAO — Traditional Food Preservation Methods', source: 'fao.org' },
        ],
      };
    }
    if (
      qLower.includes('mushroom') ||
      qLower.includes('fungi') ||
      qLower.includes('toadstool') ||
      qLower.includes('death cap') ||
      qLower.includes('amanita')
    ) {
      return {
        text: `⚠️ Foraging Safety Rule: Never eat wild mushrooms or unfamiliar fungi based solely on photo or digital ID! Many deadly toxic mushrooms (like Death Caps and toxic red Amanita) look virtually identical to edible field mushrooms. Always consult a certified local mycologist before ingesting any wild find.`,
        activity: `Take a photo of the gills and stem structure from a safe distance without harvesting the mushroom!`,
        sources: [
          { title: 'North American Mycological Association — Field Safety', source: 'namyco.org' },
          { title: 'FDA — Wild Foraged Mushroom Guidelines', source: 'fda.gov' },
        ],
      };
    }
    if (qLower.includes('mango')) {
      return {
        text: `Yes! Green (unripe) mangoes are completely edible, safe, and loved across many world cuisines! They have a tart, crunchy profile and are packed with Vitamin C. They are commonly enjoyed raw with a pinch of salt and chili, shredded into salads (like Thai som tum), or pickled in spicy chutneys. Just remember to wash off any sticky sap from the stem end before slicing!`,
        activity: `Check your kitchen spice rack or local market for amchur (dried green mango powder) used to add natural tanginess to dishes!`,
        sources: [
          { title: 'Culinary Botany — Tropical Fruits & Mangifera indica', source: 'botany.org' },
          { title: 'FAO — Tropical Fruit Postharvest & Nutrition', source: 'fao.org' },
        ],
      };
    }
    return {
      text: `Munch's Culinary Field Notes on "${q}": Wild and cultivated edible plants connect cultural heritage, culinary science, and seasonal ecology! Exploring botanical ingredients reveals how diverse cultures use nature's pantry for nourishment and flavor.`,
      activity: `Take a moment to inspect an aromatic herb or seasonal fruit in your kitchen or garden!`,
      sources: [
        { title: 'Culinary Herb Institute — Botanical Origins', source: 'herbsociety.org' },
        { title: 'FAO — World Spices & Edible Flora', source: 'fao.org' },
      ],
    };
  },
  nova: (q: string) => ({
    text: `Nova's Earth Science Notes on "${q}": Geological formations, mineral crystallization, and tectonic dynamics tell the story of planetary history and deep time!`,
    activity: `Find two contrasting stones outside and test which one leaves a pale scratch on rough concrete!`,
    sources: [
      { title: 'USGS Geology in the Parks — Geomorphology', source: 'usgs.gov' },
      { title: 'Earth Science Online — Mineral Hardness & Weathering', source: 'earthscience.org' },
    ],
  }),
};

class PartneriztApiClient {
  private async fetchApi<T>(endpoint: string, options?: RequestInit, timeoutMs = 30000): Promise<T | null> {
    const isIdentify = endpoint.includes('/discoveries/identify');
    const normalizedEndpoint = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;
    const url = `${API_BASE_URL}/api/v1${normalizedEndpoint}`;

    let isTimedOut = false;
    const controller = new AbortController();
    const timeoutId = setTimeout(() => {
      isTimedOut = true;
      controller.abort();
    }, timeoutMs);

    try {
      if (isIdentify) {
        const bodyObj = options?.body ? JSON.parse(options.body as string) : {};
        const hasImg = Boolean(bodyObj.photoUrl || bodyObj.photoBase64);
        const size = (bodyObj.photoUrl || bodyObj.photoBase64 || '').length;
        console.log(`[IDENTIFY_FRONTEND_START] endpoint=${url} hasImage=${hasImg} payloadSize=${size}`);
      }

      const res = await fetch(url, {
        ...options,
        signal: controller.signal,
        headers: {
          'Content-Type': 'application/json',
          ...(options?.headers || {}),
        },
      });
      clearTimeout(timeoutId);

      if (isIdentify) {
        console.log(`[IDENTIFY_FRONTEND_RESPONSE] status=${res.status} ok=${res.ok}`);
      }

      if (res.ok) {
        const json = await res.json();
        return json.data !== undefined ? json.data : json;
      } else {
        const errText = await res.text();
        if (isIdentify) {
          console.error(`[IDENTIFY_FRONTEND_ERROR] url=${url} status=${res.status} error=${errText}`);
        }
      }
    } catch (e: any) {
      clearTimeout(timeoutId);
      if (isTimedOut) {
        console.warn(`[IDENTIFY_FRONTEND_TIMEOUT] Request to ${url} timed out after ${timeoutMs}ms`);
      } else if (isIdentify) {
        console.error(`[IDENTIFY_FRONTEND_ERROR] url=${url} error=${e?.message || e}`);
      }
      console.warn(`[Partnerizt API] Request to ${url} failed or timed out:`, e);
    }
    return null;
  }

  public async getHomeSummary(): Promise<{
    user: UserStats;
    todayQuest: Quest;
    recentDiscovery?: Discovery;
  }> {
    const remoteUser = await this.fetchApi<any>('/users/stats');
    const remoteDaily = await this.fetchApi<any[]>('/quests/daily');
    const remoteDiscoveries = await this.fetchApi<any[]>('/discoveries');

    if (remoteUser && remoteDaily && remoteDaily.length > 0) {
      const mappedUser: UserStats = {
        id: remoteUser.id || MOCK_USER_STATS.id,
        username: remoteUser.username || MOCK_USER_STATS.username,
        displayName: remoteUser.displayName || MOCK_USER_STATS.displayName,
        level: remoteUser.level || MOCK_USER_STATS.level,
        currentXp: remoteUser.xp || remoteUser.currentXp || MOCK_USER_STATS.currentXp,
        nextLevelXp: remoteUser.xpForNextLevel || remoteUser.nextLevelXp || MOCK_USER_STATS.nextLevelXp,
        coins: remoteUser.coins || MOCK_USER_STATS.coins,
        currentStreak: remoteUser.streak || remoteUser.currentStreak || MOCK_USER_STATS.currentStreak,
        longestStreak: remoteUser.longestStreak || MOCK_USER_STATS.longestStreak,
        totalDistanceKm: Number(((remoteUser.totalDistance || 0) / 1000).toFixed(2)) || MOCK_USER_STATS.totalDistanceKm,
        totalExplorationTimeSeconds: remoteUser.totalExplorationTime || MOCK_USER_STATS.totalExplorationTimeSeconds,
        discoveriesCount: remoteUser.totalDiscoveries || remoteUser.discoveriesCount || MOCK_USER_STATS.discoveriesCount,
        questsCompletedCount: remoteUser.totalQuestsCompleted || remoteUser.questsCompletedCount || MOCK_USER_STATS.questsCompletedCount,
        todayDistanceKm: Number(((remoteUser.todayDistance || 0) / 1000).toFixed(2)) || MOCK_USER_STATS.todayDistanceKm,
        todayExplorationTimeSeconds: remoteUser.todayExplorationTime || MOCK_USER_STATS.todayExplorationTimeSeconds,
        todayTargetDistanceKm: remoteUser.todayTargetDistanceKm || MOCK_USER_STATS.todayTargetDistanceKm,
      };

      const q = remoteDaily[0];
      const companionCategoryMap: Record<string, QuestCategory> = {
        birdo: 'bird',
        flora: 'plant',
        atlas: 'monument',
        munch: 'plant',
        nova: 'discovery',
      };
      const companionId = (q.companionId || 'flora') as CharacterId;

      const mappedQuest: Quest = {
        id: String(q.id),
        title: q.title || MOCK_QUESTS[0].title,
        description: q.description || MOCK_QUESTS[0].description,
        characterId: companionId,
        category: companionCategoryMap[companionId] || 'plant',
        xpReward: q.xpReward || 50,
        coinReward: q.coinReward || 10,
        durationMinutes: q.timeLimitMinutes || q.durationMinutes || 120,
        expiresAt: q.expiresAt || new Date(Date.now() + 120 * 60 * 1000).toISOString(),
        isDaily: q.isDaily ?? true,
        status: (q.status === 'ACTIVE' ? 'in_progress' : q.status === 'COMPLETED' ? 'completed' : 'available'),
        targetHint: q.targetLocation || q.targetHint || 'Look in parks or garden areas',
        iconEmoji: q.iconEmoji || '🌟',
      };

      const firstDisc = remoteDiscoveries && remoteDiscoveries.length > 0 ? remoteDiscoveries[0] : undefined;

      return {
        user: mappedUser,
        todayQuest: mappedQuest,
        recentDiscovery: firstDisc,
      };
    }

    await this.delay(60);
    return {
      user: { ...MOCK_USER_STATS },
      todayQuest: MOCK_QUESTS[0],
      recentDiscovery: undefined,
    };
  }

  public async getQuests(): Promise<Quest[]> {
    const remoteQuests = await this.fetchApi<any[]>('/quests');
    if (remoteQuests && remoteQuests.length > 0) {
      const companionCategoryMap: Record<string, QuestCategory> = {
        birdo: 'bird',
        flora: 'plant',
        atlas: 'monument',
        munch: 'plant',
        nova: 'discovery',
      };
      return remoteQuests.map((q) => {
        const companionId = (q.companionId || 'flora') as CharacterId;
        return {
          id: String(q.id),
          title: q.title,
          description: q.description,
          characterId: companionId,
          category: companionCategoryMap[companionId] || 'plant',
          xpReward: q.xpReward || 50,
          coinReward: q.coinReward || 10,
          durationMinutes: q.timeLimitMinutes || q.durationMinutes || 120,
          expiresAt: q.expiresAt || new Date(Date.now() + 120 * 60 * 1000).toISOString(),
          isDaily: q.isDaily ?? false,
          status: (q.status === 'ACTIVE' ? 'in_progress' : q.status === 'COMPLETED' ? 'completed' : 'available'),
          targetHint: q.targetLocation || q.targetHint || 'Outdoors',
          iconEmoji: q.iconEmoji || '🌟',
        };
      });
    }

    await this.delay(60);
    return [...MOCK_QUESTS];
  }

  public async startExplorationSession(activeQuestId?: string | number): Promise<ExplorationSession | null> {
    const remote = await this.fetchApi<any>('/exploration/sessions/start', {
      method: 'POST',
      body: JSON.stringify({ activeQuestId: activeQuestId ? Number(activeQuestId) : null }),
    });
    if (remote) {
      return {
        id: String(remote.id),
        startTime: remote.startTime || new Date().toISOString(),
        distanceKm: (remote.distanceCovered || 0) / 1000,
        durationSeconds: remote.durationSeconds || 0,
        questsCompletedCount: 0,
        discoveriesCount: remote.discoveriesCount || 0,
        xpEarned: remote.xpEarned || 0,
        coinsEarned: remote.coinsEarned || 0,
        status: 'active',
      };
    }
    return null;
  }

  public async updateExplorationSession(
    sessionId: string | number,
    distanceKm: number,
    durationSeconds: number,
    speed?: number
  ): Promise<void> {
    const numericId = typeof sessionId === 'string' && sessionId.startsWith('session_') ? null : Number(sessionId);
    if (!numericId || isNaN(numericId)) return;

    await this.fetchApi<any>(`/exploration/sessions/${numericId}/update`, {
      method: 'PUT',
      body: JSON.stringify({
        distanceCovered: Math.round(distanceKm * 1000),
        durationSeconds,
        speed,
      }),
    });
  }

  public async endExplorationSession(
    sessionId: string | number,
    distanceKm: number,
    durationSeconds: number
  ): Promise<{ xpEarned: number; coinsEarned: number } | null> {
    const numericId = typeof sessionId === 'string' && sessionId.startsWith('session_') ? null : Number(sessionId);
    if (!numericId || isNaN(numericId)) return null;

    const remote = await this.fetchApi<any>(`/exploration/sessions/${numericId}/complete`, {
      method: 'POST',
      body: JSON.stringify({
        finalDistance: Math.round(distanceKm * 1000),
        finalDurationSeconds: durationSeconds,
      }),
    });

    if (remote) {
      return {
        xpEarned: remote.xpEarned || Math.max(15, Math.round(distanceKm * 40)),
        coinsEarned: remote.coinsEarned || Math.max(5, Math.round(distanceKm * 10)),
      };
    }
    return null;
  }

  public async getActiveExplorationSession(): Promise<ExplorationSession | null> {
    const remote = await this.fetchApi<any>('/exploration/sessions/active');
    if (remote) {
      return {
        id: String(remote.id),
        startTime: remote.startTime || new Date().toISOString(),
        distanceKm: (remote.distanceCovered || 0) / 1000,
        durationSeconds: remote.durationSeconds || 0,
        questsCompletedCount: 0,
        discoveriesCount: remote.discoveriesCount || 0,
        xpEarned: remote.xpEarned || 0,
        coinsEarned: remote.coinsEarned || 0,
        status: 'active',
      };
    }
    return null;
  }

  public async startQuest(questId: string): Promise<Quest> {
    const remote = await this.fetchApi<any>(`/quests/${questId}/start`, { method: 'POST' });
    if (remote) {
      const companionId = (remote.companionId || 'flora') as CharacterId;
      return {
        id: String(remote.id),
        title: remote.title || 'Outdoor Quest',
        description: remote.description || '',
        characterId: companionId,
        category: 'plant',
        xpReward: remote.xpReward || 50,
        coinReward: remote.coinReward || 10,
        durationMinutes: remote.timeLimitMinutes || 120,
        expiresAt: remote.expiresAt || new Date(Date.now() + 120 * 60 * 1000).toISOString(),
        status: 'in_progress',
        isDaily: remote.isDaily ?? false,
        targetHint: remote.targetLocation || 'Outdoors',
        iconEmoji: remote.iconEmoji || '🌟',
      };
    }

    await this.delay(100);
    const q = MOCK_QUESTS.find((item) => item.id === questId);
    if (!q) throw new Error('Quest not found');
    return { ...q, status: 'in_progress' };
  }

  public async completeQuest(
    questId: string,
    photoDataUrl?: string
  ): Promise<{
    quest: Quest;
    discovery: Discovery;
    xpEarned: number;
    coinsEarned: number;
  }> {
    await this.fetchApi<any>(`/quests/${questId}/complete`, {
      method: 'POST',
      body: JSON.stringify({
        photoUrl: photoDataUrl,
        notes: 'Field evidence gathered and verified',
      }),
    });

    const q = MOCK_QUESTS.find((item) => item.id === questId) || MOCK_QUESTS[0];

    const newDiscovery: Discovery = {
      id: `disc_${Date.now()}`,
      title: q.title || 'Outdoor Field Discovery',
      scientificName: undefined,
      category: q.category,
      characterId: q.characterId,
      imageUrl:
        photoDataUrl ||
        (q.category === 'plant'
          ? 'https://images.unsplash.com/photo-1528183429752-a97d0bf99b5a?auto=format&fit=crop&w=600&q=80'
          : 'https://images.unsplash.com/photo-1507667522111-bf5a34e00517?auto=format&fit=crop&w=600&q=80'),
      timestamp: 'Just now',
      locationName: 'Local Explorer Route',
      explanation: `Great job! You found this directly in your environment. ${q.description}`,
      coolFact: 'Real-world exploration creates stronger neural connections than screen-based memorization!',
      xpEarned: q.xpReward,
      coinsEarned: q.coinReward,
      sources: [
        { title: 'Partnerizt Outdoor Knowledge Engine', source: 'partnerizt.app' },
        { title: 'Global Biodiversity Information Facility', source: 'gbif.org' },
      ],
    };

    return {
      quest: { ...q, status: 'completed' },
      discovery: newDiscovery,
      xpEarned: q.xpReward,
      coinsEarned: q.coinReward,
    };
  }

  public async getCharacters(): Promise<Character[]> {
    await this.delay(60);
    return [...MOCK_CHARACTERS];
  }

  public async getCharacterById(id: CharacterId): Promise<Character | undefined> {
    await this.delay(40);
    return MOCK_CHARACTERS.find((c) => c.id === id);
  }

  public async sendCharacterMessage(
    characterId: CharacterId,
    userQuestion: string
  ): Promise<CharacterMessage> {
    const backendCompanionId = characterId;

    // 1. Try Backend Spring Boot API
    const remote = await this.fetchApi<any>(
      `/chat/${backendCompanionId}/send`,
      {
        method: 'POST',
        body: JSON.stringify({ message: userQuestion }),
      },
      30000
    );

    if (remote && remote.companionMessage && remote.companionMessage.message) {
      return {
        id: `msg_${Date.now()}`,
        sender: 'character',
        characterId,
        text: remote.companionMessage.message,
        suggestedActivity: 'Look for this specimen in your next outdoor exploration session!',
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        sourceReferences: [
          { title: `${remote.companionName || characterId} Field Knowledge Base`, source: 'partnerizt.app' },
        ],
      };
    }

    // 2. Direct Live Google AI Generation (Real dynamic intelligence for exact question)
    const candidateKeys = [
      import.meta.env.VITE_GEMMA_API_KEY,
      import.meta.env.VITE_GEMINI_API_KEY,
    ].filter(Boolean) as string[];

    const charPrompts: Record<CharacterId, string> = {
      birdo: 'You are Birdo, a cheerful wildlife biologist who loves exploring local fauna and birds with outdoor adventurers.',
      flora: 'You are Flora, a friendly field botanist who loves sharing fascinating secrets about plants, trees, and wildflowers.',
      atlas: 'You are Atlas, a passionate architectural historian who loves uncovering the craftsmanship and stories of historic buildings.',
      munch: 'You are Munch, an upbeat culinary botanist who loves sharing delicious facts about edible plants, wild berries, and recipes.',
      nova: 'You are Nova, an adventurous geologist who loves explaining the ancient history of rocks, minerals, and landscapes.',
    };

    const charNames: Record<CharacterId, string> = {
      birdo: 'Birdo',
      flora: 'Flora',
      atlas: 'Atlas',
      munch: 'Munch',
      nova: 'Nova',
    };

    const charName = charNames[characterId] || 'Companion';
    const persona = charPrompts[characterId] || charPrompts.birdo;
    const models = ['gemma-4-26b-a4b-it', 'gemma-4-31b-it'];

    const sanitizeCompanionReply = (raw: string): string => {
      let t = raw.trim();

      // If text contains "Answer:" or "Birdo:", extract after it
      if (t.includes('Answer:')) {
        t = t.substring(t.indexOf('Answer:') + 7).trim();
      }

      // If text has metadata lines like "User asks:", "Role:", "Prompt:", "Task:", strip them
      const lines = t.split('\n');
      const cleanLines = lines.filter((line) => {
        const lower = line.trim().toLowerCase();
        return (
          !lower.startsWith('user asks:') &&
          !lower.startsWith('user question:') &&
          !lower.startsWith('question:') &&
          !lower.startsWith('role:') &&
          !lower.startsWith('* role:') &&
          !lower.startsWith('task:') &&
          !lower.startsWith('* task:') &&
          !lower.startsWith('system:') &&
          !lower.startsWith('system instruction:')
        );
      });
      t = cleanLines.join(' ').trim();

      // If there is a trailing checklist (e.g., "* First-person? Yes", "* Friendly/Cheerful? Yes"), cut it off
      const checklistIndex = t.search(/\s\*\s+(First-person|Friendly|Cheerful|Knowledgeable|Safety|Checklist|Criteria|Rubric)\b/i);
      if (checklistIndex > 15) {
        t = t.substring(0, checklistIndex).trim();
      }

      // If the LLM returned draft variations, isolate the final draft
      if (t.includes('Draft 2') || t.includes('Draft 3') || t.includes('Draft 1')) {
        const lastDraft = Math.max(t.lastIndexOf('Draft 2'), t.lastIndexOf('Draft 1'));
        const colon = t.indexOf(':', lastDraft);
        if (colon !== -1 && colon + 1 < t.length) {
          t = t.substring(colon + 1).trim();
          t = t.replace(/^\*+|\*+$/g, '').trim();
        }
      }

      // Remove any leftover prefix like "Birdo:" or "Birdo (cheerful..."
      if (t.toLowerCase().startsWith(charName.toLowerCase() + ':')) {
        t = t.substring(charName.length + 1).trim();
      }
      if (t.startsWith(`${charName} (`)) {
        const closeParen = t.indexOf(').');
        if (closeParen !== -1 && closeParen + 2 < t.length) {
          t = t.substring(closeParen + 2).trim();
        }
      }

      t = t.replace(/^\*+|\*+$/g, '').trim();
      t = t.replace(/^["']|["']$/g, '').trim();
      return t;
    };

    for (const apiKey of candidateKeys) {
      for (const model of models) {
        try {
          const aiRes = await fetch(
            `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${apiKey}`,
            {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({
                contents: [
                  {
                    role: 'user',
                    parts: [
                      {
                        text: `You are ${charName} for the Partnerizt outdoor learning app. ${persona} Always speak directly in first person as ${charName} in 2-3 engaging, conversational sentences. Never repeat the question or add checklists.`,
                      },
                    ],
                  },
                  {
                    role: 'model',
                    parts: [
                      {
                        text: `Understood! I'm ${charName}, ready to help the explorer!`,
                      },
                    ],
                  },
                  {
                    role: 'user',
                    parts: [{ text: userQuestion }],
                  },
                ],
                generationConfig: {
                  temperature: 0.7,
                  maxOutputTokens: 800,
                },
              }),
            }
          );



          if (aiRes.ok) {
            const aiJson = await aiRes.json();
            const replyText = aiJson?.candidates?.[0]?.content?.parts?.[0]?.text;
            if (replyText && replyText.trim().length > 0) {
              const cleanText = sanitizeCompanionReply(replyText);
              if (cleanText.length > 0) {
                return {
                  id: `msg_${Date.now()}`,
                  sender: 'character',
                  characterId,
                  text: cleanText,
                  suggestedActivity: `Ask ${characterId.toUpperCase()} another question or explore outdoors!`,
                  timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
                  sourceReferences: [
                    { title: `${characterId.toUpperCase()} Live AI Engine`, source: 'Google Generative AI' },
                  ],
                };
              }
            }
          }
        } catch (modelErr) {
          console.warn(`[Partnerizt AI] Model ${model} on key failed:`, modelErr);
        }
      }
    }


    // 3. Fallback only if offline / keys unavailable
    await this.delay(350);
    const generator = CHARACTER_RESPONSES[characterId] || CHARACTER_RESPONSES.birdo;
    const { text, activity, sources } = generator(userQuestion);

    return {
      id: `msg_${Date.now()}`,
      sender: 'character',
      characterId,
      text,
      suggestedActivity: activity,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      sourceReferences: sources,
    };
  }

  public async analyzePhotoDiscovery(
    photoFile: File | string,
    questCategory?: string,
    characterId?: CharacterId
  ): Promise<IdentificationResult> {
    const requestId = 'req_' + Math.random().toString(36).substring(2, 9) + '_' + Date.now();
    const isBase64 = typeof photoFile === 'string' && photoFile.startsWith('data:');
    const photoUrl = typeof photoFile === 'string' && !isBase64 ? photoFile : '';
    const photoBase64 = isBase64 ? (photoFile as string) : '';

    console.log(`[IDENTIFY_REQUEST] requestId=${requestId}`);
    console.log(`[IDENTIFY_FRONTEND] sending image=true requestId=${requestId} category=${questCategory || 'none'}`);

    const companionIdMap: Record<string, CharacterId> = {
      birdo: 'birdo',
      flora: 'flora',
      atlas: 'atlas',
      munch: 'munch',
      nova: 'nova',
    };

    const requestedCompanion = characterId || (questCategory === 'bird' || questCategory === 'animal' ? 'birdo' : questCategory === 'monument' ? 'atlas' : questCategory === 'plant' ? 'flora' : 'nova');

    const remote = await this.fetchApi<any>(
      '/discoveries/identify',
      {
        method: 'POST',
        body: JSON.stringify({
          requestId,
          photoUrl,
          photoBase64,
          companionId: requestedCompanion,
          userQuery: questCategory || 'Outdoor specimen',
        }),
      },
      IDENTIFICATION_TIMEOUT_MS
    );

    if (remote) {
      const sources = (remote.sources || []).map((s: any) => ({
        title: s.title || 'Verified Knowledge Source',
        snippet: s.snippet || '',
        source: s.sourceName || 'web',
        url: s.url || '',
      }));

      const resolvedCharId = companionIdMap[characterId || ''] || companionIdMap[remote.category] || requestedCompanion;
      const isIdentified = remote.identified !== false && remote.isIdentified !== false && (remote.confidence === undefined || remote.confidence >= 0.60);

      const frontendResult: IdentificationResult = {
        name: remote.identifiedTitle || remote.name || 'Outdoor Specimen',
        scientificName: remote.scientificName,
        category: (remote.category || questCategory || 'discovery') as any,
        confidence: typeof remote.confidence === 'number' ? remote.confidence : 0.50,
        characterId: resolvedCharId,
        explanation: remote.explanation || remote.companionCommentary || 'Identified outdoor specimen.',
        coolFact: (remote.funFacts && remote.funFacts[0]) || 'Observation directly in the field creates lasting neural memory connections.',
        outdoorTip: remote.companionCommentary || remote.suggestedChallenge || 'Keep observing unique textures in your environment!',
        safetyDisclaimer: remote.safetyDisclaimer,
        isIdentified,
        suggestedChallenge: remote.suggestedChallenge,
        xpValue: remote.xpValue,
        coinsValue: remote.coinsValue,
        domain: remote.domain,
        knowledgeSources: sources.length > 0 ? sources : [
          {
            title: `Field Taxonomy Record — ${remote.domain || 'Partnerizt Science'}`,
            snippet: remote.explanation || '',
            source: 'partnerizt.app',
          },
        ],
      };

      console.log(`[FRONTEND_RESPONSE] requestId=${requestId} name=${frontendResult.name} scientificName=${frontendResult.scientificName || 'N/A'} confidence=${frontendResult.confidence} identified=${frontendResult.isIdentified}`);
      return frontendResult;
    }

    console.warn(`[IDENTIFY_FRONTEND_FALLBACK] reason=Backend unreachable or returned non-2xx status for requestId=${requestId}`);
    await this.delay(600);

    // Development offline / network error fallback: strict uncertain specimen (< 0.60)
    const fallbackResult: IdentificationResult = {
      name: 'Uncertain Specimen',
      category: 'discovery',
      confidence: 0.45,
      characterId: requestedCompanion,
      explanation: 'Could not connect to AI identification service or verify specimen. Please ensure the backend is running on port 8080 and capture a clear photo with good natural lighting.',
      coolFact: 'Clear lighting and close focus help visual models extract critical morphological traits.',
      outdoorTip: 'Try taking a photo with the light source behind you.',
      isIdentified: false,
      knowledgeSources: [],
    };

    console.log(`[FRONTEND_RESPONSE] requestId=${requestId} name=${fallbackResult.name} scientificName=N/A confidence=${fallbackResult.confidence} identified=${fallbackResult.isIdentified}`);
    return fallbackResult;
  }

  public async speakCompanionText(text: string, companion?: string): Promise<Blob | null> {
    if (!text || !text.trim()) return null;

    const url = `${API_BASE_URL}/api/v1/audio/speak`;

    try {
      const res = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Accept: 'audio/mpeg',
        },
        body: JSON.stringify({
          text: text.trim(),
          companion: companion || 'birdo',
        }),
      });

      if (res.ok) {
        const blob = await res.blob();
        if (blob && blob.size > 0) {
          return blob;
        }
      } else {
        console.warn(`[Partnerizt TTS] Endpoint ${url} returned status: ${res.status}`);
      }
    } catch (err) {
      console.warn(`[Partnerizt TTS] Failed calling ${url}:`, err);
    }
    return null;
  }

  public async getProfileData(): Promise<{
    user: UserStats;
    discoveries: Discovery[];
  }> {
    const summary = await this.getHomeSummary();
    const remoteDiscoveries = await this.fetchApi<any[]>('/discoveries');
    return {
      user: summary.user,
      discoveries: remoteDiscoveries || [],
    };
  }

  private delay(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}

export const api = new PartneriztApiClient();

