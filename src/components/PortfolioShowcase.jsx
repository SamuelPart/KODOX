import { useMemo, useState } from 'react';
import { AnimatePresence, motion, useReducedMotion } from 'framer-motion';
import {
  ArrowUpRight,
  ExternalLink,
  Layers3,
  MessageCircle,
  Sparkles,
  Star,
} from 'lucide-react';
import { siteConfig, projectInquiryLink } from '../data/portfolioData';
import { SectionHeading } from './SectionPrimitives';

/* Tarjeta individual de proyecto */
function ProjectCard({ project, onQuote, index }) {
  const reduce = useReducedMotion();
  const [imageLoaded, setImageLoaded] = useState(false);

  return (
    <motion.article
      layout
      initial={reduce ? false : { opacity: 0, y: 40, scale: 0.97 }}
      animate={{ opacity: 1, y: 0, scale: 1 }}
      exit={reduce ? { opacity: 0 } : { opacity: 0, y: -18, scale: 0.96 }}
      transition={{
        duration: 0.55,
        delay: index * 0.05,
        ease: [0.16, 1, 0.3, 1],
        layout: { type: 'spring', stiffness: 240, damping: 28 },
      }}
      whileHover={reduce ? {} : { y: -6 }}
      className="group relative flex flex-col overflow-hidden rounded-3xl border border-white/10 bg-zinc-900/60 shadow-card backdrop-blur-xl transition-colors duration-300 hover:border-indigo-500/50"
    >
      {/* Resplandor superior */}
      <div
        aria-hidden="true"
        className={`pointer-events-none absolute inset-x-0 top-0 h-40 bg-gradient-to-b ${project.accent} opacity-70 transition-opacity duration-500 group-hover:opacity-100`}
      />

      {/* Imagen */}
      <div className="relative aspect-[16/10] overflow-hidden">
        <div className="absolute inset-0 animate-pulse bg-zinc-800/60" aria-hidden="true" />

        <img
          src={project.image}
          alt={project.alt}
          loading={index < 2 ? 'eager' : 'lazy'}
          decoding="async"
          onLoad={() => setImageLoaded(true)}
          className={`relative h-full w-full object-cover transition-all duration-[900ms] ease-out-expo ${
            imageLoaded ? 'scale-100 opacity-100 blur-0' : 'scale-105 opacity-0 blur-md'
          } group-hover:scale-[1.06]`}
        />

        {/* Capas de contraste */}
        <div className="absolute inset-0 bg-gradient-to-t from-[#050507] via-[#050507]/45 to-transparent" />
        <div className="absolute inset-0 bg-gradient-to-br from-indigo-900/25 via-transparent to-cyan-900/15 opacity-0 transition-opacity duration-500 group-hover:opacity-100" />

        {/* Badges */}
        <div className="absolute inset-x-3 top-3 flex flex-wrap items-center justify-between gap-2">
          <span className="inline-flex items-center gap-1.5 rounded-full border border-white/15 bg-black/55 px-2.5 py-1 font-mono text-[8.5px] uppercase tracking-[0.14em] text-zinc-200 backdrop-blur-md">
            <Layers3 className="h-2.5 w-2.5 text-cyan-300" />
            {project.categoryLabel}
          </span>

          {project.featured && (
            <span className="inline-flex items-center gap-1 rounded-full border border-amber-400/25 bg-amber-400/15 px-2.5 py-1 font-mono text-[8.5px] uppercase tracking-[0.14em] text-amber-200 backdrop-blur-md">
              <Star className="h-2.5 w-2.5 fill-amber-300 text-amber-300" />
              Destacado
            </span>
          )}
        </div>

        {/* Métricas (aparecen al hover) */}
        <div className="absolute inset-x-3 bottom-3 flex translate-y-3 items-center gap-2 opacity-0 transition-all duration-500 group-hover:translate-y-0 group-hover:opacity-100">
          {project.metrics.map((metric) => (
            <span
              key={metric.label}
              className="flex-1 rounded-xl border border-white/10 bg-black/60 px-2 py-1.5 text-center backdrop-blur-md"
            >
              <span className="block font-display text-[13px] font-bold leading-none text-white">
                {metric.value}
              </span>
              <span className="mt-0.5 block font-mono text-[7px] uppercase tracking-[0.12em] text-zinc-400">
                {metric.label}
              </span>
            </span>
          ))}
        </div>

        <span className="absolute right-3 top-1/2 -translate-y-1/2 rounded-full border border-white/10 bg-black/50 px-2 py-1 font-mono text-[8px] text-zinc-400 backdrop-blur-md">
          {project.year}
        </span>
      </div>

      {/* Contenido */}
      <div className="relative flex flex-1 flex-col p-5">
        <div className="flex items-start justify-between gap-3">
          <div>
            <h3 className="font-display text-lg font-semibold leading-tight text-white transition-colors duration-300 group-hover:text-cyan-300">
              {project.title}
            </h3>
            <p className="mt-0.5 text-[12.5px] font-medium text-indigo-300/90">
              {project.subtitle}
            </p>
          </div>
        </div>

        <p className="mt-3 text-[13px] leading-relaxed text-zinc-400">{project.description}</p>

        {/* Tags */}
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

        {/* Stack */}
        <div className="mt-3 flex flex-wrap items-center gap-x-2 gap-y-1 font-mono text-[9px] text-zinc-600">
          {project.tech.map((tech, techIndex) => (
            <span key={tech} className="inline-flex items-center gap-2">
              {tech}
              {techIndex < project.tech.length - 1 && <span className="text-zinc-700">·</span>}
            </span>
          ))}
        </div>

        {/* Acciones */}
        <div className="mt-5 flex flex-col gap-2 sm:flex-row">
          <motion.a
            href={project.url}
            target="_blank"
            rel="noopener noreferrer"
            whileHover={{ y: -2 }}
            whileTap={{ scale: 0.96 }}
            className="group/demo flex flex-1 items-center justify-center gap-2 rounded-xl border border-white/10 bg-white/[0.04] px-4 py-2.5 text-[12.5px] font-medium text-zinc-200 transition-colors duration-300 hover:border-indigo-400/50 hover:bg-indigo-500/10 hover:text-white"
          >
            <ExternalLink className="h-3.5 w-3.5" />
            {siteConfig.ctaLabels.demo}
            <ArrowUpRight className="h-3.5 w-3.5 transition-transform duration-300 group-hover/demo:-translate-y-0.5 group-hover/demo:translate-x-0.5" />
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

        {/* Enlace WhatsApp con contexto del proyecto */}
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
  const [activeFilter, setActiveFilter] = useState('all');
  const { filters, projects, eyebrow, title, subtitle } = siteConfig.portfolio;

  const visibleProjects = useMemo(
    () =>
      activeFilter === 'all'
        ? projects
        : projects.filter((project) => project.category === activeFilter),
    [activeFilter, projects]
  );

  const countFor = (filterId) =>
    filterId === 'all' ? projects.length : projects.filter((p) => p.category === filterId).length;

  return (
    <section id="proyectos" className="relative scroll-mt-24 overflow-hidden py-20 sm:py-28">
      {/* Fondo */}
      <div aria-hidden="true" className="pointer-events-none absolute inset-0 -z-10">
        <div className="absolute left-1/2 top-0 h-72 w-[700px] -translate-x-1/2 rounded-full bg-indigo-600/10 blur-[120px]" />
        <div className="bg-dots grid-fade absolute inset-0 opacity-40" />
      </div>

      <div className="mx-auto max-w-6xl px-4 sm:px-6">
        <SectionHeading
          eyebrow={eyebrow}
          title={title}
          subtitle={subtitle}
          accentWord="Interactivas"
        >
          {/* Píldora de estado */}
          <motion.div
            initial={{ opacity: 0 }}
            whileInView={{ opacity: 1 }}
            viewport={{ once: true }}
            transition={{ delay: 0.25 }}
            className="mt-1 inline-flex items-center gap-2 rounded-full border border-white/10 bg-white/[0.03] px-3 py-1.5 font-mono text-[9px] uppercase tracking-[0.16em] text-zinc-400"
          >
            <Sparkles className="h-3 w-3 text-cyan-300" />
            {projects.length} demos reales desplegadas en Vercel
          </motion.div>
        </SectionHeading>

        {/* ---------- Filtros ---------- */}
        <div className="mt-10 flex justify-center">
          <div
            role="tablist"
            aria-label="Filtrar proyectos por categoría"
            className="scrollbar-none flex max-w-full gap-1.5 overflow-x-auto rounded-2xl border border-white/10 bg-zinc-900/50 p-1.5 backdrop-blur-xl"
          >
            {filters.map((filter) => {
              const isActive = activeFilter === filter.id;

              return (
                <button
                  key={filter.id}
                  role="tab"
                  aria-selected={isActive}
                  type="button"
                  onClick={() => setActiveFilter(filter.id)}
                  className={`relative shrink-0 whitespace-nowrap rounded-xl px-3.5 py-2 text-[12px] font-medium transition-colors duration-200 ${
                    isActive ? 'text-white' : 'text-zinc-400 hover:text-zinc-200'
                  }`}
                >
                  {isActive && (
                    <motion.span
                      layoutId="portfolioFilterPill"
                      className="absolute inset-0 -z-10 rounded-xl bg-gradient-to-r from-indigo-500 to-purple-500 shadow-[0_8px_24px_-10px_rgba(99,102,241,1)]"
                      transition={{ type: 'spring', stiffness: 380, damping: 32, mass: 0.7 }}
                    />
                  )}
                  <span className="relative flex items-center gap-1.5">
                    {filter.label}
                    <span
                      className={`rounded-md px-1 py-0.5 font-mono text-[8.5px] ${
                        isActive ? 'bg-white/20 text-white' : 'bg-white/[0.06] text-zinc-500'
                      }`}
                    >
                      {countFor(filter.id)}
                    </span>
                  </span>
                </button>
              );
            })}
          </div>
        </div>

        {/* ---------- Grid animado ---------- */}
        <motion.div
          layout
          className="mt-9 grid grid-cols-1 gap-5 md:grid-cols-2 lg:grid-cols-3"
        >
          <AnimatePresence mode="popLayout">
            {visibleProjects.map((project, index) => (
              <ProjectCard
                key={project.id}
                project={project}
                index={index}
                onQuote={onQuote}
              />
            ))}
          </AnimatePresence>
        </motion.div>

        {/* Estado vacío (nunca debería verse, pero es buena práctica) */}
        <AnimatePresence>
          {visibleProjects.length === 0 && (
            <motion.p
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="mt-10 text-center text-sm text-zinc-500"
            >
              Aún no hay demos en esta categoría. Escríbeme y te muestro algo en vivo.
            </motion.p>
          )}
        </AnimatePresence>

        {/* ---------- CTA final ---------- */}
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
