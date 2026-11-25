import { useState } from 'react';
import { motion, useReducedMotion } from 'framer-motion';
import {
  BadgeCheck,
  Building2,
  Check,
  Crown,
  Info,
  MessageCircle,
  ShieldCheck,
  Sparkles,
  Sprout,
  X,
  Zap,
} from 'lucide-react';
import { siteConfig, buildWhatsAppLink } from '../data/portfolioData';
import { SectionHeading } from './SectionPrimitives';

const PLAN_ICONS = { Sprout, Crown, Building2, Zap };

function PlanCard({ plan, index, onQuote }) {
  const reduce = useReducedMotion();
  const [hovered, setHovered] = useState(false);
  const Icon = PLAN_ICONS[plan.icon] || Zap;

  const message = `Hola KODOX, me interesa el ${plan.name} (${plan.pricePrefix.toLowerCase()} ${plan.price} ${plan.period.toLowerCase()}). ¿Me das más detalles para empezar?`;

  return (
    <motion.div
      initial={reduce ? false : { opacity: 0, y: 40 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, amount: 0.2 }}
      transition={{ duration: 0.7, delay: index * 0.1, ease: [0.16, 1, 0.3, 1] }}
      whileHover={reduce ? {} : { y: -6 }}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      className={`relative flex flex-col overflow-hidden rounded-3xl border p-6 backdrop-blur-xl transition-colors duration-300 ${
        plan.highlighted
          ? 'border-indigo-400/40 bg-gradient-to-b from-indigo-600/[0.14] via-zinc-900/70 to-zinc-900/70 shadow-glow-indigo'
          : 'border-white/10 bg-zinc-900/60 hover:border-indigo-500/40'
      }`}
    >
      {/* Destello animado */}
      {plan.highlighted && (
        <motion.div
          aria-hidden="true"
          animate={reduce ? {} : { opacity: [0.35, 0.75, 0.35] }}
          transition={{ duration: 5, repeat: Infinity, ease: 'easeInOut' }}
          className="pointer-events-none absolute -top-24 left-1/2 h-48 w-64 -translate-x-1/2 rounded-full bg-indigo-500/30 blur-[70px]"
        />
      )}

      <div className="bg-grid-sm pointer-events-none absolute inset-0 opacity-20" />

      {/* Badge */}
      {plan.badge && (
        <motion.div
          initial={reduce ? false : { opacity: 0, y: -12, scale: 0.9 }}
          whileInView={{ opacity: 1, y: 0, scale: 1 }}
          viewport={{ once: true }}
          transition={{ delay: 0.35 + index * 0.1, type: 'spring', stiffness: 260, damping: 18 }}
          className="absolute right-4 top-4 z-10"
        >
          <span className="relative inline-flex items-center gap-1 rounded-full bg-gradient-to-r from-indigo-500 via-purple-500 to-cyan-400 px-2.5 py-1 font-mono text-[8.5px] font-bold uppercase tracking-[0.14em] text-white shadow-[0_6px_20px_-6px_rgba(99,102,241,1)]">
            <Sparkles className="h-2.5 w-2.5" />
            {plan.badge}
          </span>
        </motion.div>
      )}

      {/* Encabezado */}
      <div className="relative flex items-center gap-3">
        <span
          className={`grid h-11 w-11 place-items-center rounded-2xl border ${
            plan.highlighted
              ? 'border-indigo-400/30 bg-indigo-500/15 text-indigo-200'
              : 'border-white/10 bg-white/[0.04] text-zinc-300'
          }`}
        >
          <Icon className="h-5 w-5" />
        </span>
        <div>
          <h3 className="font-display text-base font-semibold leading-tight text-white sm:text-[17px]">
            {plan.name}
          </h3>
          <p className="mt-0.5 text-[11px] leading-tight text-zinc-500">{plan.tagline}</p>
        </div>
      </div>

      {/* Precio */}
      <div className="relative mt-6">
        <div className="flex items-end gap-2">
          <div>
            <span className="block font-mono text-[9px] uppercase tracking-[0.18em] text-zinc-500">
              {plan.pricePrefix}
            </span>
            <span className="font-display text-[2.3rem] font-bold leading-none tracking-tight text-white">
              {plan.price}
            </span>
          </div>
          <span className="mb-1 rounded-full border border-emerald-500/25 bg-emerald-500/10 px-2 py-0.5 font-mono text-[8.5px] uppercase tracking-[0.12em] text-emerald-300">
            {plan.period}
          </span>
        </div>

        <p className="mt-3 min-h-[48px] text-[12.5px] leading-relaxed text-zinc-400">
          {plan.description}
        </p>
      </div>

      <div className="divider-glow my-5" />

      {/* Características */}
      <ul className="relative flex flex-1 flex-col gap-2">
        {plan.features.map((feature, featureIndex) => (
          <motion.li
            key={feature}
            initial={reduce ? false : { opacity: 0, x: -8 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.4, delay: 0.2 + featureIndex * 0.035 }}
            className="flex items-start gap-2 text-[12.5px] leading-snug text-zinc-300"
          >
            <span
              className={`mt-0.5 grid h-4 w-4 shrink-0 place-items-center rounded-full ${
                plan.highlighted ? 'bg-indigo-500/25' : 'bg-emerald-500/15'
              }`}
            >
              <Check
                className={`h-2.5 w-2.5 ${
                  plan.highlighted ? 'text-indigo-300' : 'text-emerald-400'
                }`}
                strokeWidth={3}
              />
            </span>
            {feature}
          </motion.li>
        ))}

        {plan.excluded.map((feature) => (
          <li
            key={feature}
            className="flex items-start gap-2 text-[12.5px] leading-snug text-zinc-600 line-through decoration-zinc-700"
          >
            <span className="mt-0.5 grid h-4 w-4 shrink-0 place-items-center rounded-full bg-white/[0.04]">
              <X className="h-2.5 w-2.5 text-zinc-600" strokeWidth={3} />
            </span>
            {feature}
          </li>
        ))}
      </ul>

      {/* CTA */}
      <div className="relative mt-6 space-y-2">
        <motion.a
          href={buildWhatsAppLink(message)}
          target="_blank"
          rel="noopener noreferrer"
          whileHover={{ y: -2 }}
          whileTap={{ scale: 0.96 }}
          className={`flex w-full items-center justify-center gap-2 rounded-xl px-4 py-3 text-[13px] font-semibold transition-all duration-300 ${
            plan.highlighted
              ? 'bg-gradient-to-r from-indigo-500 via-purple-500 to-cyan-400 text-white shadow-[0_14px_40px_-14px_rgba(99,102,241,0.95)]'
              : 'border border-white/10 bg-white/[0.04] text-zinc-200 hover:border-indigo-400/50 hover:bg-indigo-500/10 hover:text-white'
          }`}
        >
          <MessageCircle className="h-4 w-4" />
          {plan.cta}
        </motion.a>

        <button
          type="button"
          onClick={() => onQuote?.({ plan: plan.name })}
          className="w-full rounded-xl py-1.5 font-mono text-[9.5px] uppercase tracking-[0.16em] text-zinc-500 transition-colors hover:text-indigo-300"
        >
          O usar el cotizador interactivo
        </button>
      </div>

      {/* Borde animado en hover */}
      {hovered && !plan.highlighted && (
        <motion.span
          layoutId="plan-hover-outline"
          className="pointer-events-none absolute inset-0 rounded-3xl ring-1 ring-indigo-400/40"
          transition={{ type: 'spring', stiffness: 300, damping: 30 }}
        />
      )}
    </motion.div>
  );
}

export default function PricingPlans({ onQuote }) {
  const { eyebrow, title, subtitle, plans, guarantees, note } = siteConfig.pricing;

  return (
    <section id="planes" className="relative scroll-mt-24 overflow-hidden py-20 sm:py-28">
      <div aria-hidden="true" className="pointer-events-none absolute inset-0 -z-10">
        <div className="absolute left-1/2 top-0 h-80 w-[760px] -translate-x-1/2 rounded-full bg-indigo-600/12 blur-[130px]" />
        <div className="bg-grid grid-fade absolute inset-0 opacity-25" />
      </div>

      <div className="mx-auto max-w-6xl px-4 sm:px-6">
        <SectionHeading eyebrow={eyebrow} title={title} subtitle={subtitle} accentWord="Transparente">
          <motion.div
            initial={{ opacity: 0 }}
            whileInView={{ opacity: 1 }}
            viewport={{ once: true }}
            transition={{ delay: 0.25 }}
            className="mt-2 flex flex-wrap items-center justify-center gap-2"
          >
            <span className="inline-flex items-center gap-1.5 rounded-full border border-emerald-500/25 bg-emerald-500/[0.08] px-3 py-1.5 font-mono text-[9.5px] uppercase tracking-[0.14em] text-emerald-300">
              <ShieldCheck className="h-3 w-3" />
              Pago único · Sin mensualidades
            </span>
            <span className="inline-flex items-center gap-1.5 rounded-full border border-white/10 bg-white/[0.03] px-3 py-1.5 font-mono text-[9.5px] uppercase tracking-[0.14em] text-zinc-400">
              <BadgeCheck className="h-3 w-3 text-indigo-400" />
              Hosting gratis de por vida
            </span>
          </motion.div>
        </SectionHeading>

        {/* Planes */}
        <div className="mt-12 grid grid-cols-1 items-stretch gap-5 md:grid-cols-2 lg:grid-cols-3">
          {plans.map((plan, index) => (
            <PlanCard key={plan.id} plan={plan} index={index} onQuote={onQuote} />
          ))}
        </div>

        {/* Garantías */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.7 }}
          className="mt-8 grid grid-cols-1 gap-3 sm:grid-cols-3"
        >
          {guarantees.map((guarantee, index) => (
            <div
              key={guarantee}
              className="flex items-center gap-2.5 rounded-2xl border border-white/[0.07] bg-white/[0.02] px-4 py-3"
            >
              <span className="grid h-7 w-7 shrink-0 place-items-center rounded-lg bg-emerald-500/12 text-emerald-400">
                <Check className="h-3.5 w-3.5" strokeWidth={3} />
              </span>
              <span className="text-[12px] text-zinc-300">{guarantee}</span>
            </div>
          ))}
        </motion.div>

        {/* Nota */}
        <div className="mt-5 flex items-start gap-2 rounded-2xl border border-white/[0.06] bg-white/[0.015] px-4 py-3">
          <Info className="mt-0.5 h-3.5 w-3.5 shrink-0 text-zinc-500" />
          <p className="text-[11.5px] leading-relaxed text-zinc-500">{note}</p>
        </div>
      </div>
    </section>
  );
}
