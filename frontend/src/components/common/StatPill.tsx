import React from 'react';

interface StatPillProps {
  icon: React.ReactNode;
  label: string;
  value: string | number;
  highlightColor?: string;
  onClick?: () => void;
  className?: string;
}

export const StatPill: React.FC<StatPillProps> = ({
  icon,
  label,
  value,
  highlightColor = 'bg-white',
  onClick,
  className = '',
}) => {
  return (
    <div
      onClick={onClick}
      className={`flex items-center gap-2 px-3 py-1.5 rounded-full border border-slate-200/80 shadow-sm text-sm font-semibold transition-all select-none ${highlightColor} ${
        onClick ? 'cursor-pointer hover:scale-105 active:scale-95' : ''
      } ${className}`}
    >
      <span className="flex-shrink-0 text-base">{icon}</span>
      <div className="flex flex-col sm:flex-row sm:items-center sm:gap-1.5 leading-tight">
        <span className="text-xs text-slate-500 font-medium">{label}</span>
        <span className="text-slate-800 font-bold">{value}</span>
      </div>
    </div>
  );
};
