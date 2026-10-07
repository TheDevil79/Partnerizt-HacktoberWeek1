import React from 'react';
import { ExplorationHero } from '../components/home/ExplorationHero';
import { DailyProgressCard } from '../components/home/DailyProgressCard';
import { DailyQuestCard } from '../components/home/DailyQuestCard';
import { RecentDiscoveryCard } from '../components/home/RecentDiscoveryCard';

export const HomePage: React.FC = () => {
  return (
    <div className="space-y-5 pb-6">
      {/* 1. Main Exploration Hero (Go outside & explore / Live Session HUD) */}
      <ExplorationHero />

      {/* 2. Today's Exploration Progress & Stats */}
      <DailyProgressCard />

      {/* 3. Today's Featured Quest */}
      <DailyQuestCard />

      {/* 4. Most Recent Field Discovery */}
      <RecentDiscoveryCard />
    </div>
  );
};
