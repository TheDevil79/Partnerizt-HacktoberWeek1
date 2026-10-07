import React from 'react';
import { ProfileHeader } from '../components/profile/ProfileHeader';
import { StatsGrid } from '../components/profile/StatsGrid';
import { BadgesShowcase } from '../components/profile/BadgesShowcase';
import { DiscoveryGallery } from '../components/profile/DiscoveryGallery';

export const ProfilePage: React.FC = () => {
  return (
    <div className="space-y-6 pb-6">
      {/* 1. Profile Header & XP / Level Progression */}
      <ProfileHeader />

      {/* 2. Exploration Statistics Grid (Distance, Time, Discoveries, Quests) */}
      <StatsGrid />

      {/* 3. Badges Showcase */}
      <BadgesShowcase />

      {/* 4. Real-World Discovery Catalog */}
      <DiscoveryGallery />
    </div>
  );
};
