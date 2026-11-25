import { motion, useReducedMotion } from 'framer-motion';
import {
  ArrowUpRight,
  Building2,
  Check,
  Code2,
  Gauge,
  LayoutDashboard,
  MessageCircle,
  MonitorSmartphone,
  QrCode,
  ShoppingBag,
  Smartphone,
  Sparkles,
  Users,
} from 'lucide-react';
import { siteConfig, quoteLink } from '../data/portfolioData';
import { SectionHeading } from './SectionPrimitives';

const ICONS = {
  QrCode,
  ShoppingBag,
  MonitorSmartphone,
  Sparkles,
  Smartphone,
  LayoutDashboard,
  Gauge,
  Building2,
  Code2,
};

function ServiceCard({ service, index, onQuote }) {
  const reduce = useReducedMotion();
  const Icon = ICONS[service.icon] || Code2;

  return (
    <motion.article
      initial={reduce ? false : { opacity: 0, y: 34, filter: 'blur(6px)' }}
      whileInView={{ opacity: 1, y: 0, filter: 'blur(0px)' }}
      viewport={{ once: true, amount: 0.2 }}
      transition={{ duration: 0.65, delay: index * 0.07, ease: [0.16, 1, 0.3, 1] }}
      whileHover={reduce ? {} : { y: -6 }}
      className={`group relative flex flex-col overflow-hidden rounded-3xl border border-white/10 bg-zinc-900/60 p-5 backdrop-blur-xl transition-colors duration-300 hover:border-indigo-500/50 sm:p-6 ${service.span}`}
    >
      {/* Glow de hover */}
      <div
        aria-hidden="true"
        className="pointer-events-none absolute -right-16 -top-16 h-40 w-40 rounded-full bg-indigo-600/0 blur-[60px] transition-all duration-500 group-hover:bg-indigo-600/25"
      />
      <div
        aria-hidden="true"
        className="pointer-events-none absolute inset-x-0 bottom-0 h-px bg-gradient-to-r from-transparent via-indigo-500/50 to-transparent opacity-0 transition-opacity duration-500 group-hover:opacity-100"
      />

      <div className="relative flex items-start justify-between gap-3">
        <motion.span
          whileHover={{ rotate: -6, scale: 1.08 }}
          transition={{ type: 'spring', stiffness: 320, damping: 18 }}
          className="grid h-11 w-11 place-items-center rounded-2xl border border-white/10 bg-gradient-to-br from-indigo-500/25 via-purple-500/15 to-cyan-500/15 text-indigo-200 shadow-[inset_0_1px_0_rgba(255,255,255,0.08)]"
        >
          <Icon className="h-5 w-5" strokeWidth={2} />
        </motion.span>

        <div className="flex flex-col items-end gap-1.5">
          <span className="font-mono text-[9px] uppercase tracking-[0.2em] text-zinc-600">
            {String(index + 1).padStart(2, '0')}
          </span>
          {service.badge && (
            <span className="rounded-full border border-cyan-400/25 bg-cyan-400/10 px-2 py-0.5 font-mono text-[8px] uppercase tracking-[0.12em] text-cyan-200">
              {service.badge}
            </span>
          )}
        </div>
      </div>

      <h3 className="relative mt-5 font-display text-[17px] font-semibold leading-snug text-white transition-colors duration-300 group-hover:text-cyan-200 sm:text-lg">
        {service.title}
      </h3>

      <p className="relative mt-2.5 text-[13px] leading-relaxed text-zinc-400">
        {service.description}
      </p>

      <div className="relative mt-4 inline-flex items-center gap-1.5 self-start rounded-lg border border-white/[0.07] bg-white/[0.03] px-2.5 py-1.5">
        <Users className="h-3 w-3 text-indigo-400" />
        <span className="text-[10.5px] text-zinc-400">{service.idealFor}</span>
      </div>

      <ul className="relative mt-4 flex flex-1 flex-col gap-1.5">
        {service.bullets.map((bullet) => (
          <li key={bullet} className="flex items-start gap-2 text-[12px] text-zinc-400">
            <span className="mt-0.5 grid h-4 w-4 shrink-0 place-items-center rounded-full bg-emerald-500/15">
              <Check className="h-2.5 w-2.5 text-emerald-400" strokeWidth={3} />
            </span>
            {bullet}
          </li>
        ))}
      </ul>

      <div className="relative mt-5 flex items-center gap-2">
        <button
          type="button"
          onClick={() => onQuote?.({ businessType: service.idealFor, need: service.title })}
          className="inline-flex flex-1 items-center justify-center gap-1.5 rounded-xl border border-white/10 bg-white/[0.03] px-3 py-2 text-[11.5px] font-medium text-zinc-300 transition-all duration-300 hover:border-indigo-400/50 hover:bg-indigo-500/10 hover:text-white"
        >
          <MessageCircle className="h-3.5 w-3.5" />
          Cotizar este servicio
        </button>

        <a
          href={quoteLink(service.title.toLowerCase())}
          target="_blank"
          rel="noopener noreferrer"
          aria-label={`Consultar por WhatsApp: ${service.title}`}
          className="grid h-9 w-9 shrink-0 place-items-center rounded-xl bg-[#16A34A] text-white transition-transform duration-300 hover:scale-105"
        >
          <ArrowUpRight className="h-4 w-4" />
        </a>
      </div>
    </motion.article>
  );
}

export default function ServicesGrid({ onQuote }) {
  const { eyebrow, title, subtitle, items } = siteConfig.services;

  return (
    <section id="servicios" className="relative scroll-mt-24 overflow-hidden py-20 sm:py-28">
      <div aria-hidden="true" className="pointer-events-none absolute inset-0 -z-10">
        <div className="absolute right-0 top-1/4 h-80 w-80 rounded-full bg-cyan-500/10 blur-[120px]" />
        <div className="absolute -left-20 bottom-1/4 h-80 w-80 rounded-full bg-indigo-600/10 blur-[120px]" />
        <div className="bg-grid grid-fade absolute inset-0 opacity-30" />
      </div>

      <div className="mx-auto max-w-6xl px-4 sm:px-6">
        <SectionHeading eyebrow={eyebrow} title={title} subtitle={subtitle} accentWord="medida" />

        {/* Bento grid */}
        <div className="mt-12 grid grid-cols-1 gap-4 sm:gap-5 md:grid-cols-2 lg:grid-cols-6">
          {items.map((service, index) => (
            <ServiceCard key={service.id} service={service} index={index} onQuote={onQuote} />
          ))}
        </div>

        {/* Nota de valor */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.7 }}
          className="mt-10 flex flex-col items-center justify-between gap-4 rounded-2xl border border-white/[0.07] bg-white/[0.02] px-5 py-4 sm:flex-row"
        >
          <p className="text-center text-[13px] text-zinc-400 sm:text-left">
            <span className="font-semibold text-white">¿Necesitas algo que no está en la lista?</span>{' '}
            He construido integraciones con pagos, reservas, mapas, WhatsApp API e IA.
          </p>
          <button
            type="button"
            onClick={() => onQuote?.()}
            className="btn-ghost shrink-0 px-5 py-2.5 text-[12.5px]"
          >
            Cuéntame tu idea
            <ArrowUpRight className="h-3.5 w-3.5" />
          </button>
        </motion.div>
      </div>
    </section>
  );
}
