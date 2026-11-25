import { useEffect, useState } from 'react';
import {
  AnimatePresence,
  motion,
  useReducedMotion,
  useScroll,
  useSpring,
} from 'framer-motion';
import { ArrowUp, MessageCircle, Zap } from 'lucide-react';

import Navbar from './components/Navbar';
import HeroSection from './components/HeroSection';
import TechMarquee from './components/TechMarquee';
import PortfolioShowcase from './components/PortfolioShowcase';
import ServicesGrid from './components/ServicesGrid';
import ProcessTimeline from './components/ProcessTimeline';
import PricingPlans from './components/PricingPlans';
import InteractiveQuoteModal from './components/InteractiveQuoteModal';
import FaqSection from './components/FaqSection';
import Footer from './components/Footer';
import { siteConfig, buildWhatsAppLink } from './data/portfolioData';

/* ------------------------------------------------------------------ Barra de
   progreso de lectura global (segunda capa, sobre el navbar) */
function ReadingProgress() {
  const { scrollYProgress } = useScroll();
  const scaleX = useSpring(scrollYProgress, {
    stiffness: 140,
    damping: 30,
    restDelta: 0.001,
  });

  return (
    <motion.div
      aria-hidden="true"
      style={{ scaleX }}
      className="fixed inset-x-0 top-0 z-[75] h-[2px] origin-left bg-gradient-to-r from-cyan-400 via-indigo-400 to-purple-500"
    />
  );
}

/* ------------------------------------------------------- Botones flotantes */
function FloatingActions({ onQuote }) {
  const [visible, setVisible] = useState(false);
  const reduce = useReducedMotion();

  useEffect(() => {
    const onScroll = () => setVisible(window.scrollY > 700);
    onScroll();
    window.addEventListener('scroll', onScroll, { passive: true });
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  return (
    <div className="fixed bottom-4 right-4 z-[65] flex flex-col items-end gap-2.5 sm:bottom-6 sm:right-6">
      <AnimatePresence>
        {visible && (
          <motion.button
            key="scroll-top"
            type="button"
            onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}
            aria-label="Volver arriba"
            initial={{ opacity: 0, scale: 0.7, y: 12 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.7, y: 12 }}
            whileHover={{ y: -3 }}
            whileTap={{ scale: 0.92 }}
            className="grid h-10 w-10 place-items-center rounded-full border border-white/10 bg-zinc-900/90 text-zinc-300 shadow-xl backdrop-blur-xl transition-colors hover:border-indigo-400/50 hover:text-white"
          >
            <ArrowUp className="h-4 w-4" />
          </motion.button>
        )}
      </AnimatePresence>

      <motion.button
        type="button"
        onClick={onQuote}
        aria-label="Abrir cotizador rápido"
        initial={reduce ? false : { opacity: 0, scale: 0.8 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ delay: 2.1, type: 'spring', stiffness: 260, damping: 20 }}
        whileHover={{ y: -3, scale: 1.03 }}
        whileTap={{ scale: 0.95 }}
        className="group relative hidden h-11 items-center gap-2 rounded-full border border-white/10 bg-zinc-950/90 px-4 text-[12px] font-semibold text-white shadow-2xl backdrop-blur-xl sm:flex"
      >
        <Zap className="h-3.5 w-3.5 text-cyan-300" />
        Cotizar
        <span className="absolute inset-0 -z-10 rounded-full bg-gradient-to-r from-indigo-500/25 to-cyan-400/20 opacity-0 blur transition-opacity group-hover:opacity-100" />
      </motion.button>

      <motion.a
        href={buildWhatsAppLink()}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="Escribir por WhatsApp"
        initial={reduce ? false : { opacity: 0, scale: 0.8 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ delay: 1.9, type: 'spring', stiffness: 260, damping: 20 }}
        whileHover={{ y: -3, scale: 1.05 }}
        whileTap={{ scale: 0.93 }}
        className="relative grid h-12 w-12 place-items-center rounded-full bg-[#16A34A] p-3.5 text-white shadow-glow-wa"
      >
        <span className="absolute inset-0 animate-pulse-ring rounded-full bg-emerald-500/50" />
        <MessageCircle className="relative h-5 w-5" />
        <span className="absolute right-0 top-0 h-2.5 w-2.5 rounded-full border-2 border-[#16A34A] bg-emerald-300" />
      </motion.a>
    </div>
  );
}

/* ---------------------------------------------------------------- Aplicación */
export default function App() {
  const [quoteOpen, setQuoteOpen] = useState(false);
  const [preselect, setPreselect] = useState(null);

  const openQuote = (payload = null) => {
    setPreselect(payload);
    setQuoteOpen(true);
  };

  const closeQuote = () => {
    setQuoteOpen(false);
    // Limpia la preselección después de la animación de salida
    setTimeout(() => setPreselect(null), 400);
  };

  return (
    <div className="relative min-h-screen overflow-x-clip bg-obsidian text-zinc-300 selection:bg-indigo-500/40">
      <ReadingProgress />

      <Navbar onQuote={() => openQuote()} />

      <main>
        <HeroSection />
        <TechMarquee />
        <PortfolioShowcase onQuote={openQuote} />
        <ServicesGrid onQuote={openQuote} />
        <ProcessTimeline />
        <PricingPlans onQuote={openQuote} />
        <FaqSection onQuote={() => openQuote()} />
      </main>

      <Footer onQuote={() => openQuote()} />

      <FloatingActions onQuote={() => openQuote()} />

      <InteractiveQuoteModal open={quoteOpen} onClose={closeQuote} preselect={preselect} />

      {/* Nota de accesibilidad / SEO oculta */}
      <p className="sr-only">
        {siteConfig.brand.name} — {siteConfig.brand.studio}. Desarrollo web en Trujillo, Perú:
        cartas digitales QR, tiendas con carrito de WhatsApp, apps Android e integraciones con
        inteligencia artificial. Pago único, sin mensualidades.
      </p>
    </div>
  );
}
