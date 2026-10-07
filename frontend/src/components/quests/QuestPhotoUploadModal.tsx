import React, { useState, useRef, useEffect } from 'react';
import { Camera, Upload, CheckCircle2, ArrowRight, Loader2, Globe, Bot, AlertTriangle, ExternalLink, RefreshCw, X, AlertCircle, Volume2 } from 'lucide-react';
import { Quest, IdentificationResult } from '../../types';
import { CharacterAvatar } from '../../assets/characterAvatars';
import { usePartnerizt } from '../../context/PartneriztContext';
import { api } from '../../services/api';

interface QuestPhotoUploadModalProps {
  quest: Quest;
  isOpen: boolean;
  onClose: () => void;
}

type AnalysisStep = 'idle' | 'gemma_identifying' | 'serpapi_knowledge' | 'character_explaining' | 'complete' | 'error';
type VoiceState = 'idle' | 'generating' | 'playing' | 'error';

export const QuestPhotoUploadModal: React.FC<QuestPhotoUploadModalProps> = ({
  quest,
  isOpen,
  onClose,
}) => {
  const { completeQuest } = usePartnerizt();
  const cameraInputRef = useRef<HTMLInputElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [previewImage, setPreviewImage] = useState<string | null>(null);
  const [step, setStep] = useState<AnalysisStep>('idle');
  const [result, setResult] = useState<IdentificationResult | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isDragging, setIsDragging] = useState<boolean>(false);

  const [voiceState, setVoiceState] = useState<VoiceState>('idle');
  const [voiceErrorMessage, setVoiceErrorMessage] = useState<string | null>(null);
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const audioUrlRef = useRef<string | null>(null);


  if (!isOpen) return null;

  // Compress & rescale high-res mobile photos to prevent huge payloads
  const processAndCompressImage = (file: File) => {
    if (!file.type.startsWith('image/')) {
      setErrorMessage('Please select a valid image file (JPEG, PNG, or WebP).');
      return;
    }

    if (file.size > 15 * 1024 * 1024) {
      setErrorMessage('Photo is too large (maximum 15MB). Please take a new photo.');
      return;
    }

    setErrorMessage(null);
    const reader = new FileReader();
    reader.onerror = () => {
      setErrorMessage('Failed to read image file from your device.');
    };
    reader.onload = (event) => {
      const img = new Image();
      img.onerror = () => {
        setErrorMessage('Could not load image. Please select a valid photo.');
      };
      img.onload = () => {
        const maxDim = 1200;
        let width = img.width;
        let height = img.height;
        if (width > maxDim || height > maxDim) {
          if (width > height) {
            height = Math.round((height * maxDim) / width);
            width = maxDim;
          } else {
            width = Math.round((width * maxDim) / height);
            height = maxDim;
          }
        }
        const canvas = document.createElement('canvas');
        canvas.width = width;
        canvas.height = height;
        const ctx = canvas.getContext('2d');
        if (ctx) {
          ctx.drawImage(img, 0, 0, width, height);
          const compressed = canvas.toDataURL('image/jpeg', 0.80);
          setPreviewImage(compressed);
        } else {
          setPreviewImage(event.target?.result as string);
        }
        setStep('idle');
        setResult(null);
      };
      img.src = event.target?.result as string;
    };
    reader.readAsDataURL(file);
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      processAndCompressImage(file);
    }
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    const file = e.dataTransfer.files?.[0];
    if (file) {
      processAndCompressImage(file);
    }
  };

  // Sample quick test images for desktop/offline simulation
  const setSampleImage = (category: string) => {
    setErrorMessage(null);
    if (category === 'plant') {
      setPreviewImage('https://images.unsplash.com/photo-1528183429752-a97d0bf99b5a?auto=format&fit=crop&w=600&q=80');
    } else if (category === 'monument' || category === 'architecture') {
      setPreviewImage('https://images.unsplash.com/photo-1548625361-195fe57876a2?auto=format&fit=crop&w=600&q=80');
    } else if (category === 'food') {
      setPreviewImage('https://images.unsplash.com/photo-1515586000433-a5ac7d18a436?auto=format&fit=crop&w=600&q=80');
    } else if (category === 'geology') {
      setPreviewImage('https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=600&q=80');
    } else {
      setPreviewImage('https://images.unsplash.com/photo-1507667522111-bf5a34e00517?auto=format&fit=crop&w=600&q=80');
    }
    setStep('idle');
    setResult(null);
  };

  // Run the multi-stage AI discovery pipeline
  const runAiPipeline = async () => {
    if (!previewImage) return;

    try {
      setErrorMessage(null);

      // Stage 1: Gemma visual identification
      setStep('gemma_identifying');
      await new Promise((r) => setTimeout(r, 650));

      // Stage 2: SerpApi factual knowledge enrichment
      setStep('serpapi_knowledge');
      await new Promise((r) => setTimeout(r, 650));

      // Stage 3: Character persona synthesis
      setStep('character_explaining');
      const analysis = await api.analyzePhotoDiscovery(previewImage, quest.category, quest.characterId);
      setResult(analysis);
      await new Promise((r) => setTimeout(r, 450));

      setStep('complete');
    } catch (err: any) {
      console.error('AI pipeline error', err);
      setErrorMessage(err?.message || 'Failed to complete AI discovery. Please check network connection and try again.');
      setStep('error');
    }
  };

  // Audio playback management for ElevenLabs TTS
  const stopAndCleanAudio = () => {
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.currentTime = 0;
      audioRef.current = null;
    }
    if (audioUrlRef.current) {
      URL.revokeObjectURL(audioUrlRef.current);
      audioUrlRef.current = null;
    }
    setVoiceState('idle');
    setVoiceErrorMessage(null);
  };

  useEffect(() => {
    return () => {
      stopAndCleanAudio();
    };
  }, []);

  const handleListenCommentary = async () => {
    if (!result || !result.explanation) return;

    // Toggle playback if already playing
    if (voiceState === 'playing' && audioRef.current) {
      audioRef.current.pause();
      audioRef.current.currentTime = 0;
      setVoiceState('idle');
      return;
    }

    // Replay cached audio if available
    if (audioUrlRef.current && audioRef.current) {
      try {
        setVoiceState('playing');
        audioRef.current.currentTime = 0;
        await audioRef.current.play();
      } catch (e) {
        console.warn('Playback error', e);
        setVoiceState('error');
        setVoiceErrorMessage('Voice unavailable — you can still read the discovery.');
      }
      return;
    }

    try {
      setVoiceState('generating');
      setVoiceErrorMessage(null);

      // Preserve safety disclaimer in Munch / foraging spoken commentary
      const textToSpeak = result.safetyDisclaimer
        ? `${result.explanation} ${result.safetyDisclaimer}`
        : result.explanation;

      const audioBlob = await api.speakCompanionText(textToSpeak, quest.characterId);

      if (!audioBlob) {
        setVoiceState('error');
        setVoiceErrorMessage('Voice unavailable — you can still read the discovery.');
        return;
      }

      const audioUrl = URL.createObjectURL(audioBlob);
      audioUrlRef.current = audioUrl;

      const audio = new Audio(audioUrl);
      audioRef.current = audio;

      audio.onended = () => {
        setVoiceState('idle');
      };

      audio.onerror = () => {
        setVoiceState('error');
        setVoiceErrorMessage('Voice unavailable — you can still read the discovery.');
      };

      setVoiceState('playing');
      await audio.play();
    } catch (err: any) {
      console.error('TTS speech generation error', err);
      setVoiceState('error');
      setVoiceErrorMessage('Voice unavailable — you can still read the discovery.');
    }
  };

  // Claim quest reward
  const handleClaimReward = async () => {
    stopAndCleanAudio();
    if (previewImage) {
      await completeQuest(
        quest.id,
        previewImage,
        result
          ? {
              title: result.title,
              scientificName: result.scientificName,
              explanation: result.explanation,
              coolFact: result.coolFact,
              sources: result.sources,
              category: result.domain || quest.category.toUpperCase(),
              characterId: quest.characterId,
              xpEarned: result.xpEarned || quest.xpReward,
              coinsEarned: result.coinsEarned || quest.coinReward,
            }
          : undefined
      );
      onClose();
    }
  };

  const handleRetake = () => {
    stopAndCleanAudio();
    setPreviewImage(null);
    setResult(null);
    setStep('idle');
    setErrorMessage(null);
  };

  const handleModalClose = () => {
    stopAndCleanAudio();
    onClose();
  };

  const isLowConfidence = result !== null && (result.confidence === undefined || result.confidence < 0.60);


  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4 bg-slate-900/60 backdrop-blur-sm overflow-y-auto">
      <div className="bg-white rounded-4xl w-full max-w-lg p-5 sm:p-6 shadow-2xl border border-slate-100 relative max-h-[92vh] flex flex-col my-auto">
        {/* Modal Header */}
        <div className="flex items-center justify-between pb-3 border-b border-slate-100 mb-4 flex-shrink-0">
          <div className="flex items-center gap-2">
            <span className="text-xl">{quest.iconEmoji}</span>
            <div>
              <h3 className="text-base sm:text-lg font-black text-slate-900 leading-tight">
                Outdoor Photo Discovery
              </h3>
              <p className="text-xs text-slate-500 font-medium">
                {quest.title}
              </p>
            </div>
          </div>
          <button
            onClick={handleModalClose}
            className="p-1.5 rounded-full hover:bg-slate-100 text-slate-400 hover:text-slate-700 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="overflow-y-auto flex-1 space-y-4">
          {/* Error Banner */}
          {errorMessage && (
            <div className="bg-rose-50 border border-rose-200 rounded-2xl p-3 flex items-start gap-2 text-xs text-rose-800 font-medium">
              <AlertCircle className="w-4 h-4 text-rose-600 flex-shrink-0 mt-0.5" />
              <div className="flex-1">
                <span>{errorMessage}</span>
              </div>
            </div>
          )}

          {/* STEP 1: UPLOAD / CAMERA SELECTION */}
          {!previewImage ? (
            <div
              onDragOver={(e) => { e.preventDefault(); setIsDragging(true); }}
              onDragLeave={() => setIsDragging(false)}
              onDrop={handleDrop}
              className={`flex flex-col items-center justify-center border-2 border-dashed rounded-3xl p-6 sm:p-8 text-center transition-colors ${
                isDragging ? 'border-emerald-500 bg-emerald-50/50' : 'border-slate-300 bg-slate-50/70'
              }`}
            >
              <div className="w-16 h-16 rounded-3xl bg-emerald-100 flex items-center justify-center text-emerald-600 mb-3 shadow-sm">
                <Camera className="w-8 h-8" />
              </div>

              <h4 className="text-base font-bold text-slate-800">
                Take or Upload an Outdoor Photo
              </h4>
              <p className="text-xs text-slate-500 max-w-xs mt-1 leading-relaxed">
                Take a clear photo of your outdoor find. Gemma will identify it and retrieve verified real-world facts!
              </p>

              {/* Native mobile camera input */}
              <input
                ref={cameraInputRef}
                type="file"
                accept="image/*"
                capture="environment"
                onChange={handleFileChange}
                className="hidden"
              />

              {/* Standard desktop file input */}
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                className="hidden"
              />

              <div className="flex flex-wrap gap-2 justify-center mt-5 w-full">
                <button
                  onClick={() => cameraInputRef.current?.click()}
                  className="btn-duo-primary text-sm py-2.5 px-5"
                >
                  <Camera className="w-4 h-4" />
                  <span>Take Photo (Camera)</span>
                </button>

                <button
                  onClick={() => fileInputRef.current?.click()}
                  className="btn-duo-secondary text-xs py-2 px-3"
                >
                  <Upload className="w-3.5 h-3.5 text-slate-500" />
                  <span>Choose from Library / Files</span>
                </button>

                <button
                  onClick={() => setSampleImage(quest.category)}
                  className="text-[11px] text-slate-400 hover:text-slate-600 underline py-1 px-2 w-full text-center"
                >
                  Use Sample Test Photo
                </button>
              </div>
            </div>
          ) : (
            /* STEP 2: IMAGE PREVIEW & PIPELINE PROGRESSION */
            <div className="space-y-4">
              <div className="relative rounded-3xl overflow-hidden bg-slate-900 border border-slate-200 max-h-56">
                <img
                  src={previewImage}
                  alt="Outdoor Discovery"
                  className="w-full h-56 object-cover"
                />
                <button
                  onClick={handleRetake}
                  className="absolute top-2 right-2 bg-slate-900/80 hover:bg-slate-900 text-white text-xs px-2.5 py-1.5 rounded-xl font-bold flex items-center gap-1 backdrop-blur-sm transition-colors"
                >
                  <RefreshCw className="w-3 h-3" />
                  <span>Retake / Change</span>
                </button>
              </div>

              {/* PIPELINE PROGRESS INDICATOR */}
              {step !== 'idle' && step !== 'complete' && step !== 'error' && (
                <div className="bg-slate-50 rounded-3xl p-4 border border-slate-200 space-y-3">
                  <div className="flex items-center gap-3">
                    <Loader2 className="w-5 h-5 text-emerald-600 animate-spin flex-shrink-0" />
                    <div>
                      <h4 className="text-sm font-bold text-slate-900">
                        {step === 'gemma_identifying' && '1. Gemma Vision: Analyzing Visual Features...'}
                        {step === 'serpapi_knowledge' && '2. SerpApi: Retrieving Real-World Web Sources...'}
                        {step === 'character_explaining' && `3. ${quest.characterId.toUpperCase()}: Synthesizing Field Notes...`}
                      </h4>
                      <p className="text-xs text-slate-500">
                        {step === 'gemma_identifying' && 'Extracting morphological traits, species details, and confidence'}
                        {step === 'serpapi_knowledge' && 'Cross-referencing verified scientific and encyclopedic sources'}
                        {step === 'character_explaining' && 'Formulating actionable field commentary and outdoor facts'}
                      </p>
                    </div>
                  </div>

                  {/* Visual pipeline stages */}
                  <div className="grid grid-cols-3 gap-1.5 pt-2">
                    <div className={`h-1.5 rounded-full ${step === 'gemma_identifying' ? 'bg-emerald-500 animate-pulse' : 'bg-emerald-500'}`} />
                    <div className={`h-1.5 rounded-full ${step === 'serpapi_knowledge' ? 'bg-emerald-500 animate-pulse' : step === 'character_explaining' ? 'bg-emerald-500' : 'bg-slate-200'}`} />
                    <div className={`h-1.5 rounded-full ${step === 'character_explaining' ? 'bg-emerald-500 animate-pulse' : 'bg-slate-200'}`} />
                  </div>
                </div>
              )}

              {/* PIPELINE COMPLETE: LOW CONFIDENCE (< 0.60) GATE */}
              {step === 'complete' && result && isLowConfidence && (
                <div className="bg-amber-50 border-2 border-amber-300 rounded-3xl p-4 sm:p-5 space-y-3 text-center">
                  <div className="w-12 h-12 rounded-full bg-amber-100 text-amber-700 flex items-center justify-center mx-auto">
                    <AlertTriangle className="w-6 h-6" />
                  </div>
                  <h4 className="text-base font-black text-amber-950">
                    Uncertain Identification
                  </h4>
                  <p className="text-xs sm:text-sm text-amber-900 font-medium leading-relaxed max-w-sm mx-auto">
                    I couldn't confidently identify this outdoor specimen (confidence is below 60%). Please try taking a clearer photo closer to the subject with good natural lighting.
                  </p>
                  <button
                    onClick={handleRetake}
                    className="btn-duo-primary w-full text-sm py-2.5 mt-2"
                  >
                    <Camera className="w-4 h-4" />
                    <span>Retake Clearer Photo</span>
                  </button>
                </div>
              )}

              {/* PIPELINE COMPLETE: SUCCESSFUL IDENTIFICATION */}
              {step === 'complete' && result && !isLowConfidence && (
                <div className="bg-emerald-50/80 border-2 border-emerald-200 rounded-3xl p-4 sm:p-5 space-y-3">
                  {/* Verified Identification Header */}
                  <div className="flex items-start justify-between gap-2 pb-2 border-b border-emerald-200/60">
                    <div className="flex items-center gap-2">
                      <CharacterAvatar characterId={quest.characterId} size={48} animated={true} />
                      <div>
                        <div className="flex items-center gap-1.5">
                          <span className="text-xs font-black uppercase text-emerald-800">
                            {quest.characterId}
                          </span>
                          <span className="bg-emerald-200 text-emerald-900 text-[10px] font-bold px-1.5 py-0.2 rounded-md">
                            {Math.round((result.confidence || 0.94) * 100)}% Confidence
                          </span>
                        </div>
                        <h4 className="text-base font-black text-slate-900">
                          {result.name}
                        </h4>
                        {result.scientificName && (
                          <span className="text-xs italic text-emerald-800 font-serif">
                            {result.scientificName}
                          </span>
                        )}
                      </div>
                    </div>

                    <div className="text-right">
                      <span className="text-xs font-extrabold text-emerald-700 bg-emerald-100 px-2 py-1 rounded-lg">
                        +{quest.xpReward} XP
                      </span>
                    </div>
                  </div>

                  {/* Character Explanation */}
                  <div className="text-xs sm:text-sm text-slate-700 leading-relaxed font-medium">
                    "{result.explanation}"
                  </div>

                  {/* Companion Voice TTS Button */}
                  <div className="pt-0.5 pb-1">
                    <button
                      type="button"
                      onClick={handleListenCommentary}
                      disabled={voiceState === 'generating'}
                      className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-bold transition-all shadow-sm ${
                        voiceState === 'playing'
                          ? 'bg-emerald-600 text-white animate-pulse'
                          : voiceState === 'generating'
                          ? 'bg-slate-100 text-slate-500 cursor-wait'
                          : 'bg-emerald-100/90 hover:bg-emerald-200/90 text-emerald-900 border border-emerald-300/70'
                      }`}
                    >
                      {voiceState === 'generating' ? (
                        <>
                          <Loader2 className="w-3.5 h-3.5 animate-spin text-emerald-600" />
                          <span>Generating voice...</span>
                        </>
                      ) : voiceState === 'playing' ? (
                        <>
                          <Volume2 className="w-3.5 h-3.5 text-white" />
                          <span>🔊 Playing...</span>
                        </>
                      ) : (
                        <>
                          <Volume2 className="w-3.5 h-3.5 text-emerald-700" />
                          <span>🔊 Listen</span>
                        </>
                      )}
                    </button>

                    {voiceState === 'error' && voiceErrorMessage && (
                      <p className="text-[11px] text-slate-600 font-medium mt-1.5 flex items-center gap-1">
                        <AlertCircle className="w-3.5 h-3.5 text-amber-600 flex-shrink-0" />
                        <span>{voiceErrorMessage}</span>
                      </p>
                    )}
                  </div>

                  {/* Cool Fact */}
                  {result.coolFact && (
                    <div className="bg-amber-100/60 border border-amber-200 rounded-2xl p-2.5 text-xs text-amber-900 font-medium">
                      <strong className="text-amber-800 uppercase text-[10px] tracking-wider block mb-0.5">
                        💡 Outdoor Field Fact:
                      </strong>
                      {result.coolFact}
                    </div>
                  )}

                  {/* Safety Disclaimer (for foraging / food / unknown items) */}
                  {result.safetyDisclaimer && (
                    <div className="bg-rose-50 border border-rose-200 rounded-2xl p-2.5 text-[11px] text-rose-800 flex items-start gap-1.5 font-medium">
                      <AlertTriangle className="w-3.5 h-3.5 text-rose-600 flex-shrink-0 mt-0.5" />
                      <span>{result.safetyDisclaimer}</span>
                    </div>
                  )}

                  {/* Sources Attribution */}
                  {result.knowledgeSources && result.knowledgeSources.length > 0 && (
                    <div className="pt-2 flex flex-col gap-1 text-[11px] text-slate-500">
                      <span className="font-bold flex items-center gap-1 text-slate-600">
                        <Globe className="w-3 h-3 text-emerald-600" /> Verified Knowledge Sources:
                      </span>
                      {result.knowledgeSources.map((src, i) => (
                        <div key={i} className="flex items-center justify-between text-[10px] bg-white/70 px-2.5 py-1.5 rounded-lg border border-emerald-100">
                          <span className="font-medium truncate max-w-[220px]">{src.title}</span>
                          {src.url ? (
                            <a
                              href={src.url}
                              target="_blank"
                              rel="noreferrer"
                              className="text-emerald-700 font-bold hover:underline flex items-center gap-0.5 ml-1"
                            >
                              <span>{src.source}</span>
                              <ExternalLink className="w-2.5 h-2.5" />
                            </a>
                          ) : (
                            <span className="text-emerald-700 font-bold ml-1">{src.source}</span>
                          )}
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}

              {/* ACTION BUTTONS */}
              <div className="pt-2">
                {step === 'idle' && (
                  <button
                    onClick={runAiPipeline}
                    className="btn-duo-primary w-full text-sm py-3"
                  >
                    <Bot className="w-4 h-4" />
                    <span>Run AI Visual Identification</span>
                    <ArrowRight className="w-4 h-4" />
                  </button>
                )}

                {step === 'error' && (
                  <button
                    onClick={runAiPipeline}
                    className="btn-duo-primary w-full text-sm py-3"
                  >
                    <RefreshCw className="w-4 h-4" />
                    <span>Retry AI Identification</span>
                  </button>
                )}

                {step === 'complete' && result && !isLowConfidence && (
                  <button
                    onClick={handleClaimReward}
                    className="btn-duo-accent w-full text-base py-3.5"
                  >
                    <CheckCircle2 className="w-5 h-5" />
                    <span>Complete Quest &amp; Claim +{quest.xpReward} XP</span>
                  </button>
                )}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
