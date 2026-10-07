import React from 'react';
import { Sparkles, ExternalLink, MapPin, Eye } from 'lucide-react';
import { usePartnerizt } from '../../context/PartneriztContext';
import { CharacterAvatar } from '../../assets/characterAvatars';

export const RecentDiscoveryCard: React.FC = () => {
  const { discoveries, openCharacterChat } = usePartnerizt();
  const recent = discoveries[0];

  if (!recent) {
    return (
      <div className="card-duo p-5 sm:p-6 text-center border-dashed border-2 border-slate-200 bg-slate-50/50">
        <div className="w-12 h-12 rounded-2xl bg-emerald-100 text-emerald-600 flex items-center justify-center mx-auto mb-2.5">
          <Sparkles className="w-6 h-6" />
        </div>
        <h4 className="text-sm font-black text-slate-800">No Recent Discoveries Yet</h4>
        <p className="text-xs text-slate-500 max-w-sm mx-auto mt-1">
          Explore outdoors, complete quests, and photograph wildlife, plants, trees, rocks, or monuments to log your discoveries here!
        </p>
      </div>
    );
  }

  return (
    <div className="card-duo p-5 sm:p-6">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <span className="text-xs font-black uppercase tracking-wider text-slate-500">
            Recent Discovery
          </span>
          <span className="text-xs bg-emerald-100 text-emerald-800 font-bold px-2 py-0.5 rounded-full">
            {recent.category}
          </span>
        </div>
        <span className="text-xs text-slate-400 font-medium">{recent.timestamp}</span>
      </div>

      <div className="flex flex-col sm:flex-row gap-4 items-start">
        {/* Photo thumbnail */}
        <div className="relative w-full sm:w-36 h-36 rounded-2xl overflow-hidden flex-shrink-0 bg-slate-100 border border-slate-200 group">
          <img
            src={recent.imageUrl}
            alt={recent.title}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
          />
          <div className="absolute top-2 left-2 bg-slate-900/75 backdrop-blur-sm text-white text-[10px] font-bold px-2 py-0.5 rounded-md flex items-center gap-1">
            <Eye className="w-3 h-3 text-emerald-400" />
            <span>AI Verified</span>
          </div>
        </div>

        {/* Discovery Details */}
        <div className="flex-1 flex flex-col justify-between">
          <div>
            <div className="flex items-start justify-between gap-2">
              <div>
                <h3 className="text-lg font-black text-slate-900 leading-snug">
                  {recent.title}
                </h3>
                {recent.scientificName && (
                  <p className="text-xs italic text-emerald-700 font-serif font-medium">
                    {recent.scientificName}
                  </p>
                )}
              </div>
              <div className="flex items-center gap-1 bg-emerald-50 text-emerald-700 px-2 py-1 rounded-xl text-xs font-black flex-shrink-0">
                <Sparkles className="w-3 h-3" />
                <span>+{recent.xpEarned} XP</span>
              </div>
            </div>

            <p className="text-xs text-slate-600 mt-2 leading-relaxed">
              {recent.explanation}
            </p>

            {/* Fun Fact Callout */}
            <div className="mt-2.5 bg-amber-50/70 border border-amber-200/80 rounded-2xl p-2.5 flex items-start gap-2.5">
              <div
                onClick={() => openCharacterChat(recent.characterId)}
                className="cursor-pointer hover:scale-105 transition-transform flex-shrink-0"
              >
                <CharacterAvatar characterId={recent.characterId} size={32} />
              </div>
              <div className="flex-1 text-[11px] text-amber-900">
                <span className="font-extrabold uppercase text-[10px] tracking-wider text-amber-700 mr-1">
                  Cool Fact:
                </span>
                {recent.coolFact}
              </div>
            </div>
          </div>

          {/* Location and Sources attribution */}
          <div className="mt-3 pt-2 border-t border-slate-100 flex flex-wrap items-center justify-between gap-2 text-[11px] text-slate-400 font-medium">
            <div className="flex items-center gap-1 text-slate-500">
              <MapPin className="w-3 h-3 text-emerald-500" />
              <span>{recent.locationName}</span>
            </div>

            {recent.sources && recent.sources.length > 0 && (
              <div className="flex items-center gap-1.5">
                <span className="text-slate-400">Knowledge source:</span>
                <span className="text-emerald-700 font-semibold inline-flex items-center gap-0.5">
                  {recent.sources[0].source}
                  <ExternalLink className="w-2.5 h-2.5" />
                </span>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
