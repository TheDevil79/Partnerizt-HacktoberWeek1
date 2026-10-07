import React from 'react';
import { Outlet } from 'react-router-dom';
import { Header } from '../components/common/Header';
import { BottomNav, DesktopNav } from './BottomNav';
import { ConfettiCelebration } from '../components/common/ConfettiCelebration';
import { CharacterChatModal } from '../components/characters/CharacterChatModal';
import { usePartnerizt } from '../context/PartneriztContext';

export const MainLayout: React.FC = () => {
  const { activeChatCharacterId, closeCharacterChat } = usePartnerizt();

  return (
    <div className="min-h-screen flex flex-col bg-[#FAF8F5] text-slate-800 pb-20 md:pb-8">
      {/* Top Header */}
      <Header />

      {/* Desktop Navigation Bar (shown below header on medium/large screens) */}
      <div className="hidden md:block bg-white/60 border-b border-slate-200/60 py-2.5 px-4 backdrop-blur-sm">
        <div className="max-w-4xl mx-auto flex items-center justify-between">
          <DesktopNav />
          <div className="text-xs font-semibold text-emerald-800 bg-emerald-50 border border-emerald-200/80 px-3 py-1 rounded-full flex items-center gap-1.5">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            <span>The Duolingo for learning from your environment</span>
          </div>
        </div>
      </div>

      {/* Main Content Area */}
      <main className="flex-1 max-w-4xl w-full mx-auto px-4 py-4 sm:py-6">
        <Outlet />
      </main>

      {/* Mobile-first Bottom Navigation Bar */}
      <BottomNav />

      {/* Global Reward & Confetti Celebration Overlay */}
      <ConfettiCelebration />

      {/* Global Character Chat Modal */}
      {activeChatCharacterId && (
        <CharacterChatModal
          characterId={activeChatCharacterId}
          isOpen={true}
          onClose={closeCharacterChat}
        />
      )}
    </div>
  );
};
