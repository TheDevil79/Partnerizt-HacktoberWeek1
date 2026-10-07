import React, { useState } from 'react';
import { createPortal } from 'react-dom';
import { Sparkles, Coins, Clock, MapPin, Camera, MessageSquare } from 'lucide-react';
import { Quest } from '../../types';
import { CharacterAvatar } from '../../assets/characterAvatars';
import { usePartnerizt } from '../../context/PartneriztContext';
import { QuestPhotoUploadModal } from './QuestPhotoUploadModal';

interface QuestDetailsModalProps {
  quest: Quest | null;
  isOpen: boolean;
  onClose: () => void;
}

export const QuestDetailsModal: React.FC<QuestDetailsModalProps> = ({
  quest,
  isOpen,
  onClose,
}) => {
  const { startQuest, openCharacterChat } = usePartnerizt();
  const [isPhotoModalOpen, setIsPhotoModalOpen] = useState(false);

  if (!isOpen || !quest) return null;

  return (
    <>
      {createPortal(
        <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4 bg-slate-900/60 backdrop-blur-sm overflow-y-auto">
        <div className="bg-white rounded-4xl w-full max-w-md p-5 sm:p-6 shadow-2xl border border-slate-100 relative max-h-[90vh] flex flex-col my-auto">
          {/* Header */}
          <div className="flex items-center justify-between pb-3 border-b border-slate-100 mb-3 flex-shrink-0">
            <span className="text-xs font-black uppercase tracking-wider text-slate-500 bg-slate-100 px-2.5 py-0.5 rounded-full">
              Quest Details
            </span>
            <button
              onClick={onClose}
              className="p-1.5 rounded-full hover:bg-slate-100 text-slate-400 hover:text-slate-700 transition-colors"
            >
              ✕
            </button>
          </div>

          <div className="overflow-y-auto flex-1 space-y-4 pr-1">
            {/* Character Companion Callout */}
            <div className="flex items-center gap-3 p-3 rounded-2xl bg-emerald-50/70 border border-emerald-100">
              <CharacterAvatar characterId={quest.characterId} size={52} animated={true} />
              <div className="flex-1">
                <span className="text-[11px] font-black uppercase tracking-wider text-emerald-800">
                  Quest Mentor: {quest.characterId.toUpperCase()}
                </span>
                <p className="text-xs text-slate-600 font-medium">
                  Ask {quest.characterId.toUpperCase()} for domain tips anytime!
                </p>
              </div>
              <button
                onClick={() => {
                  onClose();
                  openCharacterChat(quest.characterId);
                }}
                className="p-2 rounded-xl bg-white text-emerald-700 shadow-sm border border-emerald-200 hover:bg-emerald-50 transition-colors"
                title="Chat with Mentor"
              >
                <MessageSquare className="w-4 h-4" />
              </button>
            </div>

            {/* Title & Description */}
            <div>
              <div className="flex items-center gap-2 mb-1">
                <span className="text-2xl">{quest.iconEmoji}</span>
                <h3 className="text-lg sm:text-xl font-black text-slate-900 leading-snug">
                  {quest.title}
                </h3>
              </div>
              <p className="text-sm text-slate-600 font-medium leading-relaxed mt-2">
                {quest.description}
              </p>
            </div>

            {/* Target Hint */}
            <div className="bg-slate-50 p-3 rounded-2xl border border-slate-200 flex items-start gap-2.5 text-xs text-slate-600">
              <MapPin className="w-4 h-4 text-emerald-600 flex-shrink-0 mt-0.5" />
              <div>
                <strong className="text-slate-800 font-bold block mb-0.5">Where to look:</strong>
                <span>{quest.targetHint}</span>
              </div>
            </div>

            {/* Rewards Card */}
            <div className="grid grid-cols-2 gap-3 pt-1">
              <div className="bg-emerald-50 rounded-2xl p-3 border border-emerald-200 text-center">
                <span className="text-xs font-bold text-emerald-700 uppercase flex items-center justify-center gap-1">
                  <Sparkles className="w-3.5 h-3.5" /> Experience
                </span>
                <div className="text-xl font-black text-emerald-900 mt-0.5">
                  +{quest.xpReward} XP
                </div>
              </div>

              <div className="bg-amber-50 rounded-2xl p-3 border border-amber-200 text-center">
                <span className="text-xs font-bold text-amber-700 uppercase flex items-center justify-center gap-1">
                  <Coins className="w-3.5 h-3.5" /> Explorer Coins
                </span>
                <div className="text-xl font-black text-amber-900 mt-0.5">
                  +{quest.coinReward}
                </div>
              </div>
            </div>

            {/* Timer reminder */}
            <div className="flex items-center justify-center gap-1.5 text-xs font-semibold text-slate-500 py-1">
              <Clock className="w-3.5 h-3.5 text-slate-400" />
              <span>Expires in {quest.durationMinutes} minutes</span>
            </div>

            {/* Actions */}
            <div className="pt-2">
              {quest.status === 'completed' ? (
                <div className="bg-emerald-100 text-emerald-800 font-bold text-center py-3 rounded-2xl">
                  Quest Already Completed!
                </div>
              ) : (
                <button
                  onClick={() => {
                    startQuest(quest.id);
                    setIsPhotoModalOpen(true);
                  }}
                  className="btn-duo-primary w-full py-3.5 text-base"
                >
                  <Camera className="w-5 h-5" />
                  <span>Start &amp; Upload Photo</span>
                </button>
              )}
            </div>
          </div>
        </div>,
        document.body
      )}

      {/* Photo submission modal */}
      {isPhotoModalOpen && (
        <QuestPhotoUploadModal
          quest={quest}
          isOpen={isPhotoModalOpen}
          onClose={() => {
            setIsPhotoModalOpen(false);
            onClose();
          }}
        />
      )}
    </>
  );
};
