import React from 'react';
import { Footprints, Clock, Camera, Target, Award } from 'lucide-react';
import { usePartnerizt } from '../../context/PartneriztContext';
import { formatDistance, formatDuration } from '../../services/explorationService';

export const StatsGrid: React.FC = () => {
  const { user } = usePartnerizt();

  const STATS_CARDS = [
    {
      icon: Footprints,
      label: 'Total Distance',
      value: formatDistance(user.totalDistanceKm),
      subtext: 'Calculated via GPS',
      color: 'text-emerald-700 bg-emerald-50 border-emerald-100',
      iconColor: 'text-emerald-600',
    },
    {
      icon: Clock,
      label: 'Exploration Time',
      value: formatDuration(user.totalExplorationTimeSeconds),
      subtext: 'Time spent outdoors',
      color: 'text-blue-700 bg-blue-50 border-blue-100',
      iconColor: 'text-blue-600',
    },
    {
      icon: Camera,
      label: 'Discoveries',
      value: user.discoveriesCount,
      subtext: 'AI verified field finds',
      color: 'text-purple-700 bg-purple-50 border-purple-100',
      iconColor: 'text-purple-600',
    },
    {
      icon: Target,
      label: 'Quests Completed',
      value: user.questsCompletedCount,
      subtext: 'Real-world challenges',
      color: 'text-amber-700 bg-amber-50 border-amber-100',
      iconColor: 'text-amber-600',
    },
  ];

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <h3 className="text-base font-black text-slate-900 font-sans tracking-tight flex items-center gap-1.5">
          <Award className="w-4 h-4 text-emerald-600" /> Exploration Statistics
        </h3>
        <span className="text-xs text-slate-400 font-medium">All-time record</span>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        {STATS_CARDS.map((stat, idx) => {
          const Icon = stat.icon;
          return (
            <div
              key={idx}
              className={`rounded-3xl p-4 border-2 ${stat.color} flex flex-col justify-between transition-all hover:scale-[1.02] shadow-sm`}
            >
              <div className="flex items-center justify-between mb-2">
                <span className="text-xs font-bold text-slate-600">{stat.label}</span>
                <Icon className={`w-4 h-4 ${stat.iconColor}`} />
              </div>

              <div>
                <div className="text-2xl font-black text-slate-900 tracking-tight">
                  {stat.value}
                </div>
                <div className="text-[10px] text-slate-500 font-medium mt-0.5">
                  {stat.subtext}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
