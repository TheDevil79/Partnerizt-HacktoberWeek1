import React from 'react';
import { MessageSquare, Compass, Sparkles } from 'lucide-react';
import { Character } from '../../types';
import { CharacterAvatar } from '../../assets/characterAvatars';
import { usePartnerizt } from '../../context/PartneriztContext';

interface CharacterCardProps {
  character: Character;
}

export const CharacterCard: React.FC<CharacterCardProps> = ({ character }) => {
  const { openCharacterChat } = usePartnerizt();

  return (
    <div className="card-duo p-5 sm:p-6 flex flex-col justify-between hover:border-emerald-300 transition-all">
      <div>
        {/* Top Header: Avatar + Identity */}
        <div className="flex items-start gap-4 mb-4">
          <div
            onClick={() => openCharacterChat(character.id)}
            className="cursor-pointer hover:scale-105 active:scale-95 transition-transform flex-shrink-0 relative"
          >
            <CharacterAvatar characterId={character.id} size={72} animated={true} />
            <div className="absolute -bottom-1 -right-1 bg-white rounded-full p-1 shadow border border-slate-200 text-sm">
              {character.badgeEmoji}
            </div>
          </div>

          <div className="flex-1">
            <div className="flex items-center gap-2">
              <h3 className="text-xl font-black text-slate-900 font-sans">
                {character.name}
              </h3>
              <span className="text-[11px] font-bold px-2 py-0.5 rounded-full bg-slate-100 text-slate-700">
                {character.personality.split(',')[0]}
              </span>
            </div>
            <p className="text-xs font-bold text-emerald-700 mt-0.5">
              {character.domain}
            </p>
            <p className="text-xs text-slate-600 mt-1.5 leading-relaxed font-medium">
              {character.bio}
            </p>
          </div>
        </div>

        {character.sampleOutdoorActivities.length > 0 && (
          <div className="bg-emerald-50/60 border border-emerald-200/80 rounded-2xl p-3 my-3">
            <div className="flex items-center gap-1.5 text-xs font-bold text-emerald-800 mb-1">
              <Compass className="w-3.5 h-3.5 text-emerald-600" />
              <span>Try this outside</span>
            </div>
            <p className="text-xs text-emerald-950 leading-relaxed font-medium">
              "{character.sampleOutdoorActivities[0]}"
            </p>
          </div>
        )}

        {/* Things you can ask */}
        <div className="my-3">
          <div className="text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-2 flex items-center gap-1">
            <Sparkles className="w-3 h-3 text-amber-500" /> Spotted something? Ask {character.name}:
          </div>
          <div className="flex flex-wrap gap-1.5">
            {character.sampleQuestions.slice(0, 2).map((q, idx) => (
              <button
                key={idx}
                onClick={() => openCharacterChat(character.id)}
                className="text-left text-xs bg-slate-100 hover:bg-emerald-50 hover:text-emerald-800 text-slate-700 font-medium px-3 py-1.5 rounded-xl border border-slate-200/80 transition-colors"
              >
                "{q}"
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Action: Open Chat */}
      <div className="pt-3 border-t border-slate-100 mt-3">
        <button
          onClick={() => openCharacterChat(character.id)}
          className="btn-duo-primary w-full text-sm py-2.5"
        >
          <MessageSquare className="w-4 h-4" />
          <span>Ask {character.name} about your find</span>
        </button>
      </div>
    </div>
  );
};
