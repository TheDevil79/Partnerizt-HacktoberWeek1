import { CharacterId } from './character';

export interface WebKnowledge {
  title: string;
  snippet: string;
  source: string;
  url?: string;
  scientificName?: string;
  nativeRegion?: string;
  funFact?: string;
}

export interface IdentificationResult {
  name: string;
  scientificName?: string;
  category: 'animal' | 'bird' | 'plant' | 'monument' | 'discovery' | 'geology' | 'architecture' | 'food';
  confidence: number;
  characterId: CharacterId;
  explanation: string;
  coolFact: string;
  outdoorTip: string;
  safetyDisclaimer?: string;
  isIdentified?: boolean;
  suggestedChallenge?: string;
  knowledgeSources: WebKnowledge[];
  xpValue?: number;
  coinsValue?: number;
  domain?: string;
}

export interface Discovery {
  id: string;
  title: string;
  category: string;
  characterId: CharacterId;
  imageUrl: string;
  timestamp: string;
  locationName: string;
  scientificName?: string;
  explanation: string;
  coolFact: string;
  safetyDisclaimer?: string;
  xpEarned: number;
  coinsEarned: number;
  sources: Array<{
    title: string;
    source: string;
    url?: string;
  }>;
}
