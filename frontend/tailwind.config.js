/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        nature: {
          50: '#f0fdf4',
          100: '#dcfce7',
          200: '#bbf7d0',
          300: '#86efac',
          400: '#4ade80',
          500: '#22c55e',
          600: '#16a34a',
          700: '#15803d',
          800: '#166534',
          900: '#14532d',
          950: '#052e16',
        },
        earth: {
          50: '#fcfbf9',
          100: '#f8f5f0',
          200: '#f1ebd8',
          300: '#e5dcbe',
          400: '#d1c398',
          500: '#b8a674',
          600: '#9e8958',
          700: '#7d6a42',
          800: '#635336',
          900: '#4e412b',
        },
        partner: {
          primary: '#10B981', // Vibrant emerald
          primaryDark: '#059669',
          primaryDeep: '#047857',
          accent: '#F59E0B', // Bright amber gold
          accentDark: '#D97706',
          surface: '#FAF8F5', // Warm clean background
          card: '#FFFFFF',
          dark: '#1E293B',
          muted: '#64748B',
        },
        char: {
          birdo: {
            light: '#ECFDF5',
            main: '#10B981',
            accent: '#FBBF24',
            dark: '#047857',
          },
          flora: {
            light: '#F0FDF4',
            main: '#22C55E',
            accent: '#F472B6',
            dark: '#15803D',
          },
          atlas: {
            light: '#FFFBEB',
            main: '#F59E0B',
            accent: '#EA580C',
            dark: '#B45309',
          },
          munch: {
            light: '#FFF7ED',
            main: '#F97316',
            accent: '#EF4444',
            dark: '#C2410C',
          },
          nova: {
            light: '#F5F3FF',
            main: '#8B5CF6',
            accent: '#06B6D4',
            dark: '#6D28D9',
          },
        }
      },
      fontFamily: {
        sans: ['"Plus Jakarta Sans"', 'Outfit', 'system-ui', '-apple-system', 'sans-serif'],
      },
      boxShadow: {
        'duo': '0 4px 0 0 rgba(0, 0, 0, 0.12)',
        'duo-primary': '0 4px 0 0 #047857',
        'duo-accent': '0 4px 0 0 #B45309',
        'duo-dark': '0 4px 0 0 #0F172A',
        'card-soft': '0 8px 30px rgba(0, 0, 0, 0.05)',
        'glass': '0 8px 32px 0 rgba(16, 185, 129, 0.08)',
      },
      borderRadius: {
        '2xl': '1rem',
        '3xl': '1.5rem',
        '4xl': '2rem',
      },
      animation: {
        'bounce-soft': 'bounceSoft 2s infinite ease-in-out',
        'pulse-glow': 'pulseGlow 2.5s infinite ease-in-out',
        'float': 'float 3s ease-in-out infinite',
      },
      keyframes: {
        bounceSoft: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-6px)' },
        },
        pulseGlow: {
          '0%, 100%': { opacity: '0.6', transform: 'scale(1)' },
          '50%': { opacity: '1', transform: 'scale(1.05)' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-8px)' },
        },
      },
    },
  },
  plugins: [],
}
