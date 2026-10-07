export interface Coordinates {
  latitude: number;
  longitude: number;
  accuracy?: number;
  timestamp?: number;
}

export interface ExplorationSession {
  id: string;
  startTime: string;
  endTime?: string;
  distanceKm: number;
  durationSeconds: number;
  questsCompletedCount: number;
  discoveriesCount: number;
  xpEarned: number;
  coinsEarned: number;
  status: 'active' | 'completed' | 'paused';
}

export interface ExplorationMetrics {
  todayDistanceKm: number;
  todayDurationSeconds: number;
  activeSession: ExplorationSession | null;
}
