import React from 'react';

interface XPBarProps {
  currentXp: number;
  nextLevelXp: number;
  level: number;
  showDetails?: boolean;
  className?: string;
}

export const XPBar: React.FC<XPBarProps> = ({
  currentXp,
  nextLevelXp,
  level,
  showDetails = true,
  className = '',
}) => {
  const percentage = Math.min(100, Math.max(0, (currentXp / nextLevelXp) * 100));

  return (
    <div className={`w-full ${className}`}>
      {showDetails && (
        <div className="flex items-center justify-between text-xs font-bold mb-1.5 px-0.5">
          <div className="flex items-center gap-1.5 text-emerald-800">
            <span className="bg-emerald-600 text-white text-[11px] px-2 py-0.5 rounded-full font-black tracking-wide">
              LVL {level}
            </span>
            <span className="text-slate-600 font-semibold">Explorer</span>
          </div>
          <span className="text-slate-600 font-medium">
            <strong className="text-slate-900 font-extrabold">{currentXp.toLocaleString()}</strong> /{' '}
            {nextLevelXp.toLocaleString()} XP
          </span>
        </div>
      )}

      {/* Outer track */}
      <div className="h-3.5 w-full bg-slate-200/90 rounded-full overflow-hidden p-0.5 shadow-inner relative">
        {/* Animated fill */}
        <div
          className="h-full bg-gradient-to-r from-emerald-500 via-teal-400 to-emerald-400 rounded-full transition-all duration-700 ease-out relative"
          style={{ width: `${percentage}%` }}
        >
          {/* Subtle shine highlight */}
          <div className="absolute inset-0 bg-white/25 rounded-full h-1/2 top-0" />
        </div>
      </div>
    </div>
  );
};
