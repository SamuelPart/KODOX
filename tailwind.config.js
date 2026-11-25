/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        obsidian: {
          DEFAULT: '#050507',
          50: '#0B0B11',
          100: '#101018',
          200: '#16161F',
        },
        whatsapp: {
          DEFAULT: '#16A34A',
          dark: '#0F7A36',
          light: '#22C55E',
        },
        electric: '#4F46E5',
        neon: '#06B6D4',
      },
      fontFamily: {
        display: ['"Space Grotesk"', 'ui-sans-serif', 'system-ui', 'sans-serif'],
        sans: ['Inter', 'ui-sans-serif', 'system-ui', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'ui-monospace', 'SFMono-Regular', 'monospace'],
      },
      boxShadow: {
        'glow-indigo': '0 0 80px -20px rgba(79, 70, 229, 0.75)',
        'glow-cyan': '0 0 80px -20px rgba(6, 182, 212, 0.55)',
        'glow-wa': '0 18px 45px -18px rgba(22, 163, 74, 0.85)',
        card: '0 30px 80px -40px rgba(0, 0, 0, 0.95)',
        'inner-line': 'inset 0 1px 0 0 rgba(255, 255, 255, 0.07)',
        'focus-ring': '0 0 0 1px rgba(129, 140, 248, 0.55), 0 0 0 6px rgba(79, 70, 229, 0.12)',
      },
      backgroundImage: {
        'gradient-brand': 'linear-gradient(100deg, #4F46E5 0%, #8B5CF6 45%, #06B6D4 100%)',
        'gradient-brand-soft':
          'linear-gradient(120deg, rgba(79,70,229,0.22) 0%, rgba(139,92,246,0.16) 45%, rgba(6,182,212,0.18) 100%)',
        'gradient-radial':
          'radial-gradient(circle at 50% 50%, rgba(79,70,229,0.28), transparent 65%)',
      },
      transitionTimingFunction: {
        'out-expo': 'cubic-bezier(0.16, 1, 0.3, 1)',
        'in-out-soft': 'cubic-bezier(0.65, 0.05, 0.36, 1)',
      },
      keyframes: {
        marquee: {
          from: { transform: 'translateX(0)' },
          to: { transform: 'translateX(-50%)' },
        },
        marqueeReverse: {
          from: { transform: 'translateX(-50%)' },
          to: { transform: 'translateX(0)' },
        },
        shimmer: {
          '0%': { transform: 'translateX(-120%)' },
          '100%': { transform: 'translateX(220%)' },
        },
        floaty: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-14px)' },
        },
        'pulse-ring': {
          '0%': { transform: 'scale(0.85)', opacity: '0.9' },
          '70%': { transform: 'scale(2.4)', opacity: '0' },
          '100%': { transform: 'scale(2.4)', opacity: '0' },
        },
        'spin-slow': {
          from: { transform: 'rotate(0deg)' },
          to: { transform: 'rotate(360deg)' },
        },
      },
      animation: {
        marquee: 'marquee 42s linear infinite',
        'marquee-reverse': 'marqueeReverse 52s linear infinite',
        shimmer: 'shimmer 2.8s ease-in-out infinite',
        floaty: 'floaty 7s ease-in-out infinite',
        'pulse-ring': 'pulse-ring 2.6s cubic-bezier(0.24, 0.6, 0.35, 1) infinite',
        'spin-slow': 'spin-slow 18s linear infinite',
      },
    },
  },
  plugins: [],
};
