import { useState } from 'react';
import { AnimatePresence, motion, useReducedMotion } from 'framer-motion';
import { ArrowUpRight, HelpCircle, MessageCircle, Minus, Plus } from 'lucide-react';
import { siteConfig, buildWhatsAppLink } from '../data/portfolioData';
import { SectionHeading } from './SectionPrimitives';

function FaqItem({ item, index, open, onToggle }) {
  const reduce = useReducedMotion();

  return (
    <motion.div
      initial={reduce ? false : { opacity: 0, y: 20 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, amount: 0.4 }}
      transition={{ duration: 0.55, delay: index * 0.06, ease: [0.16, 1, 0.3, 1] }}
      className={`overflow-hidden rounded-2xl border backdrop-blur-xl transition-colors duration-300 ${
        open
          ? 'border-indigo-500/40 bg-zinc-900/80'
          : 'border-white/10 bg-zinc-900/50 hover:border-white/20'
      }`}
    >
      <h3>
        <button
          type="button"
          onClick={onToggle}
          aria-expanded={open}
          aria-controls={`faq-panel-${item.id}`}
          className="flex w-full items-center justify-between gap-4 px-5 py-4 text-left"
        >
          <span className="flex items-start gap-3">
            <span
              className={`mt-0.5 font-mono text-[10px] transition-colors ${
                open ? 'text-indigo-400' : 'text-zinc-600'
              }`}
            >
              {String(index + 1).padStart(2, '0')}
            </span>
            <span
              className={`font-display text-[14px] font-medium leading-snug transition-colors sm:text-[15px] ${
                open ? 'text-white' : 'text-zinc-200'
              }`}
            >
              {item.question}
            </span>
          </span>

          <motion.span
            animate={{ rotate: open ? 90 : 0 }}
            transition={{ duration: 0.3, ease: [0.16, 1, 0.3, 1] }}
            className={`grid h-7 w-7 shrink-0 place-items-center rounded-full border transition-colors ${
              open
                ? 'border-indigo-400/40 bg-indigo-500/20 text-indigo-200'
                : 'border-white/10 bg-white/[0.04] text-zinc-400'
            }`}
          >
            {open ? <Minus className="h-3.5 w-3.5" /> : <Plus className="h-3.5 w-3.5" />}
          </motion.span>
        </button>
      </h3>

      <AnimatePresence initial={false}>
        {open && (
          <motion.div
            id={`faq-panel-${item.id}`}
            key="content"
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: 'auto', opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.38, ease: [0.16, 1, 0.3, 1] }}
            className="overflow-hidden"
          >
            <motion.div
              initial={{ y: -8 }}
              animate={{ y: 0 }}
              exit={{ y: -8 }}
              transition={{ duration: 0.3 }}
              className="px-5 pb-5 pl-[3.1rem] pr-6"
            >
              <div className="divider-glow mb-3" />
              <p className="text-[12.5px] leading-relaxed text-zinc-400 sm:text-[13px]">
                {item.answer}
              </p>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
}

export default function FaqSection({ onQuote }) {
  const { eyebrow, title, subtitle, items } = siteConfig.faq;
  const [openId, setOpenId] = useState(items[0]?.id ?? null);

  const toggle = (id) => setOpenId((current) => (current === id ? null : id));

  return (
    <section id="faq" className="relative scroll-mt-24 overflow-hidden py-20 sm:py-28">
      <div aria-hidden="true" className="pointer-events-none absolute inset-0 -z-10">
        <div className="absolute left-1/4 top-1/3 h-72 w-72 rounded-full bg-violet-600/10 blur-[120px]" />
        <div className="bg-grid grid-fade absolute inset-0 opacity-20" />
      </div>

      <div className="mx-auto max-w-6xl px-4 sm:px-6">
        <div className="grid grid-cols-1 gap-10 lg:grid-cols-[0.85fr_1.15fr] lg:gap-14">
          {/* Columna izquierda: encabezado + contacto */}
          <div className="lg:sticky lg:top-28 lg:self-start">
            <SectionHeading
              eyebrow={eyebrow}
              title={title}
              subtitle={subtitle}
              align="left"
              accentWord="preguntan"
            />

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.6, delay: 0.2 }}
              className="mt-7 rounded-3xl border border-white/10 bg-zinc-900/60 p-5 backdrop-blur-xl"
            >
              <div className="flex items-center gap-2.5">
                <span className="grid h-9 w-9 place-items-center rounded-xl bg-emerald-500/15 text-emerald-300">
                  <HelpCircle className="h-4 w-4" />
                </span>
                <div>
                  <p className="font-display text-[14px] font-semibold text-white">
                    ¿Otra duda?
                  </p>
                  <p className="text-[11px] text-zinc-500">Te respondo yo, no un bot.</p>
                </div>
              </div>

              <div className="mt-4 space-y-2">
                <motion.a
                  href={buildWhatsAppLink('Hola KODOX, tengo una duda sobre tus servicios.')}
                  target="_blank"
                  rel="noopener noreferrer"
                  whileHover={{ y: -2 }}
                  whileTap={{ scale: 0.97 }}
                  className="btn-whatsapp w-full py-3 text-[12.5px]"
                >
                  <MessageCircle className="h-4 w-4" />
                  Preguntar por WhatsApp
                </motion.a>

                <button
                  type="button"
                  onClick={() => onQuote?.()}
                  className="btn-ghost w-full py-3 text-[12.5px]"
                >
                  Abrir cotizador
                  <ArrowUpRight className="h-3.5 w-3.5" />
                </button>
              </div>

              <p className="mt-3 text-center font-mono text-[8.5px] uppercase tracking-[0.16em] text-zinc-600">
                {siteConfig.contact.schedule}
              </p>
            </motion.div>
          </div>

          {/* Columna derecha: acordeón */}
          <div className="space-y-2.5">
            {items.map((item, index) => (
              <FaqItem
                key={item.id}
                item={item}
                index={index}
                open={openId === item.id}
                onToggle={() => toggle(item.id)}
              />
            ))}
          </div>
        </div>
      </div>
    </section>
  );
}
