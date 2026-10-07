import React from 'react';
import { Footprints, Clock, Flame, Sparkles } from 'lucide-react';
import { usePartnerizt } from '../../context/PartneriztContext';
import { XPBar } from '../common/XPBar';
import { formatDistance, formatDuration } from '../../services/explorationService';

export const DailyProgressCard: React.FC = () => {
  const { user } = usePartnerizt();
  const dailyDistanceGoal = user.todayTargetDistanceKm || 2.0;
  const distancePercentage = Math.min(100, Math.round((user.todayDistanceKm / dailyDistanceGoal) * 100));

  return (
    <div className="card-duo p-5 sm:p-6">
      {/* Header */}
      <div className="flex items-center justify-between mb-4">
        <div>
          <h2 className="text-lg font-black text-slate-900 font-sans tracking-tight">
            Today's Field Activity
          </h2>
          <p className="text-xs text-slate-500 font-medium mt-0.5">
            Distance explored + learning progress
          </p>
        </div>
        <div className="flex items-center gap-1.5 bg-orange-50 border border-orange-200 px-3 py-1 rounded-2xl text-xs font-black text-orange-600">
          <Flame className="w-4 h-4 fill-orange-500" />
          <span>{user.currentStreak} Day Streak</span>
        </div>
      </div>

      {/* Main XP Progress Bar */}
      <div className="mb-5 bg-slate-50 p-3.5 rounded-2xl border border-slate-100">
        <XPBar
          level={user.level}
          currentXp={user.currentXp}
          nextLevelXp={user.nextLevelXp}
          showDetails={true}
        />
      </div>

      {/* Daily Metrics 2-column Grid */}
      <div className="grid grid-cols-2 gap-3">
        {/* Today's Distance (PRIMARY METRIC) */}
        <div className="bg-emerald-50/70 border-2 border-emerald-100 rounded-3xl p-3.5 flex flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-emerald-800 flex items-center gap-1">
              <Footprints className="w-4 h-4 text-emerald-600" />
              Distance
            </span>
            <span className="text-[11px] font-extrabold text-emerald-600 bg-emerald-100/80 px-2 py-0.5 rounded-full">
              {distancePercentage}%
            </span>
          </div>

          <div className="my-2">
            <div className="text-2xl font-black text-slate-900">
              {formatDistance(user.todayDistanceKm)}
            </div>
            <div className="text-[11px] text-slate-500 font-medium">
              Goal: {dailyDistanceGoal.toFixed(1)} km
            </div>
          </div>

          <div className="w-full h-2 bg-emerald-200/60 rounded-full overflow-hidden">
            <div
              className="h-full bg-emerald-500 rounded-full transition-all duration-500"
              style={{ width: `${distancePercentage}%` }}
            />
          </div>
        </div>

        {/* Today's Time Explored */}
        <div className="bg-amber-50/70 border-2 border-amber-100 rounded-3xl p-3.5 flex flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-amber-800 flex items-center gap-1">
              <Clock className="w-4 h-4 text-amber-600" />
              Time Outdoors
            </span>
            <span className="text-[11px] font-extrabold text-amber-700 bg-amber-100/80 px-2 py-0.5 rounded-full">
              Today
            </span>
          </div>

          <div className="my-2">
            <div className="text-2xl font-black text-slate-900">
              {formatDuration(user.todayExplorationTimeSeconds)}
            </div>
            <div className="text-[11px] text-slate-500 font-medium">
              Active exploration time
            </div>
          </div>

          <div className="flex items-center gap-1 text-[11px] text-amber-700 font-bold">
            <Sparkles className="w-3.5 h-3.5" /> Go find something new!
          </div>
        </div>
      </div>
    </div>
  );
};
