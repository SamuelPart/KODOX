import { useRef } from 'react';
import { motion, useReducedMotion, useScroll, useSpring, useTransform } from 'framer-motion';
import {
  ArrowRight,
  CheckCircle2,
  Clock3,
  Code2,
  MessagesSquare,
  Rocket,
  Timer,
  Wand2,
} from 'lucide-react';
import { siteConfig, buildWhatsAppLink } from '../data/portfolioData';
import { SectionHeading } from './SectionPrimitives';

const ICONS = { MessagesSquare, Code2, Wand2, Rocket };

function Step({ step, index, total }) {
  const reduce = useReducedMotion();
  const Icon = ICONS[step.icon] || Code2;
  const isLast = index === total - 1;

  return (
    <motion.li
      initial={reduce ? false : { opacity: 0, y: 40 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, amount: 0.4 }}
      transition={{ duration: 0.7, ease: [0.16, 1, 0.3, 1] }}
      className="relative pl-16 sm:pl-24"
    >
      {/* Nodo con número */}
      <motion.div
        whileHover={reduce ? {} : { scale: 1.08, rotate: -4 }}
        transition={{ type: 'spring', stiffness: 320, damping: 18 }}
        className="absolute left-0 top-0 grid h-12 w-12 place-items-center rounded-2xl border border-indigo-400/30 bg-gradient-to-br from-indigo-600/90 via-purple-600/80 to-cyan-500/70 shadow-[0_0_35px_-8px_rgba(99,102,241,0.9)] sm:h-14 sm:w-14"
      >
        <span className="font-display text-base font-bold text-white sm:text-lg">
          {step.number}
        </span>
        <span className="absolute inset-0 rounded-2xl ring-1 ring-inset ring-white/20" />
      </motion.div>

      <div className="group relative overflow-hidden rounded-3xl border border-white/10 bg-zinc-900/60 p-5 shadow-card backdrop-blur-xl transition-all duration-300 hover:border-indigo-500/50 hover:bg-zinc-900/80 sm:p-6">
        <div
          aria-hidden="true"
          className="pointer-events-none absolute -right-10 -top-10 h-32 w-32 rounded-full bg-indigo-600/0 blur-[50px] transition-all duration-500 group-hover:bg-indigo-600/25"
        />

        <div className="relative flex flex-wrap items-center justify-between gap-2">
          <div className="flex items-center gap-2.5">
            <span className="grid h-8 w-8 place-items-center rounded-xl border border-white/10 bg-white/[0.04] text-indigo-300">
              <Icon className="h-4 w-4" />
            </span>
            <h3 className="font-display text-base font-semibold text-white sm:text-lg">
              {step.title}
            </h3>
          </div>

          <span className="inline-flex items-center gap-1.5 rounded-full border border-cyan-400/20 bg-cyan-400/[0.07] px-2.5 py-1 font-mono text-[9px] uppercase tracking-[0.14em] text-cyan-200">
            <Clock3 className="h-2.5 w-2.5" />
            {step.duration}
          </span>
        </div>

        <p className="relative mt-3 max-w-xl text-[13px] leading-relaxed text-zinc-400">
          {step.description}
        </p>

        <ul className="relative mt-4 flex flex-wrap gap-1.5">
          {step.deliverables.map((item, itemIndex) => (
            <motion.li
              key={item}
              initial={reduce ? false : { opacity: 0, scale: 0.9 }}
              whileInView={{ opacity: 1, scale: 1 }}
              viewport={{ once: true }}
              transition={{ duration: 0.4, delay: 0.15 + itemIndex * 0.07 }}
              className="inline-flex items-center gap-1.5 rounded-lg border border-white/[0.08] bg-white/[0.03] px-2.5 py-1.5 text-[10.5px] text-zinc-300"
            >
              <CheckCircle2 className="h-3 w-3 text-emerald-400" />
              {item}
            </motion.li>
          ))}
        </ul>
      </div>

      {!isLast && <div className="h-6 sm:h-8" aria-hidden="true" />}
    </motion.li>
  );
}

export default function ProcessTimeline() {
  const { eyebrow, title, subtitle, steps, deliveryBadge } = siteConfig.process;
  const containerRef = useRef(null);
  const reduce = useReducedMotion();

  // Progreso vinculado al contenedor de la línea de tiempo
  const { scrollYProgress } = useScroll({
    target: containerRef,
    offset: ['start 75%', 'end 60%'],
  });

  const smoothProgress = useSpring(scrollYProgress, {
    stiffness: 90,
    damping: 24,
    restDelta: 0.001,
  });

  const glowY = useTransform(smoothProgress, [0, 1], ['0%', '100%']);
  const glowOpacity = useTransform(smoothProgress, [0, 0.06, 0.94, 1], [0, 1, 1, 0.3]);

  return (
    <section id="proceso" className="relative scroll-mt-24 overflow-hidden py-20 sm:py-28">
      <div aria-hidden="true" className="pointer-events-none absolute inset-0 -z-10">
        <div className="absolute left-1/2 top-10 h-72 w-[600px] -translate-x-1/2 rounded-full bg-purple-600/10 blur-[120px]" />
        <div className="bg-dots grid-fade absolute inset-0 opacity-25" />
      </div>

      <div className="mx-auto max-w-6xl px-4 sm:px-6">
        <div className="flex flex-col items-start justify-between gap-6 lg:flex-row lg:items-end">
          <SectionHeading
            eyebrow={eyebrow}
            title={title}
            subtitle={subtitle}
            align="left"
            accentWord="trabajamos"
            className="lg:max-w-2xl"
          />

          <motion.div
            initial={{ opacity: 0, y: 16 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6, delay: 0.2 }}
            className="flex items-center gap-3 rounded-2xl border border-emerald-500/25 bg-emerald-500/[0.07] px-4 py-3 backdrop-blur-xl"
          >
            <span className="grid h-9 w-9 place-items-center rounded-xl bg-emerald-500/15 text-emerald-300">
              <Timer className="h-4 w-4" />
            </span>
            <div>
              <p className="font-mono text-[9px] uppercase tracking-[0.18em] text-emerald-300/80">
                Tiempo promedio
              </p>
              <p className="font-display text-sm font-semibold text-white">{deliveryBadge}</p>
            </div>
          </motion.div>
        </div>

        {/* ---------- Línea de tiempo ---------- */}
        <div ref={containerRef} className="relative mt-14">
          {/* Riel base */}
          <div
            aria-hidden="true"
            className="absolute bottom-6 left-6 top-2 w-px bg-white/10 sm:left-7 sm:w-[2px]"
          >
            {/* Relleno de progreso */}
            <motion.div
              style={{ scaleY: reduce ? 1 : smoothProgress }}
              className="h-full w-full origin-top bg-gradient-to-b from-indigo-400 via-purple-400 to-cyan-400 shadow-[0_0_14px_2px_rgba(99,102,241,0.7)]"
            />

            {/* Punto luminoso que recorre la línea */}
            {!reduce && (
              <motion.div
                style={{ top: glowY, opacity: glowOpacity }}
                className="absolute -left-[7px] h-3.5 w-3.5 -translate-y-1/2 rounded-full bg-cyan-300 shadow-[0_0_20px_6px_rgba(34,211,238,0.85)] sm:-left-[6px]"
              />
            )}
          </div>

          <ol className="relative space-y-0">
            {steps.map((step, index) => (
              <Step key={step.number} step={step} index={index} total={steps.length} />
            ))}
          </ol>
        </div>

        {/* ---------- CTA ---------- */}
        <motion.div
          initial={{ opacity: 0, y: 28 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, amount: 0.4 }}
          transition={{ duration: 0.7, ease: [0.16, 1, 0.3, 1] }}
          className="relative mt-14 overflow-hidden rounded-3xl border border-white/10 bg-gradient-to-br from-indigo-600/15 via-zinc-900/60 to-cyan-500/10 p-6 backdrop-blur-xl sm:p-8"
        >
          <div className="pointer-events-none absolute inset-0 bg-grid-sm opacity-30" />

          <div className="relative flex flex-col items-start justify-between gap-5 sm:flex-row sm:items-center">
            <div>
              <p className="font-mono text-[10px] uppercase tracking-[0.2em] text-cyan-300">
                Paso 0 — el más importante
              </p>
              <h3 className="mt-2 max-w-lg font-display text-xl font-semibold leading-snug text-white sm:text-2xl">
                Escríbeme hoy y mañana ya podrías estar vendiendo.
              </h3>
              <p className="mt-1.5 max-w-md text-[13px] text-zinc-400">
                Sin compromiso. Te digo qué necesita tu negocio y cuánto costaría, en menos de una hora.
              </p>
            </div>

            <motion.a
              href={buildWhatsAppLink('Hola KODOX, quiero empezar mi proyecto. ¿Cuál es el primer paso?')}
              target="_blank"
              rel="noopener noreferrer"
              whileHover={{ y: -3 }}
              whileTap={{ scale: 0.96 }}
              className="btn-whatsapp shrink-0 px-6 py-3.5"
            >
              Iniciar mi proyecto
              <ArrowRight className="h-4 w-4" />
            </motion.a>
          </div>
        </motion.div>
      </div>
    </section>
  );
}
