import React from 'react';
import { Coins, Flame, User as UserIcon } from 'lucide-react';
import { usePartnerizt } from '../../context/PartneriztContext';
import { XPBar } from '../common/XPBar';

export const ProfileHeader: React.FC = () => {
  const { user } = usePartnerizt();

  return (
    <div className="card-duo p-5 sm:p-7 relative overflow-hidden">
      {/* Background soft gradient */}
      <div className="absolute top-0 right-0 w-48 h-48 bg-gradient-to-bl from-emerald-100/60 to-transparent -z-0 rounded-full blur-2xl" />

      <div className="relative z-10 flex flex-col sm:flex-row items-center sm:items-start gap-4 sm:gap-6 text-center sm:text-left">
        {/* User Avatar */}
        <div className="w-20 h-20 sm:w-24 sm:h-24 rounded-3xl bg-gradient-to-tr from-emerald-600 to-teal-400 p-1 shadow-lg border-2 border-emerald-400 flex-shrink-0 flex items-center justify-center">
          <div className="w-full h-full rounded-2xl bg-white/20 backdrop-blur-sm flex items-center justify-center text-white">
            <UserIcon className="w-10 h-10 sm:w-12 sm:h-12" />
          </div>
        </div>

        {/* User Identity & Info */}
        <div className="flex-1 w-full">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
            <div>
              <h2 className="text-xl sm:text-2xl font-black text-slate-900 font-sans">
                {user.displayName}
              </h2>
              <p className="text-xs text-slate-500 font-medium">
                @{user.username} • Outdoor Environmental Explorer
              </p>
            </div>

            {/* Streak & Coins quick pills */}
            <div className="flex items-center justify-center sm:justify-end gap-2 mt-1 sm:mt-0">
              <div className="flex items-center gap-1.5 bg-orange-50 border border-orange-200 px-3 py-1 rounded-2xl text-xs font-black text-orange-600">
                <Flame className="w-4 h-4 fill-orange-500" />
                <span>{user.currentStreak}d Streak</span>
              </div>

              <div className="flex items-center gap-1.5 bg-amber-50 border border-amber-200 px-3 py-1 rounded-2xl text-xs font-black text-amber-700">
                <Coins className="w-4 h-4 fill-amber-500" />
                <span>{user.coins} Coins</span>
              </div>
            </div>
          </div>

          {/* XP Progress Bar */}
          <div className="mt-4 bg-slate-50 p-3 rounded-2xl border border-slate-100">
            <XPBar
              level={user.level}
              currentXp={user.currentXp}
              nextLevelXp={user.nextLevelXp}
              showDetails={true}
            />
          </div>
        </div>
      </div>
    </div>
  );
};
