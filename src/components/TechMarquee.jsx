import { motion, useReducedMotion } from 'framer-motion';
import {
  Boxes,
  BrainCircuit,
  Code2,
  CreditCard,
  Gauge,
  Globe2,
  Layers,
  Lock,
  MessageCircle,
  QrCode,
  Receipt,
  Rocket,
  ShieldCheck,
  Smartphone,
  Sparkles,
  Store,
  Table2,
  Wand2,
  Zap,
} from 'lucide-react';
import { siteConfig } from '../data/portfolioData';

/* Icono por cada capacidad del marquee superior */
const ICON_MAP = {
  'React + Vite': Code2,
  'Apps Android': Smartphone,
  'Cartas Digitales QR': QrCode,
  'IA Integrada': BrainCircuit,
  'Tiendas Online': Store,
  'Pedidos WhatsApp': MessageCircle,
  'Diseño a Medida': Wand2,
  'Sin Mensualidades': Receipt,
};

/* Icono por cada tecnología del marquee inferior */
const TECH_ICON_MAP = {
  'Tailwind CSS': Layers,
  'Framer Motion': Sparkles,
  'Node & APIs': Globe2,
  Supabase: Lock,
  'Vercel Edge': Rocket,
  'Google Sheets API': Table2,
  'MediaPipe IA': Boxes,
  'Pagos Yape / Plin': CreditCard,
};

function MarqueeRow({ items, icons, reverse = false, speed = 42, variant = 'primary' }) {
  const reduce = useReducedMotion();
  // Duplicamos la lista para lograr un bucle perfecto sin cortes.
  const loop = [...items, ...items];

  return (
    <div className="marquee-group relative flex w-full overflow-hidden">
      <motion.div
        className="flex w-max items-center gap-3 sm:gap-4"
        animate={reduce ? undefined : { x: reverse ? ['-50%', '0%'] : ['0%', '-50%'] }}
        transition={{ duration: speed, ease: 'linear', repeat: Infinity }}
        style={{ willChange: 'transform' }}
      >
        {loop.map((item, index) => {
          const Icon = icons[item] || Zap;
          const isPrimary = variant === 'primary';

          return (
            <div
              key={`${item}-${index}`}
              className={`group/item flex shrink-0 items-center gap-2.5 rounded-full border px-4 py-2.5 transition-colors duration-300 ${
                isPrimary
                  ? 'border-white/10 bg-white/[0.03] hover:border-indigo-400/40 hover:bg-indigo-500/[0.07]'
                  : 'border-white/[0.07] bg-transparent hover:border-cyan-400/30 hover:bg-cyan-500/[0.05]'
              }`}
              aria-hidden={index >= items.length}
            >
              <span
                className={`grid h-6 w-6 place-items-center rounded-lg ${
                  isPrimary
                    ? 'bg-gradient-to-br from-indigo-500/25 to-cyan-500/20 text-indigo-300'
                    : 'bg-white/[0.04] text-zinc-400'
                }`}
              >
                <Icon className="h-3.5 w-3.5" strokeWidth={2} />
              </span>
              <span
                className={`whitespace-nowrap ${
                  isPrimary
                    ? 'text-[13px] font-medium text-zinc-200'
                    : 'font-mono text-[11px] uppercase tracking-[0.14em] text-zinc-500'
                }`}
              >
                {item}
              </span>
              <span
                className={`h-1 w-1 rounded-full ${
                  isPrimary ? 'bg-indigo-400/50' : 'bg-cyan-400/40'
                }`}
              />
            </div>
          );
        })}
      </motion.div>
    </div>
  );
}

export default function TechMarquee() {
  const { top, bottom } = siteConfig.marquee;

  return (
    <section aria-label="Tecnologías y capacidades" className="relative overflow-hidden py-6">
      {/* Separadores luminosos */}
      <div className="divider-glow" />

      <div className="relative py-7">
        {/* Máscaras laterales */}
        <div className="pointer-events-none absolute inset-y-0 left-0 z-10 w-16 bg-gradient-to-r from-obsidian to-transparent sm:w-32" />
        <div className="pointer-events-none absolute inset-y-0 right-0 z-10 w-16 bg-gradient-to-l from-obsidian to-transparent sm:w-32" />

        <div className="space-y-3">
          <MarqueeRow items={top} icons={ICON_MAP} speed={46} variant="primary" />
          <MarqueeRow items={bottom} icons={TECH_ICON_MAP} reverse speed={58} variant="secondary" />
        </div>
      </div>

      {/* Fila de badges de confianza */}
      <div className="relative mx-auto flex max-w-6xl flex-wrap items-center justify-center gap-x-6 gap-y-3 px-4 pb-4 pt-2 sm:px-6">
        {[
          { icon: Gauge, label: 'Carga < 1 s' },
          { icon: ShieldCheck, label: 'Código tuyo, para siempre' },
          { icon: Zap, label: 'Deploy en Vercel' },
          { icon: Receipt, label: 'Sin mensualidades' },
        ].map(({ icon: Icon, label }) => (
          <span
            key={label}
            className="inline-flex items-center gap-2 font-mono text-[10px] uppercase tracking-[0.16em] text-zinc-500"
          >
            <Icon className="h-3.5 w-3.5 text-indigo-400/80" />
            {label}
          </span>
        ))}
      </div>

      <div className="divider-glow" />
    </section>
  );
}
