import React from 'react';
import { Sparkles, Coins, CheckCircle, ArrowRight } from 'lucide-react';
import { usePartnerizt } from '../../context/PartneriztContext';
import { CharacterAvatar } from '../../assets/characterAvatars';

export const ConfettiCelebration: React.FC = () => {
  const { celebration, closeCelebration } = usePartnerizt();

  if (!celebration) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm animate-fade-in">
      <div className="bg-white rounded-4xl max-w-sm w-full p-6 text-center border-4 border-emerald-400 shadow-2xl relative overflow-hidden transform scale-100 transition-transform">
        {/* Decorative Top Glow */}
        <div className="absolute top-0 left-0 right-0 h-28 bg-gradient-to-b from-emerald-100 via-emerald-50/40 to-transparent -z-0" />

        <div className="relative z-10 flex flex-col items-center">
          {/* Character or Trophy Avatar */}
          <div className="relative my-2">
            {celebration.characterId ? (
              <div className="p-2 rounded-full bg-white shadow-lg border-2 border-emerald-300">
                <CharacterAvatar characterId={celebration.characterId} size={84} animated={true} />
              </div>
            ) : (
              <div className="w-20 h-20 rounded-3xl bg-gradient-to-tr from-amber-400 to-yellow-300 flex items-center justify-center shadow-lg border-2 border-amber-500 text-white animate-bounce-soft">
                <Sparkles className="w-10 h-10" />
              </div>
            )}
            <div className="absolute -bottom-1 -right-1 bg-emerald-500 text-white rounded-full p-1.5 shadow-md">
              <CheckCircle className="w-5 h-5" />
            </div>
          </div>

          <h3 className="text-2xl font-black text-slate-900 mt-2 font-sans tracking-tight">
            {celebration.title}
          </h3>
          <p className="text-sm text-slate-600 font-medium mt-1 mb-4 px-2">
            {celebration.subtitle}
          </p>

          {/* Rewards Grid */}
          <div className="grid grid-cols-2 gap-3 w-full my-3">
            {/* XP Award */}
            <div className="bg-emerald-50 border-2 border-emerald-200 rounded-2xl p-3 flex flex-col items-center">
              <span className="text-xs font-bold text-emerald-700 uppercase tracking-wider flex items-center gap-1">
                <Sparkles className="w-3.5 h-3.5" /> Earned
              </span>
              <span className="text-2xl font-black text-emerald-800 mt-0.5">
                +{celebration.xpEarned} <span className="text-sm font-bold">XP</span>
              </span>
            </div>

            {/* Coins Award */}
            <div className="bg-amber-50 border-2 border-amber-200 rounded-2xl p-3 flex flex-col items-center">
              <span className="text-xs font-bold text-amber-700 uppercase tracking-wider flex items-center gap-1">
                <Coins className="w-3.5 h-3.5" /> Coins
              </span>
              <span className="text-2xl font-black text-amber-800 mt-0.5">
                +{celebration.coinsEarned}
              </span>
            </div>
          </div>

          {/* Continue Button */}
          <button
            onClick={closeCelebration}
            className="btn-duo-primary w-full mt-4 text-base py-3.5"
          >
            <span>Awesome! Continue</span>
            <ArrowRight className="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>
  );
};
