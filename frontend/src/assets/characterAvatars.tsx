import React from 'react';
import { CharacterId } from '../types';

interface AvatarProps {
  className?: string;
  size?: number | string;
  animated?: boolean;
}

export const BirdoAvatar: React.FC<AvatarProps> = ({ className = '', size = 64, animated = false }) => (
  <svg
    width={size}
    height={size}
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    className={`${className} ${animated ? 'animate-bounce-soft' : ''}`}
  >
    {/* Background Aura */}
    <circle cx="50" cy="50" r="48" fill="#ECFDF5" />
    <circle cx="50" cy="50" r="44" fill="#10B981" />
    
    {/* Body */}
    <ellipse cx="50" cy="56" rx="28" ry="24" fill="#059669" />
    <ellipse cx="50" cy="58" rx="22" ry="18" fill="#34D399" />
    
    {/* Chest feathers pattern */}
    <path d="M44 60C46 64 54 64 56 60" stroke="#065F46" strokeWidth="2.5" strokeLinecap="round" />
    <path d="M42 66C45 70 55 70 58 66" stroke="#065F46" strokeWidth="2.5" strokeLinecap="round" />

    {/* Wings */}
    <path d="M22 52C20 44 26 38 32 46C28 54 26 62 30 68C24 64 22 58 22 52Z" fill="#047857" />
    <path d="M78 52C80 44 74 38 68 46C72 54 74 62 70 68C76 64 78 58 78 52Z" fill="#047857" />

    {/* Explorer Cap */}
    <ellipse cx="50" cy="30" rx="26" ry="8" fill="#D97706" />
    <path d="M30 30C30 18 70 18 70 30Z" fill="#F59E0B" />
    <circle cx="50" cy="18" r="4" fill="#D97706" />
    <path d="M22 32C32 28 68 28 78 32" stroke="#B45309" strokeWidth="3" strokeLinecap="round" />

    {/* Eyes */}
    <ellipse cx="40" cy="40" rx="7" ry="8" fill="#FFFFFF" />
    <ellipse cx="60" cy="40" rx="7" ry="8" fill="#FFFFFF" />
    <circle cx="42" cy="40" r="4" fill="#1E293B" />
    <circle cx="58" cy="40" r="4" fill="#1E293B" />
    <circle cx="44" cy="38" r="1.5" fill="#FFFFFF" />
    <circle cx="60" cy="38" r="1.5" fill="#FFFFFF" />

    {/* Beak */}
    <path d="M45 44L55 44L50 53Z" fill="#FBBF24" stroke="#D97706" strokeWidth="1.5" />

    {/* Cheeks */}
    <ellipse cx="32" cy="46" rx="4" ry="2.5" fill="#F472B6" opacity="0.6" />
    <ellipse cx="68" cy="46" rx="4" ry="2.5" fill="#F472B6" opacity="0.6" />
  </svg>
);

export const FloraAvatar: React.FC<AvatarProps> = ({ className = '', size = 64, animated = false }) => (
  <svg
    width={size}
    height={size}
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    className={`${className} ${animated ? 'animate-bounce-soft' : ''}`}
  >
    {/* Background Aura */}
    <circle cx="50" cy="50" r="48" fill="#F0FDF4" />
    <circle cx="50" cy="50" r="44" fill="#22C55E" />

    {/* Leaf Hair / Crown */}
    <path d="M30 36C20 20 40 12 50 26C60 12 80 20 70 36Z" fill="#15803D" />
    <circle cx="36" cy="24" r="7" fill="#F472B6" />
    <circle cx="36" cy="24" r="3" fill="#FDE047" />
    <circle cx="64" cy="24" r="7" fill="#F472B6" />
    <circle cx="64" cy="24" r="3" fill="#FDE047" />

    {/* Head */}
    <circle cx="50" cy="52" r="26" fill="#86EFAC" />
    <path d="M50 78C35 78 30 68 30 58C40 64 60 64 70 58C70 68 65 78 50 78Z" fill="#4ADE80" />

    {/* Eyes */}
    <ellipse cx="40" cy="48" rx="6" ry="7" fill="#14532D" />
    <ellipse cx="60" cy="48" rx="6" ry="7" fill="#14532D" />
    <circle cx="42" cy="46" r="2.5" fill="#FFFFFF" />
    <circle cx="62" cy="46" r="2.5" fill="#FFFFFF" />

    {/* Cute smile */}
    <path d="M44 58C47 62 53 62 56 58" stroke="#14532D" strokeWidth="2.5" strokeLinecap="round" />

    {/* Sprout atop head */}
    <path d="M50 24C48 14 38 16 38 16C46 18 48 22 50 24Z" fill="#A3E635" />
    <path d="M50 24C52 14 62 16 62 16C54 18 52 22 50 24Z" fill="#84CC16" />

    {/* Rosy floral cheeks */}
    <circle cx="33" cy="54" r="4.5" fill="#F472B6" opacity="0.6" />
    <circle cx="67" cy="54" r="4.5" fill="#F472B6" opacity="0.6" />
  </svg>
);

export const AtlasAvatar: React.FC<AvatarProps> = ({ className = '', size = 64, animated = false }) => (
  <svg
    width={size}
    height={size}
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    className={`${className} ${animated ? 'animate-bounce-soft' : ''}`}
  >
    {/* Background */}
    <circle cx="50" cy="50" r="48" fill="#FFFBEB" />
    <circle cx="50" cy="50" r="44" fill="#F59E0B" />

    {/* Explorer Hat */}
    <path d="M18 36C28 32 72 32 82 36C86 37 84 41 80 41C64 41 36 41 20 41C16 41 14 37 18 36Z" fill="#78350F" />
    <path d="M30 36C30 18 70 18 70 36Z" fill="#92400E" />
    <rect x="30" y="32" width="40" height="4" fill="#D97706" />

    {/* Head */}
    <circle cx="50" cy="55" r="25" fill="#FDE68A" />

    {/* Explorer Goggles on Hat */}
    <circle cx="40" cy="28" r="8" fill="#78350F" stroke="#D97706" strokeWidth="2" />
    <circle cx="60" cy="28" r="8" fill="#78350F" stroke="#D97706" strokeWidth="2" />
    <circle cx="40" cy="28" r="5" fill="#38BDF8" opacity="0.8" />
    <circle cx="60" cy="28" r="5" fill="#38BDF8" opacity="0.8" />
    <path d="M48 28H52" stroke="#78350F" strokeWidth="3" />

    {/* Eyes */}
    <circle cx="40" cy="52" r="5" fill="#451A03" />
    <circle cx="60" cy="52" r="5" fill="#451A03" />
    <circle cx="42" cy="50" r="1.8" fill="#FFFFFF" />
    <circle cx="62" cy="50" r="1.8" fill="#FFFFFF" />

    {/* Confident smile */}
    <path d="M43 63C47 67 53 67 57 63" stroke="#78350F" strokeWidth="3" strokeLinecap="round" />

    {/* Compass pendant */}
    <circle cx="50" cy="78" r="7" fill="#F59E0B" stroke="#78350F" strokeWidth="2" />
    <path d="M50 73L52 78L50 83L48 78Z" fill="#EF4444" />
  </svg>
);

export const MunchAvatar: React.FC<AvatarProps> = ({ className = '', size = 64, animated = false }) => (
  <svg
    width={size}
    height={size}
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    className={`${className} ${animated ? 'animate-bounce-soft' : ''}`}
  >
    {/* Background */}
    <circle cx="50" cy="50" r="48" fill="#FFF7ED" />
    <circle cx="50" cy="50" r="44" fill="#F97316" />

    {/* Chef Bandana / Ears */}
    <path d="M26 24C20 34 32 42 38 34Z" fill="#EA580C" />
    <path d="M74 24C80 34 68 42 62 34Z" fill="#EA580C" />
    <path d="M28 32C42 22 58 22 72 32C72 38 28 38 28 32Z" fill="#EF4444" />
    <circle cx="50" cy="30" r="3" fill="#FFFFFF" />

    {/* Round chubby head */}
    <ellipse cx="50" cy="56" rx="27" ry="24" fill="#FDBA74" />

    {/* Chubby cheeks */}
    <circle cx="30" cy="58" r="6.5" fill="#EA580C" opacity="0.3" />
    <circle cx="70" cy="58" r="6.5" fill="#EA580C" opacity="0.3" />

    {/* Big hungry happy eyes */}
    <circle cx="39" cy="48" r="6" fill="#431407" />
    <circle cx="61" cy="48" r="6" fill="#431407" />
    <circle cx="41" cy="46" r="2.2" fill="#FFFFFF" />
    <circle cx="63" cy="46" r="2.2" fill="#FFFFFF" />

    {/* Open mouth ready to taste */}
    <path d="M42 58C42 66 58 66 58 58Z" fill="#991B1B" />
    <path d="M46 63C48 65 52 65 54 63" fill="#F87171" />

    {/* Little berry badge */}
    <circle cx="50" cy="78" r="5" fill="#DC2626" />
    <path d="M50 73C52 70 55 70 54 73Z" fill="#16A34A" />
  </svg>
);

export const NovaAvatar: React.FC<AvatarProps> = ({ className = '', size = 64, animated = false }) => (
  <svg
    width={size}
    height={size}
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    className={`${className} ${animated ? 'animate-bounce-soft' : ''}`}
  >
    {/* Background */}
    <circle cx="50" cy="50" r="48" fill="#F5F3FF" />
    <circle cx="50" cy="50" r="44" fill="#8B5CF6" />

    {/* Glowing antenna & starlight halo */}
    <path d="M50 28V14" stroke="#06B6D4" strokeWidth="3" strokeLinecap="round" />
    <circle cx="50" cy="12" r="5" fill="#67E8F9" />
    <circle cx="50" cy="12" r="8" fill="#67E8F9" opacity="0.3" />

    {/* Head / Visor Helmet */}
    <circle cx="50" cy="54" r="26" fill="#C4B5FD" />
    <path d="M28 50C28 40 72 40 72 50C72 64 28 64 28 50Z" fill="#1E1B4B" />

    {/* Visor Screen with Stars */}
    <ellipse cx="50" cy="50" rx="19" ry="10" fill="#0F172A" stroke="#06B6D4" strokeWidth="1.5" />
    <circle cx="42" cy="48" r="3" fill="#38BDF8" />
    <circle cx="58" cy="48" r="3" fill="#38BDF8" />
    <circle cx="43" cy="47" r="1" fill="#FFFFFF" />
    <circle cx="59" cy="47" r="1" fill="#FFFFFF" />
    <path d="M47 53C49 55 51 55 53 53" stroke="#38BDF8" strokeWidth="1.5" strokeLinecap="round" />

    {/* Headphone Ears */}
    <rect x="20" y="44" width="6" height="14" rx="3" fill="#7C3AED" />
    <rect x="74" y="44" width="6" height="14" rx="3" fill="#7C3AED" />

    {/* Orbiting Sparkles */}
    <path d="M30 76L32 78L30 80L28 78Z" fill="#FDE047" />
    <path d="M70 28L72 30L70 32L68 30Z" fill="#FDE047" />
  </svg>
);

export const CharacterAvatar: React.FC<{
  characterId: CharacterId;
  size?: number | string;
  className?: string;
  animated?: boolean;
}> = ({ characterId, size = 64, className = '', animated = false }) => {
  switch (characterId) {
    case 'birdo':
      return <BirdoAvatar size={size} className={className} animated={animated} />;
    case 'flora':
      return <FloraAvatar size={size} className={className} animated={animated} />;
    case 'atlas':
      return <AtlasAvatar size={size} className={className} animated={animated} />;
    case 'munch':
      return <MunchAvatar size={size} className={className} animated={animated} />;
    case 'nova':
      return <NovaAvatar size={size} className={className} animated={animated} />;
    default:
      return <BirdoAvatar size={size} className={className} animated={animated} />;
  }
};
