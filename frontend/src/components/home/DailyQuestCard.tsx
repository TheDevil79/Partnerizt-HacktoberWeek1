import React, { useState } from 'react';
import { Sparkles, Coins, Clock, ArrowRight, Camera, CheckCircle2, MapPin, AlertCircle } from 'lucide-react';
import { usePartnerizt } from '../../context/PartneriztContext';
import { CharacterAvatar } from '../../assets/characterAvatars';
import { QuestPhotoUploadModal } from '../quests/QuestPhotoUploadModal';

function formatTimeRemaining(expiresAt: string): string {
  const diff = new Date(expiresAt).getTime() - Date.now();
  if (diff <= 0) return 'Expired';
  const h = Math.floor(diff / 3_600_000);
  const m = Math.floor((diff % 3_600_000) / 60_000);
  if (h > 0) return `${h}h ${m}m left`;
  return `${m}m left`;
}

export const DailyQuestCard: React.FC = () => {
  const { quests, startQuest, openCharacterChat } = usePartnerizt();
  const [isPhotoModalOpen, setIsPhotoModalOpen] = useState(false);

  const dailyQuest = quests.find((q) => q.isDaily) || quests[0];
  if (!dailyQuest) return null;

  const timeStr = formatTimeRemaining(dailyQuest.expiresAt);
  const isExpiringSoon = new Date(dailyQuest.expiresAt).getTime() - Date.now() < 60 * 60 * 1000;

  return (
    <>
      <div className="card-duo border-2 border-emerald-200/80 overflow-hidden">
        {/* Card top accent band */}
        <div className="h-1.5 w-full bg-gradient-to-r from-emerald-500 via-teal-400 to-emerald-500" />

        <div className="p-5 sm:p-6">
          {/* Top row: label + timer + rewards */}
          <div className="flex flex-wrap items-center justify-between gap-2 mb-4">
            <div className="flex items-center gap-2">
              <span className="bg-emerald-600 text-white text-[11px] font-black uppercase tracking-wider px-2.5 py-0.5 rounded-full">
                Today's Quest
              </span>
              <span className={`text-xs font-bold flex items-center gap-1 ${isExpiringSoon ? 'text-rose-600' : 'text-slate-500'}`}>
                {isExpiringSoon && <AlertCircle className="w-3.5 h-3.5" />}
                <Clock className="w-3.5 h-3.5" />
                {timeStr}
              </span>
            </div>

            <div className="flex items-center gap-1.5">
              <span className="bg-emerald-100 text-emerald-800 text-xs font-black px-2.5 py-1 rounded-xl flex items-center gap-1 shadow-sm">
                <Sparkles className="w-3 h-3" /> +{dailyQuest.xpReward} XP
              </span>
              <span className="bg-amber-100 text-amber-800 text-xs font-black px-2.5 py-1 rounded-xl flex items-center gap-1 shadow-sm">
                <Coins className="w-3 h-3" /> +{dailyQuest.coinReward}
              </span>
            </div>
          </div>

          {/* Character + Quest body */}
          <div className="flex items-start gap-4">
            {/* Clickable character avatar → opens companion chat */}
            <div
              onClick={() => openCharacterChat(dailyQuest.characterId)}
              className="cursor-pointer hover:scale-105 active:scale-95 transition-transform flex-shrink-0 relative"
              title={`Chat with ${dailyQuest.characterId.toUpperCase()}`}
            >
              <CharacterAvatar characterId={dailyQuest.characterId} size={64} animated={true} />
              <div className="absolute -bottom-1 -right-1 bg-white rounded-full p-0.5 shadow border border-slate-200">
                <span className="text-xs">💬</span>
              </div>
            </div>

            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-1.5 mb-0.5">
                <span className="text-[11px] font-black uppercase tracking-wider text-emerald-700">
                  {dailyQuest.characterId}
                </span>
                <span className="text-[11px] text-slate-400 font-medium">• AI learning companion</span>
              </div>
              <h3 className="text-base sm:text-lg font-black text-slate-900 leading-snug">
                {dailyQuest.title}
              </h3>
              <p className="text-xs sm:text-sm text-slate-600 font-medium mt-1 leading-relaxed">
                {dailyQuest.description}
              </p>
            </div>
          </div>

          {/* Photo requirement callout */}
          <div className="mt-4 bg-slate-50 border border-slate-200 rounded-2xl px-3.5 py-2.5 flex items-center gap-2.5 text-xs text-slate-600">
            <Camera className="w-4 h-4 text-emerald-600 flex-shrink-0" />
            <span>
              <strong className="text-slate-800">Photo required.</strong> Find it outside, take a clear photo, and let Gemma + {dailyQuest.characterId.toUpperCase()} analyse it.
            </span>
          </div>

          {/* Where to look + action */}
          <div className="mt-3 pt-3 border-t border-slate-100 flex flex-col sm:flex-row sm:items-center gap-2.5">
            <div className="text-xs text-slate-500 font-medium flex items-center gap-1 flex-1">
              <MapPin className="w-3.5 h-3.5 text-emerald-500 flex-shrink-0" />
              <span>
                Look in: <span className="font-semibold text-slate-700">{dailyQuest.targetHint}</span>
              </span>
            </div>

            {dailyQuest.status === 'completed' ? (
              <div className="flex items-center justify-center gap-2 bg-emerald-100 text-emerald-800 font-bold px-4 py-2.5 rounded-xl text-sm">
                <CheckCircle2 className="w-4 h-4" />
                <span>Quest Completed!</span>
              </div>
            ) : (
              <button
                onClick={() => {
                  if (dailyQuest.status === 'available') startQuest(dailyQuest.id);
                  setIsPhotoModalOpen(true);
                }}
                className="btn-duo-primary w-full sm:w-auto py-2.5 px-5 text-sm"
              >
                <Camera className="w-4 h-4" />
                <span>{dailyQuest.status === 'in_progress' ? 'Submit Photo' : 'Go & Submit Photo'}</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            )}
          </div>
        </div>
      </div>

      {isPhotoModalOpen && (
        <QuestPhotoUploadModal
          quest={dailyQuest}
          isOpen={isPhotoModalOpen}
          onClose={() => setIsPhotoModalOpen(false)}
        />
      )}
    </>
  );
};
