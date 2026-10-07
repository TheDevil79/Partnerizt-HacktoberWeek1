import { CharacterId } from './character';

export type QuestCategory = 'animal' | 'bird' | 'plant' | 'monument' | 'discovery';

export interface Quest {
  id: string;
  title: string;
  description: string;
  characterId: CharacterId;
  category: QuestCategory;
  xpReward: number;
  coinReward: number;
  durationMinutes: number;
  expiresAt: string; // ISO string
  isDaily: boolean;
  status: 'available' | 'in_progress' | 'completed' | 'expired';
  targetHint: string;
  iconEmoji: string;
}

export interface QuestAttempt {
  questId: string;
  startedAt: string;
  completedAt?: string;
  photoUrl?: string;
  discoveryId?: string;
  status: 'in_progress' | 'completed' | 'failed';
}
