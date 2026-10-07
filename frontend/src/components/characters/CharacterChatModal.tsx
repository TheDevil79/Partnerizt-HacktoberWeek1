import React, { useState, useEffect, useRef } from 'react';
import { Send, Sparkles, Compass, Globe, X, Volume2, Loader2 } from 'lucide-react';
import { CharacterId, CharacterMessage } from '../../types';
import { CharacterAvatar } from '../../assets/characterAvatars';
import { MOCK_CHARACTERS } from '../../services/mockData';
import { api } from '../../services/api';
import { soundService } from '../../services/soundService';

interface CharacterChatModalProps {
  characterId: CharacterId;
  isOpen: boolean;
  onClose: () => void;
}

export const CharacterChatModal: React.FC<CharacterChatModalProps> = ({
  characterId,
  isOpen,
  onClose,
}) => {
  const character = MOCK_CHARACTERS.find((c) => c.id === characterId) || MOCK_CHARACTERS[0];
  const [messages, setMessages] = useState<CharacterMessage[]>([]);
  const [inputText, setInputText] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const [speakingMsgId, setSpeakingMsgId] = useState<string | null>(null);
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);


  // Initial greeting from character tailored to domain
  useEffect(() => {
    if (isOpen) {
      setMessages([
        {
          id: `greet_${character.id}`,
          sender: 'character',
          characterId: character.id,
          text: `Hey outdoor explorer! I'm ${character.name}. Ask me anything about ${character.domain.toLowerCase()} that you spot outside!`,
          suggestedActivity: character.sampleOutdoorActivities[0],
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        },
      ]);
    }
  }, [character.id, isOpen]);

  // Scroll to bottom when messages update
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isTyping]);

  if (!isOpen) return null;

  const handleSendMessage = async (textToSend?: string) => {
    const text = (textToSend || inputText).trim();
    if (!text || isTyping) return;

    soundService.playTap();

    const userMsg: CharacterMessage = {
      id: `msg_u_${Date.now()}`,
      sender: 'user',
      text,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };

    setMessages((prev) => [...prev, userMsg]);
    setInputText('');
    setIsTyping(true);

    try {
      const response = await api.sendCharacterMessage(character.id, text);
      setIsTyping(false);
      setMessages((prev) => [...prev, response]);
      soundService.playTap();
    } catch {
      setIsTyping(false);
    }
  };

  const handlePlayMessageVoice = async (msgId: string, text: string) => {
    if (speakingMsgId === msgId) {
      if (audioRef.current) {
        audioRef.current.pause();
        audioRef.current.currentTime = 0;
      }
      setSpeakingMsgId(null);
      return;
    }

    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current = null;
    }

    try {
      setSpeakingMsgId(msgId);
      const audioBlob = await api.speakCompanionText(text, character.id);
      if (!audioBlob) {
        setSpeakingMsgId(null);
        return;
      }
      const audioUrl = URL.createObjectURL(audioBlob);
      const audio = new Audio(audioUrl);
      audioRef.current = audio;
      audio.onended = () => {
        setSpeakingMsgId(null);
        URL.revokeObjectURL(audioUrl);
      };
      audio.onerror = () => {
        setSpeakingMsgId(null);
        URL.revokeObjectURL(audioUrl);
      };
      await audio.play();
    } catch {
      setSpeakingMsgId(null);
    }
  };

  const handleClose = () => {
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current = null;
    }
    setSpeakingMsgId(null);
    onClose();
  };


  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-2 sm:p-4 bg-slate-900/60 backdrop-blur-sm">
      <div className="bg-white rounded-4xl w-full max-w-lg h-[90vh] sm:h-[82vh] shadow-2xl border border-slate-100 flex flex-col overflow-hidden">
        {/* Chat Header */}
        <div className="px-4 py-3.5 bg-gradient-to-r from-emerald-50 via-teal-50 to-white border-b border-slate-200/80 flex items-center justify-between flex-shrink-0">
          <div className="flex items-center gap-3">
            <CharacterAvatar characterId={character.id} size={44} animated={true} />
            <div>
              <div className="flex items-center gap-1.5">
                <h3 className="text-base font-black text-slate-900 font-sans leading-none">
                  {character.name}
                </h3>
                <span className="bg-emerald-600 text-white text-[10px] font-black px-1.5 py-0.5 rounded-full uppercase tracking-wider">
                  Field Expert
                </span>
              </div>
              <p className="text-xs text-emerald-800 font-medium mt-0.5">
                {character.domain}
              </p>
            </div>
          </div>

          <button
            onClick={handleClose}
            className="p-1.5 rounded-full hover:bg-slate-200/60 text-slate-500 hover:text-slate-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Message Stream */}
        <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-slate-50/50">
          {messages.map((msg) => {
            const isUser = msg.sender === 'user';
            return (
              <div
                key={msg.id}
                className={`flex gap-2.5 ${isUser ? 'justify-end' : 'justify-start'}`}
              >
                {!isUser && (
                  <div className="flex-shrink-0 mt-1">
                    <CharacterAvatar characterId={character.id} size={32} />
                  </div>
                )}

                <div
                  className={`max-w-[85%] rounded-3xl p-3.5 text-xs sm:text-sm font-medium leading-relaxed ${
                    isUser
                      ? 'bg-emerald-600 text-white rounded-br-sm shadow-md'
                      : 'bg-white text-slate-800 border border-slate-200 rounded-bl-sm shadow-sm'
                  }`}
                >
                  <p>{msg.text}</p>

                  {/* Suggested Outdoor Activity Box */}
                  {!isUser && msg.suggestedActivity && (
                    <div className="mt-2.5 bg-emerald-50/90 border border-emerald-200/90 rounded-2xl p-2.5 text-emerald-950 text-xs">
                      <div className="flex items-center gap-1 font-bold text-emerald-800 mb-0.5 text-[11px] uppercase tracking-wider">
                        <Compass className="w-3 h-3 text-emerald-600" /> Outdoor Activity:
                      </div>
                      <span>{msg.suggestedActivity}</span>
                    </div>
                  )}

                  {/* Knowledge Source references */}
                  {!isUser && msg.sourceReferences && msg.sourceReferences.length > 0 && (
                    <div className="mt-2 pt-1.5 border-t border-slate-100 flex items-center gap-1 text-[10px] text-slate-400">
                      <Globe className="w-3 h-3 text-emerald-500" />
                      <span>Verified source: {msg.sourceReferences[0].source}</span>
                    </div>
                  )}

                  <div className="flex items-center justify-between gap-2 mt-2 pt-1 border-t border-slate-100/60">
                    {!isUser ? (
                      <button
                        onClick={() => handlePlayMessageVoice(msg.id, msg.text)}
                        className={`inline-flex items-center gap-1 text-[10px] px-2 py-0.5 rounded-lg font-bold transition-all ${
                          speakingMsgId === msg.id
                            ? 'bg-emerald-600 text-white animate-pulse'
                            : 'bg-emerald-50 hover:bg-emerald-100 text-emerald-800 border border-emerald-200/60'
                        }`}
                      >
                        {speakingMsgId === msg.id ? (
                          <>
                            <Loader2 className="w-2.5 h-2.5 animate-spin" />
                            <span>Playing...</span>
                          </>
                        ) : (
                          <>
                            <Volume2 className="w-2.5 h-2.5" />
                            <span>🔊 Listen</span>
                          </>
                        )}
                      </button>
                    ) : (
                      <div />
                    )}
                    <span
                      className={`text-[10px] font-normal ${
                        isUser ? 'text-emerald-100' : 'text-slate-400'
                      }`}
                    >
                      {msg.timestamp}
                    </span>
                  </div>
                </div>
              </div>
            );
          })}


          {/* Typing indicator */}
          {isTyping && (
            <div className="flex items-center gap-2 text-slate-400 text-xs pl-2">
              <CharacterAvatar characterId={character.id} size={28} />
              <div className="bg-white border border-slate-200 rounded-full px-3 py-1.5 flex items-center gap-1 shadow-sm">
                <span className="w-1.5 h-1.5 bg-emerald-500 rounded-full animate-bounce" />
                <span className="w-1.5 h-1.5 bg-emerald-500 rounded-full animate-bounce [animation-delay:0.2s]" />
                <span className="w-1.5 h-1.5 bg-emerald-500 rounded-full animate-bounce [animation-delay:0.4s]" />
              </div>
              <span className="text-[11px] font-medium text-slate-500">
                {character.name} is thinking...
              </span>
            </div>
          )}

          <div ref={messagesEndRef} />
        </div>

        {/* Quick Suggestion Chips */}
        <div className="px-3 py-2 bg-white border-t border-slate-100 flex items-center gap-1.5 overflow-x-auto scrollbar-none flex-shrink-0">
          <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider flex items-center gap-1 flex-shrink-0">
            <Sparkles className="w-3 h-3 text-amber-500" /> Spotted something?
          </span>
          {character.sampleQuestions.map((q, idx) => (
            <button
              key={idx}
              onClick={() => handleSendMessage(q)}
              className="text-xs bg-slate-100 hover:bg-emerald-50 hover:text-emerald-800 text-slate-700 font-medium px-2.5 py-1 rounded-xl whitespace-nowrap border border-slate-200 transition-colors"
            >
              {q}
            </button>
          ))}
        </div>

        {/* Text Input Footer */}
        <div className="p-3 bg-white border-t border-slate-200 flex items-center gap-2 flex-shrink-0">
          <input
            type="text"
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter') handleSendMessage();
            }}
            placeholder={`What did you find outside? Ask ${character.name}...`}
            className="flex-1 bg-slate-100 focus:bg-white border border-slate-200 focus:border-emerald-500 rounded-2xl px-4 py-2.5 text-xs sm:text-sm text-slate-800 placeholder:text-slate-400 focus:outline-none transition-all" />

          <button
            onClick={() => handleSendMessage()}
            disabled={!inputText.trim() || isTyping}
            className="p-3 rounded-2xl bg-emerald-500 hover:bg-emerald-600 disabled:opacity-50 text-white font-bold transition-all shadow-duo-primary border-b-2 border-emerald-700 active:translate-y-0.5"
            title="Send Question"
          >
            <Send className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
};
