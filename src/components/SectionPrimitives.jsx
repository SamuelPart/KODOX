import { motion, useReducedMotion } from 'framer-motion';

/* ============================================================================
   Piezas compartidas de UI: revelado al scroll, encabezados de sección y
   utilidades de animación reutilizables.
   ========================================================================== */

export const fadeUp = {
  hidden: { opacity: 0, y: 28, filter: 'blur(6px)' },
  show: {
    opacity: 1,
    y: 0,
    filter: 'blur(0px)',
    transition: { duration: 0.7, ease: [0.16, 1, 0.3, 1] },
  },
};

export const staggerParent = {
  hidden: {},
  show: { transition: { staggerChildren: 0.09, delayChildren: 0.05 } },
};

export const viewportOnce = { once: true, amount: 0.25 };

export function Reveal({ children, delay = 0, y = 30, className = '', as = 'div' }) {
  const reduce = useReducedMotion();
  const MotionTag = motion[as] || motion.div;

  return (
    <MotionTag
      className={className}
      initial={reduce ? false : { opacity: 0, y, filter: 'blur(8px)' }}
      whileInView={{ opacity: 1, y: 0, filter: 'blur(0px)' }}
      viewport={viewportOnce}
      transition={{ duration: 0.75, delay, ease: [0.16, 1, 0.3, 1] }}
    >
      {children}
    </MotionTag>
  );
}

export function SectionHeading({
  eyebrow,
  title,
  subtitle,
  align = 'center',
  className = '',
  accentWord,
  children,
}) {
  const isCenter = align === 'center';
  const words = typeof title === 'string' ? title.split(' ') : [];

  return (
    <div
      className={`relative flex flex-col gap-5 ${
        isCenter ? 'items-center text-center' : 'items-start text-left'
      } ${className}`}
    >
      {eyebrow && (
        <Reveal>
          <span className="section-label">
            <span className="h-1.5 w-1.5 rounded-full bg-indigo-400 shadow-[0_0_10px_2px_rgba(99,102,241,0.9)]" />
            {eyebrow}
          </span>
        </Reveal>
      )}

      <h2
        className={`max-w-3xl font-display text-3xl font-semibold leading-[1.08] tracking-tight sm:text-4xl lg:text-[2.9rem] ${
          isCenter ? 'mx-auto' : ''
        }`}
      >
        {words.map((word, index) => (
          <span key={`${word}-${index}`} className="inline-block overflow-hidden pb-1 align-bottom">
            <motion.span
              className={`inline-block ${
                accentWord && word.toLowerCase().includes(accentWord.toLowerCase())
                  ? 'text-gradient-strong'
                  : ''
              }`}
              initial={{ y: '110%' }}
              whileInView={{ y: '0%' }}
              viewport={viewportOnce}
              transition={{
                duration: 0.85,
                delay: 0.035 * index,
                ease: [0.16, 1, 0.3, 1],
              }}
            >
              {word}
              {index < words.length - 1 ? '\u00A0' : ''}
            </motion.span>
          </span>
        ))}
      </h2>

      {subtitle && (
        <Reveal delay={0.12}>
          <p
            className={`max-w-2xl text-[15px] leading-relaxed text-zinc-400 sm:text-base ${
              isCenter ? 'mx-auto' : ''
            }`}
          >
            {subtitle}
          </p>
        </Reveal>
      )}

      {children}
    </div>
  );
}

export function GlowOrb({ className = '', color = 'indigo' }) {
  const palette = {
    indigo: 'bg-indigo-600/25',
    cyan: 'bg-cyan-500/20',
    violet: 'bg-violet-600/20',
  };
  return (
    <div
      aria-hidden="true"
      className={`pointer-events-none absolute rounded-full blur-[110px] ${palette[color] || palette.indigo} ${className}`}
    />
  );
}
