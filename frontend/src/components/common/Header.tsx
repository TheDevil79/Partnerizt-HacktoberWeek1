import React from 'react';
import { Flame, Coins, Sparkles, Volume2, VolumeX, Compass } from 'lucide-react';
import { usePartnerizt } from '../../context/PartneriztContext';
import { StatPill } from './StatPill';

export const Header: React.FC = () => {
  const { user, soundEnabled, toggleSound, isExploring } = usePartnerizt();

  return (
    <header className="sticky top-0 z-30 bg-[#FAF8F5]/90 backdrop-blur-md border-b border-slate-200/80 px-4 py-2.5 transition-all">
      <div className="max-w-4xl mx-auto flex items-center justify-between gap-2">
        {/* Brand Logo & Name */}
        <div className="flex items-center gap-2.5">
          <div className="w-10 h-10 rounded-2xl bg-gradient-to-tr from-emerald-600 to-teal-400 flex items-center justify-center text-white font-black shadow-duo-primary border-b-2 border-emerald-800">
            <Compass className={`w-6 h-6 ${isExploring ? 'animate-spin' : ''}`} style={{ animationDuration: '6s' }} />
          </div>
          <div>
            <div className="flex items-center gap-1.5">
              <span className="font-extrabold text-lg sm:text-xl tracking-tight text-slate-900 font-sans">
                Partnerizt
              </span>
              <span className="bg-emerald-100/90 text-emerald-800 text-[10px] font-black px-2 py-0.5 rounded-full uppercase tracking-wider">
                Outdoor
              </span>
            </div>
            <p className="text-[10px] text-slate-500 font-semibold hidden sm:block">
              Learn from your environment
            </p>
          </div>
        </div>

        {/* Gamification Stats Header Bar */}
        <div className="flex items-center gap-1.5 sm:gap-2.5">
          {/* Streak */}
          <StatPill
            icon={<Flame className="w-4 h-4 text-orange-500 fill-orange-500 animate-pulse" />}
            label="Streak"
            value={`${user.currentStreak}d`}
            highlightColor="bg-orange-50/90 border-orange-200"
          />

          {/* Coins */}
          <StatPill
            icon={<Coins className="w-4 h-4 text-amber-500 fill-amber-500" />}
            label="Coins"
            value={user.coins}
            highlightColor="bg-amber-50/90 border-amber-200"
          />

          {/* Level / XP */}
          <StatPill
            icon={<Sparkles className="w-4 h-4 text-emerald-600" />}
            label={`Lvl ${user.level}`}
            value={`${user.currentXp} XP`}
            highlightColor="bg-emerald-50/90 border-emerald-200 hidden xs:flex"
          />

          {/* Sound Toggle */}
          <button
            onClick={toggleSound}
            aria-label={soundEnabled ? 'Mute sound' : 'Unmute sound'}
            className="p-2 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-slate-600 transition-all shadow-sm active:scale-95"
          >
            {soundEnabled ? <Volume2 className="w-4 h-4 text-emerald-600" /> : <VolumeX className="w-4 h-4 text-slate-400" />}
          </button>
        </div>
      </div>
    </header>
  );
};
