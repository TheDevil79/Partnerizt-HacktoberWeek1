import React from 'react';
import { Compass, Play, Square, Map, Clock, Sparkles, Navigation, Camera, TreePine } from 'lucide-react';
import { usePartnerizt } from '../../context/PartneriztContext';
import { formatDistance, formatDuration } from '../../services/explorationService';

export const ExplorationHero: React.FC = () => {
  const {
    isExploring,
    activeSession,
    startExploration,
    stopExploration,
    gpsAccuracy,
    locationPermissionStatus,
    quests,
  } = usePartnerizt();

  const activeQuestCount = quests.filter(q => q.status === 'in_progress').length;

  return (
    <div className="relative overflow-hidden rounded-4xl bg-gradient-to-br from-emerald-600 via-emerald-700 to-teal-800 text-white shadow-xl border-b-4 border-emerald-900 transition-all">
      {/* Background Decorative Shapes */}
      <div className="absolute -right-8 -top-8 w-44 h-44 bg-white/10 rounded-full blur-2xl pointer-events-none" />
      <div className="absolute -left-12 -bottom-12 w-48 h-48 bg-teal-400/20 rounded-full blur-3xl pointer-events-none" />
      {/* Subtle leaf pattern overlay */}
      <div className="absolute top-4 right-4 opacity-10 pointer-events-none text-7xl select-none">🌿</div>

      {!isExploring ? (
        /* ── INACTIVE STATE ── */
        <div className="relative z-10 p-5 sm:p-7">
          <div className="flex flex-col sm:flex-row items-center justify-between gap-6">
            <div className="text-center sm:text-left">
              <div className="inline-flex items-center gap-1.5 bg-white/20 backdrop-blur-md px-3 py-1 rounded-full text-xs font-bold text-emerald-100 mb-3">
                <Compass className="w-3.5 h-3.5" />
                <span>Partnerizt — Outdoor Learning</span>
              </div>
              <h1 className="text-2xl sm:text-3xl font-black tracking-tight leading-tight font-sans">
                Step outside.<br />Learn something real.
              </h1>
              <p className="text-emerald-100/90 text-sm mt-2 max-w-sm leading-relaxed font-medium">
                Explore your surroundings, complete outdoor quests, photograph wildlife and landmarks, and unlock knowledge with your AI companions.
              </p>

              {/* What you do outside summary */}
              <div className="flex flex-wrap gap-2 mt-3">
                {[
                  { icon: Map, label: 'Track distance' },
                  { icon: Camera, label: 'Photograph discoveries' },
                  { icon: TreePine, label: 'Complete quests' },
                ].map(({ icon: Icon, label }) => (
                  <span key={label} className="inline-flex items-center gap-1.5 bg-white/15 text-emerald-100 text-[11px] font-bold px-2.5 py-1 rounded-full">
                    <Icon className="w-3 h-3" />
                    {label}
                  </span>
                ))}
              </div>
            </div>

            <div className="w-full sm:w-auto flex-shrink-0 flex flex-col items-center gap-2">
              <button
                onClick={startExploration}
                className="w-full sm:w-auto bg-amber-400 hover:bg-amber-300 text-amber-950 font-black text-base sm:text-lg px-8 py-4 rounded-2xl border-b-4 border-amber-600 active:border-b-0 active:translate-y-1 transition-all shadow-lg flex items-center justify-center gap-3 cursor-pointer select-none"
              >
                <Play className="w-5 h-5 fill-amber-950" />
                <span>START EXPLORING</span>
              </button>
              <span className="text-[11px] text-emerald-200/70 font-medium text-center">
                GPS distance tracking • No step counting
              </span>
            </div>
          </div>
        </div>
      ) : (
        /* ── ACTIVE SESSION HUD ── */
        <div className="relative z-10 p-5 sm:p-7">
          {/* Session status bar */}
          <div className="flex flex-wrap items-center justify-between gap-2 pb-4 border-b border-emerald-500/50">
            <div className="flex items-center gap-2">
              <span className="relative flex h-3 w-3">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-300 opacity-75" />
                <span className="relative inline-flex rounded-full h-3 w-3 bg-emerald-400" />
              </span>
              <span className="text-sm font-black tracking-tight text-white">
                Exploring Now
              </span>
            </div>
            <div className="flex items-center gap-2">
              {locationPermissionStatus === 'denied' && (
                <span className="text-[11px] bg-amber-500/90 text-amber-950 px-2.5 py-1 rounded-full font-bold">
                  ⚠️ GPS Disabled
                </span>
              )}
              {gpsAccuracy !== null && locationPermissionStatus !== 'denied' && (
                <span className="text-[11px] bg-emerald-800/80 px-2.5 py-1 rounded-full font-semibold text-emerald-200 flex items-center gap-1">
                  <Navigation className="w-3 h-3 text-emerald-300" />
                  GPS ±{Math.round(gpsAccuracy)}m
                </span>
              )}
              {activeQuestCount > 0 && (
                <span className="text-[11px] bg-amber-500/90 text-amber-950 font-black px-2.5 py-1 rounded-full">
                  {activeQuestCount} quest{activeQuestCount > 1 ? 's' : ''} active
                </span>
              )}
            </div>
          </div>

          {/* Primary Metrics — Distance is the hero number */}
          <div className="mt-5 mb-3">
            <div className="text-[11px] font-bold text-emerald-300 uppercase tracking-widest mb-1 flex items-center gap-1.5">
              <Map className="w-3.5 h-3.5" /> Distance Explored
            </div>
            <div className="text-5xl sm:text-6xl font-black tracking-tight leading-none">
              {activeSession ? formatDistance(activeSession.distanceKm) : '0 m'}
            </div>
          </div>

          {/* Secondary metrics grid */}
          <div className="grid grid-cols-3 gap-2 mb-5">
            <div className="bg-white/10 backdrop-blur-md rounded-2xl p-2.5 border border-white/15">
              <span className="text-[10px] font-bold text-emerald-300 uppercase tracking-wider flex items-center gap-1">
                <Clock className="w-3 h-3" /> Time
              </span>
              <div className="text-lg font-black mt-0.5 font-mono tracking-tight">
                {activeSession ? formatDuration(activeSession.durationSeconds) : '00:00'}
              </div>
            </div>

            <div className="bg-white/10 backdrop-blur-md rounded-2xl p-2.5 border border-white/15">
              <span className="text-[10px] font-bold text-emerald-300 uppercase tracking-wider flex items-center gap-1">
                <Sparkles className="w-3 h-3" /> Quests
              </span>
              <div className="text-lg font-black mt-0.5">
                {activeSession?.questsCompletedCount || 0} done
              </div>
            </div>

            <div className="bg-white/10 backdrop-blur-md rounded-2xl p-2.5 border border-white/15">
              <span className="text-[10px] font-bold text-emerald-300 uppercase tracking-wider">
                ⭐ Session XP
              </span>
              <div className="text-lg font-black mt-0.5 text-amber-300">
                +{activeSession ? Math.max(15, Math.round(activeSession.distanceKm * 40)) : 15}
              </div>
            </div>
          </div>

          {/* Controls */}
          <div className="flex items-center justify-end gap-3">
            <button
              onClick={stopExploration}
              className="w-full sm:w-auto bg-rose-500 hover:bg-rose-600 text-white font-black text-sm px-7 py-3.5 rounded-2xl border-b-4 border-rose-800 active:border-b-0 active:translate-y-1 transition-all shadow-md flex items-center justify-center gap-2 cursor-pointer"
            >
              <Square className="w-4 h-4 fill-white" />
              <span>STOP & SAVE SESSION</span>
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
