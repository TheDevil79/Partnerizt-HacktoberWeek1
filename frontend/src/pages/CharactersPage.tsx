import React from 'react';
import { Users, Compass, MessageSquare } from 'lucide-react';
import { usePartnerizt } from '../context/PartneriztContext';
import { CharacterCard } from '../components/characters/CharacterCard';

export const CharactersPage: React.FC = () => {
  const { characters } = usePartnerizt();

  return (
    <div className="space-y-4 pb-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
        <div>
          <h1 className="text-2xl font-black text-slate-900 font-sans tracking-tight flex items-center gap-2">
            <Users className="w-6 h-6 text-emerald-600" />
            <span>Your Field Companions</span>
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 font-medium mt-0.5">
            Domain experts who explain what you find outside — not generic chatbots
          </p>
        </div>
      </div>

      {/* How it works explainer — framed around the outdoor loop */}
      <div className="bg-gradient-to-br from-emerald-600 to-teal-700 text-white rounded-3xl p-5 shadow-sm">
        <h2 className="text-sm font-black uppercase tracking-wider text-emerald-100 mb-3 flex items-center gap-2">
          <Compass className="w-4 h-4 text-amber-300" />
          How companion learning works
        </h2>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
          {[
            {
              step: '1',
              label: 'Go outside & discover',
              desc: 'Find animals, plants, buildings, or anything interesting.',
            },
            {
              step: '2',
              label: 'Photograph it',
              desc: 'Take a photo and let Gemma identify what you found.',
            },
            {
              step: '3',
              label: 'Your companion explains',
              desc: 'The right companion gives you real facts and an outdoor activity to try.',
            },
          ].map(({ step, label, desc }) => (
            <div key={step} className="bg-white/15 rounded-2xl p-3.5 flex gap-3 items-start">
              <span className="text-2xl font-black text-amber-300 leading-none flex-shrink-0">{step}</span>
              <div>
                <div className="text-sm font-bold text-white leading-snug">{label}</div>
                <div className="text-xs text-emerald-100/80 mt-0.5 leading-relaxed">{desc}</div>
              </div>
            </div>
          ))}
        </div>
        <p className="text-[11px] text-emerald-200/70 mt-3 font-medium flex items-center gap-1.5">
          <MessageSquare className="w-3 h-3" />
          You can also ask any companion a question directly — they'll stay strictly within their expertise.
        </p>
      </div>

      {/* Characters grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {characters.map((character) => (
          <CharacterCard key={character.id} character={character} />
        ))}
      </div>
    </div>
  );
};
