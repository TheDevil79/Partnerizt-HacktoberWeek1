export interface Badge {
  id: string;
  title: string;
  description: string;
  iconEmoji: string;
  category: 'exploration' | 'discovery' | 'streak' | 'mastery';
  rarity: 'common' | 'rare' | 'epic' | 'legendary';
  unlockedAt?: string;
  isUnlocked: boolean;
  progressCurrent?: number;
  progressTarget?: number;
}

export interface UserStats {
  id: string;
  username: string;
  displayName: string;
  level: number;
  currentXp: number;
  nextLevelXp: number;
  coins: number;
  currentStreak: number;
  longestStreak: number;
  totalDistanceKm: number;
  totalExplorationTimeSeconds: number;
  discoveriesCount: number;
  questsCompletedCount: number;
  todayDistanceKm: number;
  todayExplorationTimeSeconds: number;
  todayTargetDistanceKm: number;
}
