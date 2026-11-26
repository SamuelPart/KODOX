import { useMemo, useState } from 'react';
import { AnimatePresence, motion, useReducedMotion } from 'framer-motion';
import {
  ArrowUpRight,
  ExternalLink,
  MessageCircle,
  Monitor,
  Smartphone,
  Sparkles,
} from 'lucide-react';
import { siteConfig, projectInquiryLink } from '../data/portfolioData';
import { SectionHeading } from './SectionPrimitives';

const DEVICE_MODES = [
  { id: 'desktop', label: 'PC', icon: Monitor, aria: 'Vista de escritorio' },
  { id: 'mobile', label: 'Móvil', icon: Smartphone, aria: 'Vista móvil' },
];

function DeviceToggle({ value, onChange, groupId }) {
  return (
    <div
      role="radiogroup"
      aria-label="Cambiar vista previa de dispositivo"
      className="inline-flex items-center gap-1 rounded-xl border border-white/10 bg-black/40 p-1 backdrop-blur-md"
    >
      {DEVICE_MODES.map((mode) => {
        const Icon = mode.icon;
        const active = value === mode.id;
        return (
          <button
            key={mode.id}
            type="button"
            role="radio"
            aria-checked={active}
            aria-label={mode.aria}
            onClick={() => onChange(mode.id)}
            className={`relative inline-flex items-center gap-1.5 rounded-lg px-3 py-1.5 text-[11px] font-medium transition-colors duration-200 ${
              active ? 'text-white' : 'text-zinc-400 hover:text-zinc-200'
            }`}
          >
            {active && (
              <motion.span
                layoutId={`device-pill-${groupId}`}
                className="absolute inset-0 -z-10 rounded-lg bg-gradient-to-r from-indigo-500 to-purple-500 shadow-[0_8px_20px_-10px_rgba(99,102,241,1)]"
                transition={{ type: 'spring', stiffness: 380, damping: 32, mass: 0.7 }}
              />
            )}
            <Icon className="relative h-3.5 w-3.5" />
            <span className="relative">{mode.label}</span>
          </button>
        );
      })}
    </div>
  );
}

function DemoPreview({ project, device, reduce }) {
  const isMobile = device === 'mobile';

  return (
    <div className="relative flex w-full items-center justify-center px-3 pb-3 pt-2 sm:px-5 sm:pb-5">
      {/* Glow behind frame */}
      <div
        aria-hidden="true"
        className={`pointer-events-none absolute inset-x-10 top-6 h-40 rounded-full bg-gradient-to-r ${project.accent} blur-[70px] opacity-70`}
      />

      <motion.div
        layout
        transition={{ type: 'spring', stiffness: 260, damping: 30 }}
        className={`relative w-full ${
          isMobile ? 'max-w-[320px] sm:max-w-[360px]' : 'max-w-full'
        }`}
      >
        <motion.div
          layout
          transition={{ type: 'spring', stiffness: 260, damping: 30 }}
          className={`relative overflow-hidden border border-white/10 bg-zinc-950 shadow-card ${
            isMobile
              ? 'rounded-[2.2rem] ring-1 ring-white/10'
              : 'rounded-2xl ring-1 ring-white/5'
          }`}
        >
          {/* Browser / phone top bar */}
          <div
            className={`relative flex items-center border-b border-white/10 bg-zinc-900/90 ${
              isMobile ? 'justify-center px-4 py-3' : 'gap-3 px-4 py-2.5'
            }`}
          >
            {isMobile ? (
              <div className="flex flex-col items-center gap-1">
                <span className="h-1.5 w-16 rounded-full bg-zinc-700" />
                <span className="h-1 w-1 rounded-full bg-zinc-600" />
              </div>
            ) : (
              <>
                <div className="flex items-center gap-1.5" aria-hidden="true">
                  <span className="h-2.5 w-2.5 rounded-full bg-rose-400/80" />
                  <span className="h-2.5 w-2.5 rounded-full bg-amber-300/80" />
                  <span className="h-2.5 w-2.5 rounded-full bg-emerald-400/80" />
                </div>
                <div className="flex min-w-0 flex-1 items-center justify-center">
                  <div className="flex w-full max-w-sm min-w-0 items-center gap-2 rounded-lg border border-white/10 bg-black/40 px-3 py-1.5">
                    <span className="h-1.5 w-1.5 shrink-0 rounded-full bg-emerald-400 shadow-[0_0_8px_rgba(52,211,153,0.9)]" />
                    <span className="truncate font-mono text-[10px] tracking-tight text-zinc-400">
                      {project.url.replace(/^https?:\/\//, '')}
                    </span>
                  </div>
                </div>
                <div className="hidden w-[52px] sm:block" aria-hidden="true" />
              </>
            )}
          </div>

          {/* Screen */}
          <motion.div
            layout
            transition={{ type: 'spring', stiffness: 260, damping: 30 }}
            className={`relative bg-[#050507] ${
              isMobile ? 'aspect-[9/19]' : 'aspect-[16/10]'
            }`}
          >
            {/* Loading skeleton */}
            <div
              aria-hidden="true"
              className="absolute inset-0 animate-pulse bg-gradient-to-b from-zinc-800/40 via-zinc-900/40 to-zinc-950/60"
            />
            <div
              aria-hidden="true"
              className="absolute inset-0 flex flex-col items-center justify-center gap-3 text-zinc-600"
            >
              <Sparkles className="h-5 w-5 text-indigo-400/60" />
              <span className="font-mono text-[10px] uppercase tracking-[0.2em]">
                Cargando demo
              </span>
            </div>

            <iframe
              src={project.url}
              title={`Vista previa de ${project.title}`}
              loading="lazy"
              className="relative z-10 h-full w-full border-0 bg-white"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
              referrerPolicy="strict-origin-when-cross-origin"
            />
          </motion.div>

          {/* Mobile home indicator */}
          {isMobile && (
            <div className="flex justify-center bg-zinc-900/90 py-2">
              <span className="h-1 w-24 rounded-full bg-zinc-700" />
            </div>
          )}
        </motion.div>

        {/* Floating device label */}
        <div className="pointer-events-none absolute -bottom-2 left-1/2 -translate-x-1/2">
          <span className="inline-flex items-center gap-1.5 rounded-full border border-white/10 bg-zinc-950/90 px-3 py-1 font-mono text-[9px] uppercase tracking-[0.16em] text-zinc-400 shadow-lg backdrop-blur-md">
            <span className="h-1.5 w-1.5 rounded-full bg-cyan-400 shadow-[0_0_8px_rgba(34,211,238,0.9)]" />
            {isMobile ? 'Vista móvil' : 'Vista escritorio'}
          </span>
        </div>
      </motion.div>
    </div>
  );
}

function DemoShowcaseCard({ project, index, onQuote }) {
  const reduce = useReducedMotion();
  const [device, setDevice] = useState('desktop');

  return (
    <motion.article
      initial={reduce ? false : { opacity: 0, y: 36 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, amount: 0.15 }}
      transition={{ duration: 0.7, delay: index * 0.08, ease: [0.16, 1, 0.3, 1] }}
      className="group relative flex flex-col overflow-hidden rounded-3xl border border-white/10 bg-zinc-900/50 shadow-card backdrop-blur-xl"
    >
      <div
        aria-hidden="true"
        className={`pointer-events-none absolute inset-x-0 top-0 h-48 bg-gradient-to-b ${project.accent} opacity-60`}
      />

      {/* Header */}
      <div className="relative flex flex-col gap-4 px-5 pt-5 sm:flex-row sm:items-start sm:justify-between sm:px-6 sm:pt-6">
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-2">
            <span className="inline-flex items-center gap-1.5 rounded-full border border-white/10 bg-white/[0.04] px-2.5 py-1 font-mono text-[9px] uppercase tracking-[0.14em] text-zinc-300">
              <span className="h-1.5 w-1.5 rounded-full bg-emerald-400 shadow-[0_0_8px_rgba(52,211,153,0.9)]" />
              Demo real
            </span>
            <span className="rounded-full border border-white/10 bg-black/30 px-2.5 py-1 font-mono text-[9px] uppercase tracking-[0.14em] text-zinc-500">
              {project.categoryLabel}
            </span>
          </div>
          <h3 className="mt-3 font-display text-xl font-semibold leading-tight text-white sm:text-2xl">
            {project.title}
          </h3>
          <p className="mt-1 text-[13px] font-medium text-indigo-300/90">{project.subtitle}</p>
        </div>

        <div className="shrink-0">
          <DeviceToggle value={device} onChange={setDevice} groupId={project.id} />
        </div>
      </div>

      {/* Preview */}
      <div className="relative mt-2">
        <AnimatePresence mode="wait" initial={false}>
          <motion.div
            key={device}
            initial={reduce ? false : { opacity: 0, y: 16, scale: 0.985 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={reduce ? { opacity: 0 } : { opacity: 0, y: -12, scale: 0.985 }}
            transition={{ duration: 0.35, ease: [0.16, 1, 0.3, 1] }}
          >
            <DemoPreview project={project} device={device} reduce={reduce} />
          </motion.div>
        </AnimatePresence>
      </div>

      {/* Info + actions */}
      <div className="relative flex flex-1 flex-col px-5 pb-5 sm:px-6 sm:pb-6">
        <p className="text-[13px] leading-relaxed text-zinc-400">{project.description}</p>

        <ul className="mt-4 flex flex-wrap gap-1.5">
          {project.tags.map((tag) => (
            <li
              key={tag}
              className="rounded-md border border-white/[0.08] bg-white/[0.03] px-2 py-1 font-mono text-[9px] uppercase tracking-[0.08em] text-zinc-400"
            >
              {tag}
            </li>
          ))}
        </ul>

        <div className="mt-5 flex flex-col gap-2 sm:flex-row">
          <motion.a
            href={project.url}
            target="_blank"
            rel="noopener noreferrer"
            whileHover={{ y: -2 }}
            whileTap={{ scale: 0.96 }}
            className="group/link flex flex-1 items-center justify-center gap-2 rounded-xl border border-white/10 bg-white/[0.04] px-4 py-2.5 text-[12.5px] font-medium text-zinc-200 transition-colors duration-300 hover:border-indigo-400/50 hover:bg-indigo-500/10 hover:text-white"
          >
            <ExternalLink className="h-3.5 w-3.5" />
            Abrir sitio real
            <ArrowUpRight className="h-3.5 w-3.5 transition-transform duration-300 group-hover/link:-translate-y-0.5 group-hover/link:translate-x-0.5" />
          </motion.a>

          <motion.button
            type="button"
            onClick={() =>
              onQuote?.({
                businessType: project.categoryLabel,
                need: project.quoteType,
              })
            }
            whileHover={{ y: -2 }}
            whileTap={{ scale: 0.96 }}
            className="flex flex-1 items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-indigo-500 via-purple-500 to-cyan-400 px-4 py-2.5 text-[12.5px] font-semibold text-white shadow-[0_10px_30px_-14px_rgba(99,102,241,0.95)] transition-shadow duration-300 hover:shadow-[0_16px_40px_-14px_rgba(99,102,241,1)]"
          >
            <MessageCircle className="h-3.5 w-3.5" />
            {siteConfig.ctaLabels.wantThis}
          </motion.button>
        </div>

        <a
          href={projectInquiryLink(project)}
          target="_blank"
          rel="noopener noreferrer"
          className="mt-3 inline-flex items-center gap-1.5 self-start font-mono text-[9px] uppercase tracking-[0.14em] text-zinc-500 transition-colors hover:text-emerald-300"
        >
          <span className="h-1 w-1 rounded-full bg-emerald-400" />
          Consultar disponibilidad
        </a>
      </div>
    </motion.article>
  );
}

export default function PortfolioShowcase({ onQuote }) {
  const { projects, eyebrow, title, subtitle } = siteConfig.portfolio;

  const demos = useMemo(
    () => projects.filter((project) => project.url && project.url.trim()),
    [projects]
  );

  return (
    <section id="proyectos" className="relative scroll-mt-24 overflow-hidden py-20 sm:py-28">
      <div aria-hidden="true" className="pointer-events-none absolute inset-0 -z-10">
        <div className="absolute left-1/2 top-0 h-72 w-[700px] -translate-x-1/2 rounded-full bg-indigo-600/10 blur-[120px]" />
        <div className="bg-dots grid-fade absolute inset-0 opacity-40" />
      </div>

      <div className="mx-auto max-w-7xl px-4 sm:px-6">
        <SectionHeading
          eyebrow={eyebrow}
          title={title}
          subtitle={subtitle}
          accentWord="Interactivas"
        >
          <motion.div
            initial={{ opacity: 0 }}
            whileInView={{ opacity: 1 }}
            viewport={{ once: true }}
            transition={{ delay: 0.25 }}
            className="mt-1 inline-flex items-center gap-2 rounded-full border border-white/10 bg-white/[0.03] px-3 py-1.5 font-mono text-[9px] uppercase tracking-[0.16em] text-zinc-400"
          >
            <Sparkles className="h-3 w-3 text-cyan-300" />
            {demos.length} demos reales desplegadas en Vercel
          </motion.div>
        </SectionHeading>

        <div className="mt-12 grid grid-cols-1 gap-6 xl:grid-cols-2">
          {demos.map((project, index) => (
            <DemoShowcaseCard
              key={project.id}
              project={project}
              index={index}
              onQuote={onQuote}
            />
          ))}
        </div>

        <motion.div
          initial={{ opacity: 0, y: 24 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, amount: 0.4 }}
          transition={{ duration: 0.7, ease: [0.16, 1, 0.3, 1] }}
          className="relative mt-12 overflow-hidden rounded-3xl border border-white/10 bg-zinc-900/50 p-6 backdrop-blur-xl sm:p-8"
        >
          <div className="pointer-events-none absolute -right-20 -top-20 h-56 w-56 rounded-full bg-indigo-600/25 blur-[80px]" />
          <div className="pointer-events-none absolute -bottom-24 -left-16 h-56 w-56 rounded-full bg-cyan-500/15 blur-[80px]" />

          <div className="relative flex flex-col items-start justify-between gap-5 sm:flex-row sm:items-center">
            <div>
              <p className="font-mono text-[10px] uppercase tracking-[0.2em] text-indigo-300">
                ¿No ves tu rubro?
              </p>
              <h3 className="mt-2 max-w-lg font-display text-xl font-semibold leading-snug text-white sm:text-2xl">
                Cada negocio necesita algo distinto.{' '}
                <span className="text-gradient-strong">Diseñemos el tuyo desde cero.</span>
              </h3>
            </div>

            <motion.button
              type="button"
              onClick={() => onQuote?.()}
              whileHover={{ y: -3 }}
              whileTap={{ scale: 0.96 }}
              className="btn-primary shrink-0 px-6 py-3.5"
            >
              <MessageCircle className="h-4 w-4" />
              {siteConfig.ctaLabels.quote}
              <ArrowUpRight className="h-4 w-4" />
            </motion.button>
          </div>
        </motion.div>
      </div>
    </section>
  );
}
