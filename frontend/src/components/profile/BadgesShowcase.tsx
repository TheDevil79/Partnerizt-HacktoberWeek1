import React from 'react';
import { Award } from 'lucide-react';
import { usePartnerizt } from '../../context/PartneriztContext';
import { BadgeCard } from '../common/BadgeCard';

export const BadgesShowcase: React.FC = () => {
  const { badges } = usePartnerizt();
  const unlockedCount = badges.filter((b) => b.isUnlocked).length;

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <h3 className="text-base font-black text-slate-900 font-sans tracking-tight flex items-center gap-1.5">
          <Award className="w-4 h-4 text-amber-500" /> Explorer Badges
        </h3>
        <span className="text-xs font-bold text-emerald-700 bg-emerald-50 border border-emerald-200 px-2.5 py-0.5 rounded-full">
          {unlockedCount} / {badges.length} Unlocked
        </span>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3">
        {badges.map((badge) => (
          <BadgeCard key={badge.id} badge={badge} />
        ))}
      </div>
    </div>
  );
};
