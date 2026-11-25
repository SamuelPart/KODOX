import { useEffect, useState } from 'react';
import {
  AnimatePresence,
  motion,
  useMotionValueEvent,
  useScroll,
  useSpring,
} from 'framer-motion';
import { ArrowUpRight, Menu, X, Zap, Code2 } from 'lucide-react';
import { siteConfig, buildWhatsAppLink } from '../data/portfolioData';

export default function Navbar({ onQuote }) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const [hovered, setHovered] = useState(null);

  const { scrollYProgress, scrollY } = useScroll();
  const progress = useSpring(scrollYProgress, {
    stiffness: 120,
    damping: 28,
    mass: 0.35,
    restDelta: 0.001,
  });

  useMotionValueEvent(scrollY, 'change', (value) => {
    setScrolled(value > 24);
  });

  // Bloquea el scroll del body cuando el menú móvil está abierto
  useEffect(() => {
    if (mobileOpen) {
      const original = document.body.style.overflow;
      document.body.style.overflow = 'hidden';
      return () => {
        document.body.style.overflow = original;
      };
    }
    return undefined;
  }, [mobileOpen]);

  // Cierra el menú móvil si se pasa a escritorio
  useEffect(() => {
    const onResize = () => {
      if (window.innerWidth >= 1024) setMobileOpen(false);
    };
    window.addEventListener('resize', onResize);
    return () => window.removeEventListener('resize', onResize);
  }, []);

  const handleAnchor = (event, href) => {
    event.preventDefault();
    setMobileOpen(false);
    const target = document.querySelector(href);
    if (target) {
      const top = target.getBoundingClientRect().top + window.scrollY - 88;
      window.scrollTo({ top, behavior: 'smooth' });
    }
  };

  const handleQuote = () => {
    setMobileOpen(false);
    onQuote?.();
  };

  return (
    <>
      {/* ---------- Barra de progreso de lectura ---------- */}
      <motion.div
        aria-hidden="true"
        style={{ scaleX: progress }}
        className="fixed inset-x-0 top-0 z-[70] h-[3px] origin-left bg-gradient-to-r from-indigo-500 via-purple-500 to-cyan-400 shadow-[0_0_18px_rgba(99,102,241,0.9)]"
      />

      <motion.header
        initial={{ y: -80, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.8, ease: [0.16, 1, 0.3, 1] }}
        className="fixed inset-x-0 top-0 z-[60] px-3 pt-3 sm:px-5 sm:pt-4"
      >
        <nav
          className={`mx-auto flex max-w-6xl items-center justify-between gap-3 rounded-2xl border px-4 py-2.5 transition-all duration-500 sm:px-5 ${
            scrolled
              ? 'border-white/10 bg-obsidian/80 shadow-[0_20px_60px_-30px_rgba(0,0,0,1)] backdrop-blur-2xl'
              : 'border-transparent bg-transparent'
          }`}
        >
          {/* Logo */}
          <a
            href="#inicio"
            onClick={(event) => handleAnchor(event, '#inicio')}
            className="group relative flex items-center gap-2.5"
            aria-label={`${siteConfig.brand.name} — inicio`}
          >
            <motion.span
              whileHover={{ rotate: -8, scale: 1.06 }}
              whileTap={{ scale: 0.94 }}
              transition={{ type: 'spring', stiffness: 380, damping: 16 }}
              className="relative grid h-9 w-9 place-items-center rounded-xl bg-gradient-to-br from-indigo-500 via-purple-500 to-cyan-400 shadow-[0_8px_24px_-8px_rgba(99,102,241,0.9)]"
            >
              <Code2 className="h-[18px] w-[18px] text-white" strokeWidth={2.4} />
              <span className="absolute -right-1 -top-1 h-2 w-2 rounded-full bg-cyan-300 shadow-[0_0_8px_2px_rgba(34,211,238,0.8)]" />
            </motion.span>

            <span className="flex flex-col leading-none">
              <span className="font-display text-[17px] font-bold tracking-tight text-white">
                {siteConfig.brand.name}
              </span>
              <span className="mt-1 font-mono text-[8.5px] uppercase tracking-[0.22em] text-zinc-500">
                {siteConfig.brand.studio}
              </span>
            </span>
          </a>

          {/* Enlaces desktop */}
          <ul
            className="hidden items-center gap-1 lg:flex"
            onMouseLeave={() => setHovered(null)}
          >
            {siteConfig.navigation.map((link) => (
              <li key={link.href} className="relative">
                <a
                  href={link.href}
                  onClick={(event) => handleAnchor(event, link.href)}
                  onMouseEnter={() => setHovered(link.href)}
                  className="relative block rounded-full px-3.5 py-2 text-[13px] font-medium text-zinc-400 transition-colors duration-200 hover:text-white"
                >
                  {hovered === link.href && (
                    <motion.span
                      layoutId="nav-hover-pill"
                      className="absolute inset-0 -z-10 rounded-full border border-white/10 bg-white/[0.06]"
                      transition={{ type: 'spring', stiffness: 380, damping: 30 }}
                    />
                  )}
                  {link.label}
                </a>
              </li>
            ))}
          </ul>

          {/* CTA desktop + menú móvil */}
          <div className="flex items-center gap-2">
            <motion.button
              type="button"
              onClick={handleQuote}
              whileHover={{ y: -2 }}
              whileTap={{ scale: 0.96 }}
              className="group relative hidden overflow-hidden rounded-full bg-gradient-to-r from-indigo-500 via-purple-500 to-cyan-400 p-[1px] sm:block"
            >
              <span className="relative flex items-center gap-1.5 rounded-full bg-obsidian/85 px-4 py-2 text-[12.5px] font-semibold text-white transition-colors duration-300 group-hover:bg-transparent">
                <Zap className="h-3.5 w-3.5 text-cyan-300 transition-colors group-hover:text-white" />
                {siteConfig.ctaLabels.nav}
                <ArrowUpRight className="h-3.5 w-3.5" />
              </span>
              <span className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/30 to-transparent transition-transform duration-700 group-hover:translate-x-full" />
            </motion.button>

            <motion.button
              type="button"
              aria-label={mobileOpen ? 'Cerrar menú' : 'Abrir menú'}
              aria-expanded={mobileOpen}
              onClick={() => setMobileOpen((value) => !value)}
              whileTap={{ scale: 0.92 }}
              className="grid h-10 w-10 place-items-center rounded-xl border border-white/10 bg-white/[0.04] text-zinc-200 backdrop-blur-md lg:hidden"
            >
              <AnimatePresence mode="wait" initial={false}>
                {mobileOpen ? (
                  <motion.span
                    key="close"
                    initial={{ rotate: -90, opacity: 0 }}
                    animate={{ rotate: 0, opacity: 1 }}
                    exit={{ rotate: 90, opacity: 0 }}
                    transition={{ duration: 0.2 }}
                  >
                    <X className="h-5 w-5" />
                  </motion.span>
                ) : (
                  <motion.span
                    key="open"
                    initial={{ rotate: 90, opacity: 0 }}
                    animate={{ rotate: 0, opacity: 1 }}
                    exit={{ rotate: -90, opacity: 0 }}
                    transition={{ duration: 0.2 }}
                  >
                    <Menu className="h-5 w-5" />
                  </motion.span>
                )}
              </AnimatePresence>
            </motion.button>
          </div>
        </nav>
      </motion.header>

      {/* ---------- Menú móvil ---------- */}
      <AnimatePresence>
        {mobileOpen && (
          <motion.div
            key="mobile-menu"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.25 }}
            className="fixed inset-0 z-[55] lg:hidden"
          >
            <motion.div
              className="absolute inset-0 bg-obsidian/80 backdrop-blur-xl"
              onClick={() => setMobileOpen(false)}
            />

            <motion.div
              initial={{ y: -24, opacity: 0, scale: 0.98 }}
              animate={{ y: 0, opacity: 1, scale: 1 }}
              exit={{ y: -24, opacity: 0, scale: 0.98 }}
              transition={{ duration: 0.35, ease: [0.16, 1, 0.3, 1] }}
              className="absolute inset-x-3 top-[86px] overflow-hidden rounded-3xl border border-white/10 bg-zinc-950/95 p-5 shadow-2xl"
            >
              <div className="pointer-events-none absolute -right-16 -top-16 h-40 w-40 rounded-full bg-indigo-600/25 blur-3xl" />

              <ul className="relative space-y-1">
                {siteConfig.navigation.map((link, index) => (
                  <motion.li
                    key={link.href}
                    initial={{ opacity: 0, x: -16 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: 0.06 + index * 0.05, duration: 0.35 }}
                  >
                    <a
                      href={link.href}
                      onClick={(event) => handleAnchor(event, link.href)}
                      className="flex items-center justify-between rounded-2xl px-3 py-3 text-base font-medium text-zinc-200 transition-colors hover:bg-white/[0.06] hover:text-white"
                    >
                      <span className="flex items-center gap-3">
                        <span className="font-mono text-[10px] text-indigo-400">
                          0{index + 1}
                        </span>
                        {link.label}
                      </span>
                      <ArrowUpRight className="h-4 w-4 text-zinc-500" />
                    </a>
                  </motion.li>
                ))}
              </ul>

              <div className="relative mt-4 space-y-2.5">
                <button
                  type="button"
                  onClick={handleQuote}
                  className="btn-primary w-full py-3.5"
                >
                  <Zap className="h-4 w-4" />
                  {siteConfig.ctaLabels.nav}
                </button>
                <a
                  href={buildWhatsAppLink()}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="btn-whatsapp w-full py-3.5"
                >
                  {siteConfig.ctaLabels.heroSecondary}
                </a>
              </div>

              <p className="relative mt-4 text-center font-mono text-[10px] uppercase tracking-[0.18em] text-zinc-500">
                {siteConfig.contact.schedule}
              </p>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
}
