import React, { useState } from 'react';
import { Target, Sparkles } from 'lucide-react';
import { usePartnerizt } from '../context/PartneriztContext';
import { Quest } from '../types';
import { QuestFilterTabs } from '../components/quests/QuestFilterTabs';
import { QuestCard } from '../components/quests/QuestCard';
import { QuestDetailsModal } from '../components/quests/QuestDetailsModal';

export const QuestsPage: React.FC = () => {
  const { quests, openCharacterChat } = usePartnerizt();
  const [activeTab, setActiveTab] = useState('all');
  const [selectedQuest, setSelectedQuest] = useState<Quest | null>(null);

  const filteredQuests = quests.filter((q) => {
    if (activeTab === 'all') return true;
    return q.category === activeTab;
  });

  const availableCount = quests.filter((q) => q.status !== 'completed').length;

  return (
    <div className="space-y-4 pb-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
        <div>
          <h1 className="text-2xl font-black text-slate-900 font-sans tracking-tight flex items-center gap-2">
            <Target className="w-6 h-6 text-emerald-600" />
            <span>Outdoor Quests</span>
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 font-medium mt-0.5">
            Go outside, find it, photograph it — your companion does the rest
          </p>
        </div>

        <div className="flex items-center gap-1.5 bg-emerald-50 border border-emerald-200 px-3 py-1.5 rounded-2xl text-xs font-bold text-emerald-800 self-start sm:self-auto">
          <Sparkles className="w-4 h-4 text-emerald-600" />
          <span>{availableCount} open missions</span>
        </div>
      </div>

      {/* Category Filter Tabs */}
      <QuestFilterTabs activeTab={activeTab} onTabChange={setActiveTab} />

      {/* Quests Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-2">
        {filteredQuests.map((quest) => (
          <QuestCard
            key={quest.id}
            quest={quest}
            onSelect={(q) => setSelectedQuest(q)}
            onCharacterClick={(q) => openCharacterChat(q.characterId)}
          />
        ))}
      </div>

      {filteredQuests.length === 0 && (
        <div className="text-center py-12 bg-white rounded-3xl border border-slate-200 p-6">
          <span className="text-3xl">🧭</span>
          <h4 className="font-bold text-slate-700 text-sm mt-2">No outdoor missions here yet</h4>
          <p className="text-xs text-slate-400 mt-1">Try a different category or head outside to explore!</p>
        </div>
      )}

      {/* Quest Details Modal */}
      {selectedQuest && (
        <QuestDetailsModal
          quest={selectedQuest}
          isOpen={selectedQuest !== null}
          onClose={() => setSelectedQuest(null)}
        />
      )}
    </div>
  );
};
