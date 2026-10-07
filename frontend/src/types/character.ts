export type CharacterId = 'birdo' | 'flora' | 'atlas' | 'munch' | 'nova';

export interface Character {
  id: CharacterId;
  name: string;
  tagline: string;
  domain: string;
  personality: string;
  bio: string;
  avatarColor: string;
  badgeEmoji: string;
  themeColor: {
    bg: string;
    border: string;
    text: string;
    badge: string;
    bubble: string;
  };
  sampleQuestions: string[];
  sampleOutdoorActivities: string[];
}

export interface CharacterMessage {
  id: string;
  sender: 'user' | 'character';
  characterId?: CharacterId;
  text: string;
  timestamp: string;
  suggestedActivity?: string;
  sourceReferences?: Array<{
    title: string;
    source: string;
    url?: string;
  }>;
}
