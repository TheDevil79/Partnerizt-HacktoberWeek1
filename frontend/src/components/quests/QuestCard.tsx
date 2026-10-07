import React from 'react';
import { Sparkles, Coins, Clock, ArrowRight, CheckCircle2, Camera, MapPin } from 'lucide-react';
import { Quest } from '../../types';
import { CharacterAvatar } from '../../assets/characterAvatars';

interface QuestCardProps {
  quest: Quest;
  onSelect: (quest: Quest) => void;
  onCharacterClick?: (quest: Quest) => void;
}

function formatTimeRemaining(expiresAt: string): string {
  const diff = new Date(expiresAt).getTime() - Date.now();
  if (diff <= 0) return 'Expired';
  const h = Math.floor(diff / 3_600_000);
  const m = Math.floor((diff % 3_600_000) / 60_000);
  if (h > 0) return `${h}h ${m}m left`;
  return `${m}m left`;
}

export const QuestCard: React.FC<QuestCardProps> = ({ quest, onSelect, onCharacterClick }) => {
  const isCompleted = quest.status === 'completed';
  const isInProgress = quest.status === 'in_progress';
  const timeStr = formatTimeRemaining(quest.expiresAt);
  const isExpiringSoon = new Date(quest.expiresAt).getTime() - Date.now() < 60 * 60 * 1000;

  return (
    <div
      onClick={() => onSelect(quest)}
      className={`card-duo-interactive flex flex-col justify-between overflow-hidden ${
        isCompleted ? 'bg-slate-50/80 border-slate-200 opacity-80' : 'hover:border-emerald-300'
      }`}
    >
      {/* Top accent line */}
      {!isCompleted && (
        <div className="h-1 w-full bg-gradient-to-r from-emerald-400 to-teal-400" />
      )}

      <div className="p-4 sm:p-5">
        {/* Header row: category + rewards */}
        <div className="flex items-start justify-between gap-2 mb-3">
          <div className="flex items-center gap-2">
            <span className="text-xl">{quest.iconEmoji}</span>
            <div>
              <span className="text-[10px] font-extrabold uppercase tracking-wider text-slate-500 bg-slate-100 px-1.5 py-0.5 rounded-md block">
                {quest.category}
              </span>
              {quest.isDaily && (
                <span className="text-[10px] font-bold text-emerald-700 bg-emerald-50 px-1.5 py-0.5 rounded-md mt-0.5 block">
                  Daily
                </span>
              )}
            </div>
          </div>

          <div className="flex flex-col items-end gap-1">
            <span className="bg-emerald-50 text-emerald-700 text-xs font-black px-2 py-0.5 rounded-lg flex items-center gap-1">
              <Sparkles className="w-3 h-3" /> +{quest.xpReward} XP
            </span>
            <span className="bg-amber-50 text-amber-700 text-xs font-black px-2 py-0.5 rounded-lg flex items-center gap-1">
              <Coins className="w-3 h-3" /> +{quest.coinReward}
            </span>
          </div>
        </div>

        {/* Quest body: character + title + description */}
        <div className="flex items-start gap-3 mb-3">
          <div
            onClick={(e) => {
              if (onCharacterClick) {
                e.stopPropagation();
                onCharacterClick(quest);
              }
            }}
            className="flex-shrink-0 cursor-pointer hover:scale-110 active:scale-95 transition-transform"
            title={`Guide: ${quest.characterId.toUpperCase()}`}
          >
            <CharacterAvatar characterId={quest.characterId} size={44} />
          </div>

          <div className="flex-1 min-w-0">
            <div className="text-[10px] font-bold text-emerald-700 uppercase tracking-wide">
              {quest.characterId} — guide
            </div>
            <h3 className="text-sm font-bold text-slate-900 leading-snug mt-0.5">
              {quest.title}
            </h3>
            <p className="text-xs text-slate-500 mt-1 line-clamp-2 leading-relaxed font-medium">
              {quest.description}
            </p>
          </div>
        </div>

        {/* Where to look */}
        <div className="flex items-center gap-1.5 text-[11px] text-slate-500 font-medium mb-3">
          <MapPin className="w-3.5 h-3.5 text-emerald-500 flex-shrink-0" />
          <span className="truncate">{quest.targetHint}</span>
        </div>

        {/* Photo badge */}
        <div className="flex items-center gap-1.5 bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-1.5 text-[11px] text-slate-600 font-medium">
          <Camera className="w-3.5 h-3.5 text-emerald-600 flex-shrink-0" />
          <span>Photo evidence required</span>
        </div>
      </div>

      {/* Card footer: time remaining + status */}
      <div className="px-4 sm:px-5 py-3 border-t border-slate-100 bg-slate-50/50 flex items-center justify-between gap-2">
        <div className={`flex items-center gap-1 text-[11px] font-bold ${isExpiringSoon && !isCompleted ? 'text-rose-600' : 'text-slate-500'}`}>
          <Clock className="w-3.5 h-3.5" />
          <span>{isCompleted ? 'Completed' : timeStr}</span>
        </div>

        {isCompleted ? (
          <span className="flex items-center gap-1 text-xs font-bold text-emerald-700 bg-emerald-100 px-2.5 py-1 rounded-xl">
            <CheckCircle2 className="w-3.5 h-3.5" /> Done
          </span>
        ) : isInProgress ? (
          <span className="flex items-center gap-1 text-xs font-bold text-amber-800 bg-amber-100 px-2.5 py-1 rounded-xl">
            <Camera className="w-3.5 h-3.5" /> Submit photo
          </span>
        ) : (
          <span className="text-xs font-extrabold text-emerald-600 flex items-center gap-1">
            View & Start <ArrowRight className="w-3.5 h-3.5" />
          </span>
        )}
      </div>
    </div>
  );
};
