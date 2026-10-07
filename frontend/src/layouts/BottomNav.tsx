import React from 'react';
import { NavLink } from 'react-router-dom';
import { Compass, Target, Users, User } from 'lucide-react';
import { soundService } from '../services/soundService';

interface NavItem {
  to: string;
  label: string;
  icon: React.ComponentType<{ className?: string }>;
  badgeCount?: number;
}

const NAV_ITEMS: NavItem[] = [
  { to: '/', label: 'Home', icon: Compass },
  { to: '/quests', label: 'Quests', icon: Target },
  { to: '/characters', label: 'Characters', icon: Users },
  { to: '/profile', label: 'Profile', icon: User },
];

export const BottomNav: React.FC = () => {
  return (
    <nav className="fixed bottom-0 left-0 right-0 z-40 bg-white/95 backdrop-blur-md border-t-2 border-slate-200/80 px-2 py-1.5 shadow-lg md:hidden">
      <div className="max-w-md mx-auto grid grid-cols-4 gap-1">
        {NAV_ITEMS.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.to}
              to={item.to}
              onClick={() => soundService.playTap()}
              className={({ isActive }) =>
                `flex flex-col items-center justify-center py-2 px-1 rounded-2xl transition-all duration-200 select-none ${
                  isActive
                    ? 'bg-emerald-50 text-emerald-600 font-extrabold scale-105 border-b-2 border-emerald-500'
                    : 'text-slate-500 hover:text-slate-800 font-semibold hover:bg-slate-50'
                }`
              }
            >
              <div className="relative">
                <Icon className="w-5 h-5" />
                {item.badgeCount && (
                  <span className="absolute -top-1.5 -right-2 bg-emerald-500 text-white text-[9px] font-black rounded-full px-1.5 py-0.2 shadow">
                    {item.badgeCount}
                  </span>
                )}
              </div>
              <span className="text-[11px] mt-1 tracking-tight">{item.label}</span>
            </NavLink>
          );
        })}
      </div>
    </nav>
  );
};

export const DesktopNav: React.FC = () => {
  return (
    <nav className="hidden md:flex items-center gap-2">
      {NAV_ITEMS.map((item) => {
        const Icon = item.icon;
        return (
          <NavLink
            key={item.to}
            to={item.to}
            onClick={() => soundService.playTap()}
            className={({ isActive }) =>
              `flex items-center gap-2 px-4 py-2 rounded-2xl font-bold text-sm transition-all select-none ${
                isActive
                  ? 'bg-emerald-500 text-white shadow-duo-primary border-b-2 border-emerald-700'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100/80'
              }`
            }
          >
            <Icon className="w-4 h-4" />
            <span>{item.label}</span>
          </NavLink>
        );
      })}
    </nav>
  );
};
