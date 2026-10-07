import React from 'react';
import { Lock, CheckCircle2 } from 'lucide-react';
import { Badge } from '../../types';

interface BadgeCardProps {
  badge: Badge;
  onClick?: () => void;
}

const RARITY_STYLES = {
  common: {
    border: 'border-slate-200',
    bg: 'bg-white',
    badgeText: 'text-slate-600 bg-slate-100',
    glow: '',
  },
  rare: {
    border: 'border-blue-200',
    bg: 'bg-gradient-to-b from-blue-50/40 to-white',
    badgeText: 'text-blue-700 bg-blue-100',
    glow: 'shadow-sm shadow-blue-100',
  },
  epic: {
    border: 'border-purple-200',
    bg: 'bg-gradient-to-b from-purple-50/50 to-white',
    badgeText: 'text-purple-700 bg-purple-100',
    glow: 'shadow-sm shadow-purple-100',
  },
  legendary: {
    border: 'border-amber-300',
    bg: 'bg-gradient-to-b from-amber-50/60 to-white',
    badgeText: 'text-amber-800 bg-amber-100',
    glow: 'shadow-md shadow-amber-100',
  },
};

export const BadgeCard: React.FC<BadgeCardProps> = ({ badge, onClick }) => {
  const styles = RARITY_STYLES[badge.rarity];
  const hasProgress = badge.progressTarget && badge.progressCurrent !== undefined;
  const progressPercent = hasProgress
    ? Math.min(100, (badge.progressCurrent! / badge.progressTarget!) * 100)
    : 0;

  return (
    <div
      onClick={onClick}
      className={`relative p-4 rounded-3xl border-2 ${styles.border} ${styles.bg} ${styles.glow} transition-all duration-200 ${
        badge.isUnlocked
          ? 'hover:scale-[1.02] active:scale-[0.98] cursor-pointer'
          : 'opacity-75 grayscale-[40%]'
      } flex flex-col justify-between`}
    >
      {/* Top Tag & Status */}
      <div className="flex items-center justify-between gap-1 mb-2">
        <span className={`text-[10px] font-extrabold uppercase tracking-wider px-2 py-0.5 rounded-full ${styles.badgeText}`}>
          {badge.rarity}
        </span>
        {badge.isUnlocked ? (
          <span className="flex items-center gap-1 text-[11px] font-bold text-emerald-600 bg-emerald-50 px-1.5 py-0.5 rounded-md">
            <CheckCircle2 className="w-3.5 h-3.5" /> Unlocked
          </span>
        ) : (
          <span className="flex items-center gap-1 text-[11px] font-semibold text-slate-500 bg-slate-100 px-1.5 py-0.5 rounded-md">
            <Lock className="w-3 h-3" /> Locked
          </span>
        )}
      </div>

      {/* Badge Icon & Info */}
      <div className="flex items-start gap-3 my-1">
        <div className="w-12 h-12 rounded-2xl bg-slate-100 flex items-center justify-center text-2xl flex-shrink-0 shadow-inner">
          {badge.iconEmoji}
        </div>
        <div>
          <h4 className="font-bold text-slate-800 text-sm leading-snug">{badge.title}</h4>
          <p className="text-xs text-slate-500 mt-0.5 leading-relaxed line-clamp-2">{badge.description}</p>
        </div>
      </div>

      {/* Progress Bar for Locked Badges with Target */}
      {!badge.isUnlocked && hasProgress && (
        <div className="mt-3 pt-2 border-t border-slate-100">
          <div className="flex justify-between text-[11px] font-bold text-slate-600 mb-1">
            <span>Progress</span>
            <span>
              {badge.progressCurrent} / {badge.progressTarget} km
            </span>
          </div>
          <div className="w-full h-2 bg-slate-100 rounded-full overflow-hidden">
            <div
              className="h-full bg-amber-500 rounded-full transition-all duration-500"
              style={{ width: `${progressPercent}%` }}
            />
          </div>
        </div>
      )}

      {badge.isUnlocked && badge.unlockedAt && (
        <div className="mt-2 text-[10px] text-slate-400 font-medium">
          Unlocked on {new Date(badge.unlockedAt).toLocaleDateString()}
        </div>
      )}
    </div>
  );
};
