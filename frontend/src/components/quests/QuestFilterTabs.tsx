import React from 'react';

interface QuestFilterTabsProps {
  activeTab: string;
  onTabChange: (tab: string) => void;
}

const TABS = [
  { id: 'all', label: 'All Quests', icon: '🌟' },
  { id: 'animal', label: 'Wildlife & Birds', icon: '🐦' },
  { id: 'plant', label: 'Plants & Nature', icon: '🌿' },
  { id: 'monument', label: 'Architecture', icon: '🏛️' },
  { id: 'discovery', label: 'Science & Geology', icon: '✨' },
];

export const QuestFilterTabs: React.FC<QuestFilterTabsProps> = ({
  activeTab,
  onTabChange,
}) => {
  return (
    <div className="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-none">
      {TABS.map((tab) => {
        const isActive = activeTab === tab.id;
        return (
          <button
            key={tab.id}
            onClick={() => onTabChange(tab.id)}
            className={`flex items-center gap-1.5 px-3.5 py-2 rounded-2xl text-xs font-bold whitespace-nowrap transition-all select-none cursor-pointer ${
              isActive
                ? 'bg-emerald-500 text-white shadow-duo-primary border-b-2 border-emerald-700 scale-102'
                : 'bg-white hover:bg-slate-50 text-slate-600 border border-slate-200/80 shadow-sm'
            }`}
          >
            <span>{tab.icon}</span>
            <span>{tab.label}</span>
          </button>
        );
      })}
    </div>
  );
};
