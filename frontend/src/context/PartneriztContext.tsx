import React, { createContext, useContext, useState, useEffect, useCallback, useRef } from 'react';
import {
  UserStats,
  Quest,
  Discovery,
  Badge,
  ExplorationSession,
  Coordinates,
  CharacterId,
  Character,
} from '../types';
import {
  MOCK_USER_STATS,
  MOCK_QUESTS,
  MOCK_BADGES,
  MOCK_DISCOVERY_GALLERY,
  MOCK_CHARACTERS,
} from '../services/mockData';
import {
  calculateHaversineDistanceKm,
  createInitialSession,
} from '../services/explorationService';
import { soundService } from '../services/soundService';
import { api } from '../services/api';

interface CelebrationData {
  title: string;
  subtitle: string;
  xpEarned: number;
  coinsEarned: number;
  characterId?: CharacterId;
  badgeUnlocked?: Badge;
}

interface PartneriztContextType {
  user: UserStats;
  quests: Quest[];
  badges: Badge[];
  discoveries: Discovery[];
  characters: Character[];
  activeSession: ExplorationSession | null;
  isExploring: boolean;
  gpsCoordinates: Coordinates | null;
  gpsAccuracy: number | null;
  locationPermissionStatus: 'prompt' | 'granted' | 'denied' | 'unavailable';
  isSimulatingWalk: boolean;
  celebration: CelebrationData | null;
  activeChatCharacterId: CharacterId | null;
  soundEnabled: boolean;
  
  // Actions
  startExploration: () => Promise<void>;
  stopExploration: () => Promise<void>;
  toggleSimulateWalk: () => void;
  startQuest: (questId: string) => void;
  completeQuest: (questId: string, photoUrl?: string, customDiscovery?: Partial<Discovery>) => Promise<void>;
  openCharacterChat: (characterId: CharacterId) => void;
  closeCharacterChat: () => void;
  closeCelebration: () => void;
  toggleSound: () => void;
  awardXpAndCoins: (xp: number, coins: number, reasonTitle: string, reasonSubtitle: string, characterId?: CharacterId) => void;
}

const PartneriztContext = createContext<PartneriztContextType | undefined>(undefined);

export const PartneriztProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserStats>(MOCK_USER_STATS);
  const [quests, setQuests] = useState<Quest[]>(MOCK_QUESTS);
  const [badges, setBadges] = useState<Badge[]>(MOCK_BADGES);
  const [discoveries, setDiscoveries] = useState<Discovery[]>(MOCK_DISCOVERY_GALLERY);
  const [characters] = useState<Character[]>(MOCK_CHARACTERS);
  
  const [activeSession, setActiveSession] = useState<ExplorationSession | null>(null);
  const [gpsCoordinates, setGpsCoordinates] = useState<Coordinates | null>(null);
  const [gpsAccuracy, setGpsAccuracy] = useState<number | null>(null);
  const [locationPermissionStatus, setLocationPermissionStatus] = useState<'prompt' | 'granted' | 'denied' | 'unavailable'>('prompt');
  const [isSimulatingWalk, setIsSimulatingWalk] = useState<boolean>(false);
  
  const [celebration, setCelebration] = useState<CelebrationData | null>(null);
  const [activeChatCharacterId, setActiveChatCharacterId] = useState<CharacterId | null>(null);
  const [soundEnabled, setSoundEnabled] = useState<boolean>(true);

  const watchIdRef = useRef<number | null>(null);
  const lastCoordRef = useRef<Coordinates | null>(null);
  const sessionTimerRef = useRef<NodeJS.Timeout | null>(null);
  const walkSimTimerRef = useRef<NodeJS.Timeout | null>(null);
  const backendSyncTimerRef = useRef<NodeJS.Timeout | null>(null);

  const isExploring = activeSession !== null;

  // Sound toggle
  const toggleSound = () => {
    const next = !soundEnabled;
    setSoundEnabled(next);
    soundService.setSoundEnabled(next);
  };

  // XP and Coin progression calculator
  const awardXpAndCoins = useCallback(
    (xp: number, coins: number, title: string, subtitle: string, characterId?: CharacterId) => {
      soundService.playXpGain();
      setUser((prev) => {
        let newXp = prev.currentXp + xp;
        let newLevel = prev.level;
        let nextLevelXp = prev.nextLevelXp;

        if (newXp >= nextLevelXp) {
          newLevel += 1;
          nextLevelXp = Math.round(nextLevelXp * 1.35);
        }

        return {
          ...prev,
          level: newLevel,
          currentXp: newXp,
          nextLevelXp: nextLevelXp,
          coins: prev.coins + coins,
        };
      });

      setCelebration({
        title,
        subtitle,
        xpEarned: xp,
        coinsEarned: coins,
        characterId,
      });
    },
    []
  );

  // Start Exploration Session with Browser Geolocation API
  const startExploration = async () => {
    soundService.playTap();
    
    // Find active quest to link with this session
    const activeQuest = quests.find(q => q.status === 'in_progress');
    
    // 1. Initialize session with backend
    let initialSession: ExplorationSession = createInitialSession();
    try {
      const remoteSession = await api.startExplorationSession(activeQuest ? activeQuest.id : undefined);
      if (remoteSession) {
        initialSession = remoteSession;
      }
    } catch (e) {
      console.warn('[Partnerizt Exploration] Backend start session fallback:', e);
    }

    setActiveSession(initialSession);
    lastCoordRef.current = null;

    if (!navigator.geolocation) {
      setLocationPermissionStatus('unavailable');
      console.warn('Geolocation is not supported by this browser.');
      return;
    }

    try {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          setLocationPermissionStatus('granted');
          const coord: Coordinates = {
            latitude: pos.coords.latitude,
            longitude: pos.coords.longitude,
            accuracy: pos.coords.accuracy,
            timestamp: pos.timestamp,
          };
          setGpsCoordinates(coord);
          setGpsAccuracy(pos.coords.accuracy);
          lastCoordRef.current = coord;
        },
        (err) => {
          if (err.code === 1) { // PERMISSION_DENIED
            setLocationPermissionStatus('denied');
          }
          console.warn('Geolocation initial error:', err.message);
        },
        { enableHighAccuracy: true, timeout: 10000 }
      );

      watchIdRef.current = navigator.geolocation.watchPosition(
        (pos) => {
          setLocationPermissionStatus('granted');
          const newCoord: Coordinates = {
            latitude: pos.coords.latitude,
            longitude: pos.coords.longitude,
            accuracy: pos.coords.accuracy,
            timestamp: pos.timestamp,
          };
          setGpsCoordinates(newCoord);
          setGpsAccuracy(pos.coords.accuracy);

          // Reject highly inaccurate GPS fixes (> 100m) to prevent large erratic jumps
          if (pos.coords.accuracy && pos.coords.accuracy > 100) {
            return;
          }

          if (lastCoordRef.current) {
            const deltaKm = calculateHaversineDistanceKm(lastCoordRef.current, newCoord);
            // Only add if moving above noise threshold (> 5 meters)
            if (deltaKm > 0.005) {
              setActiveSession((prev) =>
                prev
                  ? {
                      ...prev,
                      distanceKm: +(prev.distanceKm + deltaKm).toFixed(3),
                    }
                  : null
              );
              lastCoordRef.current = newCoord;
            }
          } else {
            lastCoordRef.current = newCoord;
          }
        },
        (err) => {
          if (err.code === 1) {
            setLocationPermissionStatus('denied');
          }
          console.warn('Geolocation watch error:', err.message);
        },
        { enableHighAccuracy: true, maximumAge: 3000, timeout: 10000 }
      );
    } catch (e) {
      console.warn('Geolocation initialization error:', e);
    }
  };

  // Stop Exploration Session
  const stopExploration = async () => {
    soundService.playTap();
    if (watchIdRef.current !== null) {
      navigator.geolocation.clearWatch(watchIdRef.current);
      watchIdRef.current = null;
    }
    if (sessionTimerRef.current) {
      clearInterval(sessionTimerRef.current);
      sessionTimerRef.current = null;
    }
    if (walkSimTimerRef.current) {
      clearInterval(walkSimTimerRef.current);
      walkSimTimerRef.current = null;
      setIsSimulatingWalk(false);
    }
    if (backendSyncTimerRef.current) {
      clearInterval(backendSyncTimerRef.current);
      backendSyncTimerRef.current = null;
    }

    if (activeSession) {
      const sessionDist = activeSession.distanceKm;
      const sessionDuration = activeSession.durationSeconds;
      
      let earnedXp = Math.max(15, Math.round(sessionDist * 40));
      let earnedCoins = Math.max(5, Math.round(sessionDist * 10));

      // 1. Sync session completion with backend
      try {
        const completedRemote = await api.endExplorationSession(
          activeSession.id,
          sessionDist,
          sessionDuration
        );
        if (completedRemote) {
          earnedXp = completedRemote.xpEarned;
          earnedCoins = completedRemote.coinsEarned;
        }
      } catch (e) {
        console.warn('[Partnerizt Exploration] Backend end session fallback:', e);
      }

      setUser((prev) => ({
        ...prev,
        totalDistanceKm: +(prev.totalDistanceKm + sessionDist).toFixed(2),
        todayDistanceKm: +(prev.todayDistanceKm + sessionDist).toFixed(2),
        totalExplorationTimeSeconds: prev.totalExplorationTimeSeconds + sessionDuration,
        todayExplorationTimeSeconds: prev.todayExplorationTimeSeconds + sessionDuration,
        currentXp: prev.currentXp + earnedXp,
        coins: prev.coins + earnedCoins,
      }));

      // Check TouchGrass badge progress
      setBadges((prev) =>
        prev.map((b) => {
          if (b.id === 'badge_touch_grass_master') {
            const nextDist = +(user.totalDistanceKm + sessionDist).toFixed(2);
            return {
              ...b,
              progressCurrent: nextDist,
              isUnlocked: nextDist >= (b.progressTarget || 50),
            };
          }
          return b;
        })
      );

      setCelebration({
        title: 'Exploration Completed!',
        subtitle: `You explored ${sessionDist > 0 ? `${sessionDist.toFixed(2)} km` : 'your surroundings'} and absorbed real-world knowledge.`,
        xpEarned: earnedXp,
        coinsEarned: earnedCoins,
        characterId: 'nova',
      });
    }

    setActiveSession(null);
  };

  // Toggle walk simulation (useful when testing indoors or on desktop)
  const toggleSimulateWalk = () => {
    if (!isExploring) return;
    setIsSimulatingWalk((prev) => !prev);
  };

  // Periodic Backend Distance & Duration Synchronization (Every 12s)
  useEffect(() => {
    if (activeSession) {
      backendSyncTimerRef.current = setInterval(() => {
        if (activeSession) {
          api.updateExplorationSession(
            activeSession.id,
            activeSession.distanceKm,
            activeSession.durationSeconds
          );
        }
      }, 12000);
    } else {
      if (backendSyncTimerRef.current) clearInterval(backendSyncTimerRef.current);
    }

    return () => {
      if (backendSyncTimerRef.current) clearInterval(backendSyncTimerRef.current);
    };
  }, [activeSession?.id]);

  // Active Session duration tick
  useEffect(() => {
    if (activeSession) {
      sessionTimerRef.current = setInterval(() => {
        setActiveSession((prev) =>
          prev
            ? {
                ...prev,
                durationSeconds: prev.durationSeconds + 1,
              }
            : null
        );
      }, 1000);
    } else {
      if (sessionTimerRef.current) clearInterval(sessionTimerRef.current);
    }

    return () => {
      if (sessionTimerRef.current) clearInterval(sessionTimerRef.current);
    };
  }, [isExploring]);

  // Walk simulation tick
  useEffect(() => {
    if (isExploring && isSimulatingWalk) {
      walkSimTimerRef.current = setInterval(() => {
        setActiveSession((prev) => {
          if (!prev) return null;
          // Add ~15 meters per 2 seconds (brisk walking pace ~4.5 km/h)
          const addedKm = 0.015;
          return {
            ...prev,
            distanceKm: +(prev.distanceKm + addedKm).toFixed(3),
          };
        });
      }, 2000);
    } else {
      if (walkSimTimerRef.current) clearInterval(walkSimTimerRef.current);
    }

    return () => {
      if (walkSimTimerRef.current) clearInterval(walkSimTimerRef.current);
    };
  }, [isExploring, isSimulatingWalk]);

  // Quest Actions
  const startQuest = (questId: string) => {
    soundService.playTap();
    setQuests((prev) =>
      prev.map((q) => (q.id === questId ? { ...q, status: 'in_progress' } : q))
    );
  };

  const completeQuest = async (questId: string, photoUrl?: string, customDiscovery?: Partial<Discovery>) => {
    soundService.playQuestComplete();
    const targetQuest = quests.find((q) => q.id === questId) || quests[0];

    // Mark quest completed
    setQuests((prev) =>
      prev.map((q) => (q.id === questId ? { ...q, status: 'completed' } : q))
    );

    const xp = customDiscovery?.xpEarned || targetQuest.xpReward;
    const coins = customDiscovery?.coinsEarned || targetQuest.coinReward;

    // Create new Discovery with real identification details if provided
    const newDiscovery: Discovery = {
      id: `disc_${Date.now()}`,
      title: customDiscovery?.title || targetQuest.title || 'Outdoor Field Discovery',
      scientificName: customDiscovery?.scientificName,
      category: customDiscovery?.category || targetQuest.category.toUpperCase(),
      characterId: customDiscovery?.characterId || targetQuest.characterId,
      imageUrl:
        photoUrl ||
        (targetQuest.category === 'plant'
          ? 'https://images.unsplash.com/photo-1528183429752-a97d0bf99b5a?auto=format&fit=crop&w=600&q=80'
          : targetQuest.category === 'monument'
          ? 'https://images.unsplash.com/photo-1548625361-195fe57876a2?auto=format&fit=crop&w=600&q=80'
          : 'https://images.unsplash.com/photo-1507667522111-bf5a34e00517?auto=format&fit=crop&w=600&q=80'),
      timestamp: 'Just now',
      locationName: customDiscovery?.locationName || 'Local Outdoor Exploration',
      explanation: customDiscovery?.explanation || `Awesome field work! You documented this for ${targetQuest.characterId.toUpperCase()}.`,
      coolFact: customDiscovery?.coolFact || 'Observing real-world details outside triggers dopaminergic learning pathways far more effectively than reading textbooks!',
      xpEarned: xp,
      coinsEarned: coins,
      sources: customDiscovery?.sources || [
        { title: 'Partnerizt Environmental Intelligence Engine', source: 'partnerizt.app' },
        { title: 'Global Biodiversity Knowledgebase', source: 'gbif.org' },
      ],
    };

    setDiscoveries((prev) => [newDiscovery, ...prev]);

    // Update user stats
    setUser((prev) => {
      let newXp = prev.currentXp + xp;
      let newLevel = prev.level;
      let nextLevelXp = prev.nextLevelXp;

      if (newXp >= nextLevelXp) {
        newLevel += 1;
        nextLevelXp = Math.round(nextLevelXp * 1.35);
      }

      return {
        ...prev,
        level: newLevel,
        currentXp: newXp,
        nextLevelXp,
        coins: prev.coins + coins,
        questsCompletedCount: prev.questsCompletedCount + 1,
        discoveriesCount: prev.discoveriesCount + 1,
      };
    });

    // Update active session counters if exploring
    if (activeSession) {
      setActiveSession((prev) =>
        prev
          ? {
              ...prev,
              questsCompletedCount: prev.questsCompletedCount + 1,
              discoveriesCount: prev.discoveriesCount + 1,
              xpEarned: prev.xpEarned + xp,
              coinsEarned: prev.coinsEarned + coins,
            }
          : null
      );
    }

    // Trigger celebration
    setCelebration({
      title: 'Quest Completed!',
      subtitle: customDiscovery?.title || targetQuest.title,
      xpEarned: xp,
      coinsEarned: coins,
      characterId: targetQuest.characterId,
    });
  };

  const openCharacterChat = (characterId: CharacterId) => {
    soundService.playTap();
    setActiveChatCharacterId(characterId);
  };

  const closeCharacterChat = () => {
    setActiveChatCharacterId(null);
  };

  const closeCelebration = () => {
    setCelebration(null);
  };

  return (
    <PartneriztContext.Provider
      value={{
        user,
        quests,
        badges,
        discoveries,
        characters,
        activeSession,
        isExploring,
        gpsCoordinates,
        gpsAccuracy,
        locationPermissionStatus,
        isSimulatingWalk,
        celebration,
        activeChatCharacterId,
        soundEnabled,
        startExploration,
        stopExploration,
        toggleSimulateWalk,
        startQuest,
        completeQuest,
        openCharacterChat,
        closeCharacterChat,
        closeCelebration,
        toggleSound,
        awardXpAndCoins,
      }}
    >
      {children}
    </PartneriztContext.Provider>
  );
};

export const usePartnerizt = () => {
  const context = useContext(PartneriztContext);
  if (!context) {
    throw new Error('usePartnerizt must be used within a PartneriztProvider');
  }
  return context;
};
