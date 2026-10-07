import React from 'react';
import { Camera, MapPin, Sparkles, ExternalLink } from 'lucide-react';
import { usePartnerizt } from '../../context/PartneriztContext';
import { CharacterAvatar } from '../../assets/characterAvatars';

export const DiscoveryGallery: React.FC = () => {
  const { discoveries, openCharacterChat } = usePartnerizt();

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <h3 className="text-base font-black text-slate-900 font-sans tracking-tight flex items-center gap-1.5">
          <Camera className="w-4 h-4 text-purple-600" /> Discovery Log ({discoveries.length})
        </h3>
        <span className="text-xs text-slate-400 font-medium">Real-world catalog</span>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {discoveries.map((disc) => (
          <div
            key={disc.id}
            className="card-duo p-4 flex flex-col justify-between hover:border-emerald-300 transition-all"
          >
            <div>
              {/* Photo */}
              <div className="relative h-44 rounded-2xl overflow-hidden bg-slate-900 border border-slate-200 group mb-3">
                <img
                  src={disc.imageUrl}
                  alt={disc.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                />
                <div className="absolute top-2 left-2 bg-slate-900/80 backdrop-blur-sm text-white text-[10px] font-bold px-2 py-0.5 rounded-md">
                  {disc.category}
                </div>
                <div className="absolute bottom-2 right-2 bg-emerald-600 text-white text-[10px] font-black px-2 py-0.5 rounded-md flex items-center gap-1">
                  <Sparkles className="w-3 h-3" /> +{disc.xpEarned} XP
                </div>
              </div>

              {/* Title & Scientific name */}
              <div className="flex items-start justify-between gap-2">
                <div>
                  <h4 className="font-bold text-slate-900 text-base leading-snug">
                    {disc.title}
                  </h4>
                  {disc.scientificName && (
                    <p className="text-xs italic text-emerald-700 font-serif font-medium">
                      {disc.scientificName}
                    </p>
                  )}
                </div>
                <div
                  onClick={() => openCharacterChat(disc.characterId)}
                  className="cursor-pointer hover:scale-110 transition-transform flex-shrink-0"
                  title={`Chat with ${disc.characterId.toUpperCase()}`}
                >
                  <CharacterAvatar characterId={disc.characterId} size={36} />
                </div>
              </div>

              <p className="text-xs text-slate-600 mt-2 leading-relaxed line-clamp-2">
                {disc.explanation}
              </p>
            </div>

            {/* Footer */}
            <div className="mt-3 pt-2.5 border-t border-slate-100 flex items-center justify-between text-[11px] text-slate-400">
              <div className="flex items-center gap-1 text-slate-500">
                <MapPin className="w-3 h-3 text-emerald-500" />
                <span>{disc.locationName}</span>
              </div>

              {disc.sources && disc.sources.length > 0 && (
                <span className="text-emerald-700 font-semibold inline-flex items-center gap-0.5">
                  {disc.sources[0].source}
                  <ExternalLink className="w-2.5 h-2.5" />
                </span>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
