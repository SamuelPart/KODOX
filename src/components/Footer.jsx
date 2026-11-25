import { motion, useReducedMotion } from 'framer-motion';
import {
  ArrowUp,
  ArrowUpRight,
  Code2,
  Github,
  Instagram,
  Linkedin,
  Mail,
  MapPin,
  MessageCircle,
  Music2,
  Phone,
  Sparkles,
  Zap,
} from 'lucide-react';
import { siteConfig, buildWhatsAppLink } from '../data/portfolioData';

const SOCIALS = [
  { icon: Instagram, label: 'Instagram', href: siteConfig.contact.instagram },
  { icon: Github, label: 'GitHub', href: siteConfig.contact.github },
  { icon: Linkedin, label: 'LinkedIn', href: siteConfig.contact.linkedin },
  { icon: Music2, label: 'TikTok', href: siteConfig.contact.tiktok },
];

export default function Footer({ onQuote }) {
  const reduce = useReducedMotion();
  const { brand, contact, footer } = siteConfig;

  const scrollTop = () => window.scrollTo({ top: 0, behavior: 'smooth' });

  return (
    <footer className="relative overflow-hidden border-t border-white/[0.08]">
      {/* Fondo */}
      <div aria-hidden="true" className="pointer-events-none absolute inset-0 -z-10">
        <div className="absolute left-1/2 top-0 h-72 w-[800px] -translate-x-1/2 -translate-y-1/3 rounded-full bg-indigo-600/20 blur-[130px]" />
        <div className="bg-grid grid-fade absolute inset-0 opacity-30" />
      </div>

      {/* ---------- Bloque CTA gigante ---------- */}
      <div className="mx-auto max-w-6xl px-4 pt-16 sm:px-6 sm:pt-20">
        <div className="relative overflow-hidden rounded-3xl border border-white/10 bg-gradient-to-br from-indigo-600/[0.18] via-zinc-900/70 to-cyan-500/[0.12] p-6 backdrop-blur-xl sm:p-10">
          <div
            aria-hidden="true"
            className="pointer-events-none absolute -right-24 -top-24 h-64 w-64 rounded-full bg-indigo-500/25 blur-[80px]"
          />
          <div
            aria-hidden="true"
            className="pointer-events-none absolute -bottom-28 -left-16 h-64 w-64 rounded-full bg-cyan-500/15 blur-[80px]"
          />
          <div aria-hidden="true" className="bg-dots pointer-events-none absolute inset-0 opacity-20" />

          <div className="relative flex flex-col gap-8 lg:flex-row lg:items-end lg:justify-between">
            <div>
              <span className="inline-flex items-center gap-2 rounded-full border border-emerald-500/25 bg-emerald-500/[0.08] px-3 py-1.5 font-mono text-[9px] uppercase tracking-[0.16em] text-emerald-300">
                <span className="relative flex h-1.5 w-1.5">
                  <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-emerald-400 opacity-75" />
                  <span className="relative inline-flex h-1.5 w-1.5 rounded-full bg-emerald-400" />
                </span>
                {footer.availability}
              </span>

              <h2 className="mt-5 max-w-2xl font-display text-2xl font-bold leading-[1.1] tracking-tight text-white sm:text-4xl">
                {footer.headline}
                <br />
                <span className="text-gradient-strong">{footer.headlineAccent}</span>
              </h2>

              <p className="mt-4 max-w-xl text-[13px] leading-relaxed text-zinc-400 sm:text-sm">
                {footer.description}
              </p>
            </div>

            <div className="flex w-full flex-col gap-2.5 sm:w-auto sm:flex-row lg:flex-col">
              <motion.a
                href={buildWhatsAppLink()}
                target="_blank"
                rel="noopener noreferrer"
                whileHover={{ y: -3 }}
                whileTap={{ scale: 0.96 }}
                className="btn-whatsapp whitespace-nowrap px-6 py-3.5 text-[13px]"
              >
                <MessageCircle className="h-4 w-4" />
                Escribir por WhatsApp
              </motion.a>

              <motion.button
                type="button"
                onClick={() => onQuote?.()}
                whileHover={{ y: -3 }}
                whileTap={{ scale: 0.96 }}
                className="btn-primary whitespace-nowrap px-6 py-3.5 text-[13px]"
              >
                <Zap className="h-4 w-4" />
                {siteConfig.ctaLabels.nav}
              </motion.button>
            </div>
          </div>
        </div>
      </div>

      {/* ---------- Cuerpo del footer ---------- */}
      <div className="mx-auto max-w-6xl px-4 py-12 sm:px-6 sm:py-14">
        <div className="grid grid-cols-1 gap-10 sm:grid-cols-2 lg:grid-cols-[1.4fr_1fr_1fr_1.1fr]">
          {/* Marca */}
          <div>
            <a href="#inicio" className="group inline-flex items-center gap-2.5">
              <motion.span
                whileHover={{ rotate: -8, scale: 1.06 }}
                className="grid h-10 w-10 place-items-center rounded-xl bg-gradient-to-br from-indigo-500 via-purple-500 to-cyan-400 shadow-[0_8px_24px_-8px_rgba(99,102,241,0.9)]"
              >
                <Code2 className="h-5 w-5 text-white" strokeWidth={2.4} />
              </motion.span>
              <span className="flex flex-col leading-none">
                <span className="font-display text-lg font-bold tracking-tight text-white">
                  {brand.name}
                </span>
                <span className="mt-1 font-mono text-[8.5px] uppercase tracking-[0.22em] text-zinc-500">
                  {brand.studio}
                </span>
              </span>
            </a>

            <p className="mt-4 max-w-xs text-[12.5px] leading-relaxed text-zinc-500">
              {brand.shortDescription}
            </p>

            <div className="mt-4 inline-flex items-center gap-2 rounded-xl border border-white/[0.07] bg-white/[0.02] px-3 py-2">
              <Sparkles className="h-3.5 w-3.5 text-indigo-400" />
              <span className="font-mono text-[9.5px] uppercase tracking-[0.12em] text-zinc-400">
                {brand.projectsDelivered} proyectos · {brand.experienceYears} de experiencia
              </span>
            </div>

            {/* Redes */}
            <div className="mt-5 flex items-center gap-2">
              {SOCIALS.map(({ icon: Icon, label, href }) => (
                <motion.a
                  key={label}
                  href={href}
                  target="_blank"
                  rel="noopener noreferrer"
                  aria-label={label}
                  whileHover={{ y: -3, scale: 1.05 }}
                  whileTap={{ scale: 0.94 }}
                  className="grid h-9 w-9 place-items-center rounded-xl border border-white/10 bg-white/[0.03] text-zinc-400 transition-colors hover:border-indigo-400/50 hover:text-white"
                >
                  <Icon className="h-4 w-4" />
                </motion.a>
              ))}
            </div>
          </div>

          {/* Columnas de enlaces */}
          {footer.columns.map((column) => (
            <nav key={column.title} aria-label={column.title}>
              <h3 className="font-mono text-[9.5px] uppercase tracking-[0.2em] text-zinc-500">
                {column.title}
              </h3>
              <ul className="mt-4 space-y-2.5">
                {column.links.map((link) => (
                  <li key={link.label}>
                    <a
                      href={link.href}
                      className="group inline-flex items-center gap-1.5 text-[12.5px] text-zinc-400 transition-colors hover:text-white"
                    >
                      <span className="h-1 w-1 rounded-full bg-zinc-700 transition-colors group-hover:bg-indigo-400" />
                      {link.label}
                      <ArrowUpRight className="h-3 w-3 -translate-x-1 opacity-0 transition-all group-hover:translate-x-0 group-hover:opacity-100" />
                    </a>
                  </li>
                ))}
              </ul>
            </nav>
          ))}

          {/* Contacto */}
          <div>
            <h3 className="font-mono text-[9.5px] uppercase tracking-[0.2em] text-zinc-500">
              Contacto
            </h3>
            <ul className="mt-4 space-y-2.5">
              <li>
                <a
                  href={buildWhatsAppLink()}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="group flex items-center gap-2.5 text-[12.5px] text-zinc-400 transition-colors hover:text-white"
                >
                  <span className="grid h-7 w-7 shrink-0 place-items-center rounded-lg bg-emerald-500/12 text-emerald-400">
                    <Phone className="h-3.5 w-3.5" />
                  </span>
                  {contact.whatsappDisplay}
                </a>
              </li>
              <li>
                <a
                  href={`mailto:${contact.email}`}
                  className="group flex items-center gap-2.5 text-[12.5px] text-zinc-400 transition-colors hover:text-white"
                >
                  <span className="grid h-7 w-7 shrink-0 place-items-center rounded-lg bg-indigo-500/12 text-indigo-300">
                    <Mail className="h-3.5 w-3.5" />
                  </span>
                  {contact.email}
                </a>
              </li>
              <li className="flex items-center gap-2.5 text-[12.5px] text-zinc-400">
                <span className="grid h-7 w-7 shrink-0 place-items-center rounded-lg bg-cyan-500/12 text-cyan-300">
                  <MapPin className="h-3.5 w-3.5" />
                </span>
                {brand.location}
              </li>
            </ul>

            <div className="mt-4 rounded-xl border border-white/[0.07] bg-white/[0.02] px-3 py-2.5">
              <p className="font-mono text-[8.5px] uppercase tracking-[0.16em] text-zinc-600">
                Horario
              </p>
              <p className="mt-0.5 text-[11.5px] text-zinc-400">{contact.schedule}</p>
            </div>
          </div>
        </div>

        {/* Línea final */}
        <div className="divider-glow mt-10" />

        <div className="mt-5 flex flex-col items-center justify-between gap-4 sm:flex-row">
          <div className="text-center sm:text-left">
            <p className="text-[11px] text-zinc-500">{footer.legal}</p>
            <p className="mt-1 font-mono text-[9px] uppercase tracking-[0.14em] text-zinc-600">
              {footer.builtWith}
            </p>
          </div>

          <div className="flex items-center gap-3">
            <span className="inline-flex items-center gap-1.5 rounded-full border border-white/10 bg-white/[0.03] px-3 py-1.5 font-mono text-[9px] uppercase tracking-[0.12em] text-zinc-500">
              <span className="h-1.5 w-1.5 rounded-full bg-emerald-400" />
              Sistemas operativos
            </span>

            <motion.button
              type="button"
              onClick={scrollTop}
              aria-label="Volver arriba"
              whileHover={{ y: -3 }}
              whileTap={{ scale: 0.94 }}
              className="grid h-9 w-9 place-items-center rounded-xl border border-white/10 bg-white/[0.04] text-zinc-300 transition-colors hover:border-indigo-400/50 hover:text-white"
            >
              <ArrowUp className="h-4 w-4" />
            </motion.button>
          </div>
        </div>
      </div>

      {/* Marca de agua tipográfica */}
      <div aria-hidden="true" className="relative overflow-hidden">
        <motion.p
          initial={reduce ? false : { opacity: 0, y: 40 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 1, ease: [0.16, 1, 0.3, 1] }}
          className="select-none whitespace-nowrap bg-gradient-to-b from-white/[0.055] to-transparent bg-clip-text text-center font-display text-[20vw] font-bold leading-[0.78] tracking-tighter text-transparent"
        >
          {brand.name}
        </motion.p>
      </div>
    </footer>
  );
}
