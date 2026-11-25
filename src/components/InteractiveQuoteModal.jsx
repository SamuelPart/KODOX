import { useEffect, useMemo, useState } from 'react';
import { AnimatePresence, motion, useReducedMotion } from 'framer-motion';
import {
  ArrowLeft,
  ArrowRight,
  Check,
  ChevronDown,
  MessageCircle,
  RefreshCw,
  Send,
  ShoppingCart,
  Smartphone,
  Sparkles,
  User,
  Wand2,
  X,
  Zap,
} from 'lucide-react';
import { siteConfig, buildWhatsAppLink } from '../data/portfolioData';

const NEED_ICONS = { Sparkles, RefreshCw, ShoppingCart, Smartphone, Wand2 };

const STEPS = [
  { id: 0, label: 'Negocio' },
  { id: 1, label: 'Necesidad' },
  { id: 2, label: 'Alcance' },
  { id: 3, label: 'Contacto' },
];

const EASE = [0.16, 1, 0.3, 1];

/* Selector de opciones tipo "pill" reutilizable */
function OptionPill({ active, onClick, children, icon: Icon }) {
  return (
    <motion.button
      type="button"
      onClick={onClick}
      whileHover={{ y: -2 }}
      whileTap={{ scale: 0.96 }}
      aria-pressed={active}
      className={`group relative flex items-center gap-2 rounded-xl border px-3 py-2.5 text-left text-[12px] font-medium transition-colors duration-200 ${
        active
          ? 'border-indigo-400/60 bg-indigo-500/15 text-white'
          : 'border-white/10 bg-white/[0.03] text-zinc-300 hover:border-indigo-400/40 hover:bg-white/[0.06]'
      }`}
    >
      {Icon && (
        <span
          className={`grid h-6 w-6 shrink-0 place-items-center rounded-lg ${
            active ? 'bg-indigo-500/30 text-indigo-200' : 'bg-white/[0.05] text-zinc-400'
          }`}
        >
          <Icon className="h-3.5 w-3.5" />
        </span>
      )}
      <span className="flex-1 leading-tight">{children}</span>
      {active && (
        <motion.span
          initial={{ scale: 0, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          className="grid h-4 w-4 shrink-0 place-items-center rounded-full bg-indigo-500"
        >
          <Check className="h-2.5 w-2.5 text-white" strokeWidth={3} />
        </motion.span>
      )}
    </motion.button>
  );
}

function Field({ label, children }) {
  return (
    <label className="block">
      <span className="mb-1.5 block font-mono text-[9px] uppercase tracking-[0.18em] text-zinc-500">
        {label}
      </span>
      {children}
    </label>
  );
}

const inputClass =
  'w-full rounded-xl border border-white/10 bg-white/[0.03] px-3.5 py-2.5 text-[13px] text-white placeholder:text-zinc-600 transition-colors duration-200 focus:border-indigo-400/60 focus:bg-white/[0.05] focus:outline-none';

export default function InteractiveQuoteModal({ open, onClose, preselect }) {
  const { quoteModal } = siteConfig;
  const reduce = useReducedMotion();

  const [step, setStep] = useState(0);
  const [form, setForm] = useState({
    businessType: '',
    selectedNeed: '',
    customNeed: '',
    budget: '',
    deadline: '',
    name: '',
    business: '',
    details: '',
  });
  const [sent, setSent] = useState(false);

  /* Precarga desde las tarjetas ("Quiero una así", planes, servicios) */
  useEffect(() => {
    if (!open) return;
    setSent(false);
    setStep(0);

    if (preselect) {
      const matchType = quoteModal.businessTypes.find(
        (type) =>
          preselect.businessType &&
          (type.toLowerCase().includes(preselect.businessType.toLowerCase().split('&')[0].trim()) ||
            preselect.businessType.toLowerCase().includes(type.toLowerCase().split(' ')[0]))
      );

      const matchNeed = quoteModal.needs.find(
        (need) =>
          preselect.need &&
          preselect.need.toLowerCase().includes(need.label.toLowerCase().split(' ')[0])
      );

      setForm((previous) => ({
        ...previous,
        businessType: preselect.businessType || previous.businessType,
        selectedNeed: matchNeed?.id || previous.selectedNeed,
        customNeed: preselect.need || previous.customNeed,
        details: preselect.plan ? `Me interesa el ${preselect.plan}.` : previous.details,
      }));

      if (matchType) setForm((p) => ({ ...p, businessType: matchType }));
      setStep(matchNeed ? 2 : 1);
    } else {
      setForm({
        businessType: '',
        selectedNeed: '',
        customNeed: '',
        budget: '',
        deadline: '',
        name: '',
        business: '',
        details: '',
      });
    }
  }, [open, preselect, quoteModal.businessTypes, quoteModal.needs]);

  /* Bloquea el scroll + cierra con Escape */
  useEffect(() => {
    if (!open) return undefined;

    const original = document.body.style.overflow;
    document.body.style.overflow = 'hidden';

    const onKeyDown = (event) => {
      if (event.key === 'Escape') onClose?.();
    };
    window.addEventListener('keydown', onKeyDown);

    return () => {
      document.body.style.overflow = original;
      window.removeEventListener('keydown', onKeyDown);
    };
  }, [open, onClose]);

  const selectedNeedLabel = useMemo(() => {
    const found = quoteModal.needs.find((need) => need.id === form.selectedNeed);
    if (form.customNeed) return form.customNeed;
    return found?.label || '';
  }, [form.selectedNeed, form.customNeed, quoteModal.needs]);

  const canContinue = useMemo(() => {
    if (step === 0) return Boolean(form.businessType);
    if (step === 1) return Boolean(form.selectedNeed || form.customNeed);
    if (step === 2) return Boolean(form.budget || form.deadline);
    return Boolean(form.name.trim());
  }, [step, form]);

  const message = useMemo(() => {
    const lines = [
      `*Nueva cotización — ${siteConfig.brand.name}*`,
      '',
      `*Negocio:* ${form.businessType || 'No especificado'}${
        form.business ? ` (${form.business})` : ''
      }`,
      `*Necesita:* ${selectedNeedLabel || 'No especificado'}`,
      `*Presupuesto:* ${form.budget || 'Por definir'}`,
      `*Tiempo:* ${form.deadline || 'Por definir'}`,
      `*Nombre:* ${form.name || 'Sin nombre'}`,
    ];

    if (form.details.trim()) {
      lines.push(`*Detalles:* ${form.details.trim()}`);
    }

    lines.push('', 'Enviado desde el cotizador de la web ✨');
    return lines.join('\n');
  }, [form, selectedNeedLabel]);

  const update = (key) => (value) => setForm((previous) => ({ ...previous, [key]: value }));

  const handleSubmit = (event) => {
    event.preventDefault();
    if (!canContinue) return;
    setSent(true);
    window.open(buildWhatsAppLink(message), '_blank', 'noopener,noreferrer');
  };

  const next = () => setStep((value) => Math.min(value + 1, STEPS.length - 1));
  const back = () => setStep((value) => Math.max(value - 1, 0));

  return (
    <AnimatePresence>
      {open && (
        <motion.div
          key="quote-modal"
          className="fixed inset-0 z-[90] flex items-end justify-center p-0 sm:items-center sm:p-5"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 0.25 }}
          role="dialog"
          aria-modal="true"
          aria-labelledby="quote-title"
        >
          {/* Fondo */}
          <motion.div
            className="absolute inset-0 bg-black/80 backdrop-blur-md"
            onClick={onClose}
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          />

          {/* Panel */}
          <motion.div
            initial={reduce ? { opacity: 0 } : { opacity: 0, y: 60, scale: 0.96, rotateX: 6 }}
            animate={{ opacity: 1, y: 0, scale: 1, rotateX: 0 }}
            exit={reduce ? { opacity: 0 } : { opacity: 0, y: 40, scale: 0.97 }}
            transition={{ duration: 0.45, ease: EASE }}
            className="relative flex max-h-[92vh] w-full max-w-lg flex-col overflow-hidden rounded-t-3xl border border-white/10 bg-zinc-950/95 shadow-[0_40px_120px_-40px_rgba(0,0,0,1)] backdrop-blur-2xl sm:rounded-3xl"
          >
            {/* Halos decorativos */}
            <div
              aria-hidden="true"
              className="pointer-events-none absolute -right-20 -top-24 h-56 w-56 rounded-full bg-indigo-600/25 blur-[80px]"
            />
            <div
              aria-hidden="true"
              className="pointer-events-none absolute -bottom-24 -left-20 h-56 w-56 rounded-full bg-cyan-500/15 blur-[80px]"
            />

            {/* Encabezado */}
            <div className="relative border-b border-white/[0.08] px-5 py-4">
              <div className="flex items-start justify-between gap-4">
                <div>
                  <span className="inline-flex items-center gap-1.5 font-mono text-[9px] uppercase tracking-[0.2em] text-indigo-300">
                    <Zap className="h-3 w-3" />
                    {quoteModal.eyebrow}
                  </span>
                  <h3
                    id="quote-title"
                    className="mt-1.5 font-display text-lg font-semibold leading-tight text-white"
                  >
                    {quoteModal.title}
                  </h3>
                  <p className="mt-1 max-w-[38ch] text-[11.5px] leading-relaxed text-zinc-500">
                    {quoteModal.subtitle}
                  </p>
                </div>

                <motion.button
                  type="button"
                  onClick={onClose}
                  aria-label="Cerrar cotizador"
                  whileHover={{ rotate: 90 }}
                  whileTap={{ scale: 0.9 }}
                  className="grid h-8 w-8 shrink-0 place-items-center rounded-full border border-white/10 bg-white/[0.04] text-zinc-400 transition-colors hover:text-white"
                >
                  <X className="h-4 w-4" />
                </motion.button>
              </div>

              {/* Progreso de pasos */}
              <div className="mt-4">
                <div className="flex items-center gap-1.5">
                  {STEPS.map((item) => (
                    <button
                      key={item.id}
                      type="button"
                      onClick={() => {
                        if (item.id <= step) setStep(item.id);
                      }}
                      className="group flex-1 text-left"
                      aria-label={`Ir al paso ${item.label}`}
                    >
                      <span className="relative block h-1 overflow-hidden rounded-full bg-white/10">
                        <motion.span
                          className="absolute inset-y-0 left-0 bg-gradient-to-r from-indigo-500 to-cyan-400"
                          initial={false}
                          animate={{ width: step >= item.id ? '100%' : '0%' }}
                          transition={{ duration: 0.4, ease: EASE }}
                        />
                      </span>
                      <span
                        className={`mt-1.5 block font-mono text-[8px] uppercase tracking-[0.12em] transition-colors ${
                          step >= item.id ? 'text-indigo-300' : 'text-zinc-600'
                        }`}
                      >
                        {item.label}
                      </span>
                    </button>
                  ))}
                </div>
              </div>
            </div>

            {/* Contenido */}
            <form onSubmit={handleSubmit} className="relative flex-1 overflow-y-auto">
              <div className="px-5 py-4">
                <AnimatePresence mode="wait" initial={false}>
                  {/* Paso 1: tipo de negocio */}
                  {step === 0 && (
                    <motion.div
                      key="step-0"
                      initial={{ opacity: 0, x: 24 }}
                      animate={{ opacity: 1, x: 0 }}
                      exit={{ opacity: 0, x: -24 }}
                      transition={{ duration: 0.3, ease: EASE }}
                    >
                      <p className="mb-3 font-display text-[13.5px] font-medium text-white">
                        ¿Qué tipo de negocio tienes?
                      </p>
                      <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
                        {quoteModal.businessTypes.map((type) => (
                          <OptionPill
                            key={type}
                            active={form.businessType === type}
                            onClick={() => {
                              update('businessType')(type);
                              setTimeout(next, 220);
                            }}
                          >
                            {type}
                          </OptionPill>
                        ))}
                      </div>
                    </motion.div>
                  )}

                  {/* Paso 2: necesidad */}
                  {step === 1 && (
                    <motion.div
                      key="step-1"
                      initial={{ opacity: 0, x: 24 }}
                      animate={{ opacity: 1, x: 0 }}
                      exit={{ opacity: 0, x: -24 }}
                      transition={{ duration: 0.3, ease: EASE }}
                    >
                      <p className="mb-3 font-display text-[13.5px] font-medium text-white">
                        ¿Qué necesitas exactamente?
                      </p>
                      <div className="grid grid-cols-1 gap-2">
                        {quoteModal.needs.map((need) => {
                          const Icon = NEED_ICONS[need.icon] || Sparkles;
                          return (
                            <OptionPill
                              key={need.id}
                              icon={Icon}
                              active={form.selectedNeed === need.id}
                              onClick={() => {
                                setForm((previous) => ({
                                  ...previous,
                                  selectedNeed: need.id,
                                  customNeed: '',
                                }));
                                setTimeout(next, 220);
                              }}
                            >
                              {need.label}
                            </OptionPill>
                          );
                        })}
                      </div>
                    </motion.div>
                  )}

                  {/* Paso 3: alcance */}
                  {step === 2 && (
                    <motion.div
                      key="step-2"
                      initial={{ opacity: 0, x: 24 }}
                      animate={{ opacity: 1, x: 0 }}
                      exit={{ opacity: 0, x: -24 }}
                      transition={{ duration: 0.3, ease: EASE }}
                      className="space-y-4"
                    >
                      <div>
                        <p className="mb-2 font-display text-[13.5px] font-medium text-white">
                          ¿Cuál es tu presupuesto aproximado?
                        </p>
                        <div className="grid grid-cols-2 gap-2">
                          {quoteModal.budgets.map((budget) => (
                            <OptionPill
                              key={budget.id}
                              active={form.budget === budget.label}
                              onClick={() => update('budget')(budget.label)}
                            >
                              {budget.label}
                            </OptionPill>
                          ))}
                        </div>
                      </div>

                      <div>
                        <p className="mb-2 font-display text-[13.5px] font-medium text-white">
                          ¿Para cuándo lo necesitas?
                        </p>
                        <div className="grid grid-cols-1 gap-2 sm:grid-cols-3">
                          {quoteModal.deadlines.map((deadline) => (
                            <OptionPill
                              key={deadline.id}
                              active={form.deadline === deadline.label}
                              onClick={() => {
                                update('deadline')(deadline.label);
                                setTimeout(next, 220);
                              }}
                            >
                              {deadline.label}
                            </OptionPill>
                          ))}
                        </div>
                      </div>

                      <div>
                        <Field label="Detalles (opcional)">
                          <textarea
                            rows={2}
                            value={form.details}
                            onChange={(event) => update('details')(event.target.value)}
                            placeholder={quoteModal.placeholders.details}
                            className={`${inputClass} resize-none`}
                          />
                        </Field>
                      </div>
                    </motion.div>
                  )}

                  {/* Paso 4: contacto */}
                  {step === 3 && (
                    <motion.div
                      key="step-3"
                      initial={{ opacity: 0, x: 24 }}
                      animate={{ opacity: 1, x: 0 }}
                      exit={{ opacity: 0, x: -24 }}
                      transition={{ duration: 0.3, ease: EASE }}
                      className="space-y-3"
                    >
                      <Field label="Tu nombre *">
                        <div className="relative">
                          <User className="pointer-events-none absolute right-3 top-1/2 h-3.5 w-3.5 -translate-y-1/2 text-zinc-600" />
                          <input
                            type="text"
                            required
                            autoFocus
                            value={form.name}
                            onChange={(event) => update('name')(event.target.value)}
                            placeholder={quoteModal.placeholders.name}
                            className={inputClass}
                          />
                        </div>
                      </Field>

                      <Field label="Nombre de tu negocio (opcional)">
                        <input
                          type="text"
                          value={form.business}
                          onChange={(event) => update('business')(event.target.value)}
                          placeholder={quoteModal.placeholders.business}
                          className={inputClass}
                        />
                      </Field>

                      {/* Resumen estructurado */}
                      <div className="rounded-2xl border border-white/[0.08] bg-white/[0.02] p-3">
                        <p className="font-mono text-[8.5px] uppercase tracking-[0.18em] text-zinc-500">
                          Resumen de tu consulta
                        </p>
                        <dl className="mt-2 space-y-1.5 text-[11.5px]">
                          {[
                            ['Negocio', form.businessType || '—'],
                            ['Necesita', selectedNeedLabel || '—'],
                            ['Presupuesto', form.budget || '—'],
                            ['Tiempo', form.deadline || '—'],
                          ].map(([label, value]) => (
                            <div key={label} className="flex items-start justify-between gap-3">
                              <dt className="shrink-0 font-mono text-[9px] uppercase tracking-[0.1em] text-zinc-600">
                                {label}
                              </dt>
                              <dd className="text-right text-zinc-300">{value}</dd>
                            </div>
                          ))}
                        </dl>
                      </div>

                      <p className="flex items-center gap-1.5 text-[10.5px] text-zinc-500">
                        <ChevronDown className="h-3 w-3 text-emerald-400" />
                        {quoteModal.successNote}
                      </p>
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>

              {/* Footer */}
              <div className="sticky bottom-0 border-t border-white/[0.08] bg-zinc-950/90 px-5 py-3.5 backdrop-blur-xl">
                <div className="flex items-center gap-2">
                  {step > 0 && (
                    <motion.button
                      type="button"
                      onClick={back}
                      whileTap={{ scale: 0.95 }}
                      className="inline-flex items-center gap-1.5 rounded-xl border border-white/10 bg-white/[0.03] px-3 py-2.5 text-[12px] font-medium text-zinc-300 transition-colors hover:text-white"
                    >
                      <ArrowLeft className="h-3.5 w-3.5" />
                      Atrás
                    </motion.button>
                  )}

                  {step < STEPS.length - 1 ? (
                    <motion.button
                      type="button"
                      onClick={next}
                      disabled={!canContinue}
                      whileHover={canContinue ? { y: -2 } : {}}
                      whileTap={canContinue ? { scale: 0.97 } : {}}
                      className="flex flex-1 items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-indigo-500 via-purple-500 to-cyan-400 px-4 py-2.5 text-[12.5px] font-semibold text-white shadow-[0_10px_30px_-14px_rgba(99,102,241,0.95)] disabled:opacity-40"
                    >
                      Continuar
                      <ArrowRight className="h-3.5 w-3.5" />
                    </motion.button>
                  ) : (
                    <motion.button
                      type="submit"
                      disabled={!canContinue}
                      whileHover={canContinue ? { y: -2 } : {}}
                      whileTap={canContinue ? { scale: 0.97 } : {}}
                      className="flex flex-1 items-center justify-center gap-2 rounded-xl bg-[#16A34A] px-4 py-2.5 text-[12.5px] font-semibold text-white shadow-glow-wa disabled:opacity-40"
                    >
                      {sent ? (
                        <>
                          <Check className="h-4 w-4" />
                          ¡Abriendo WhatsApp!
                        </>
                      ) : (
                        <>
                          <Send className="h-3.5 w-3.5" />
                          {quoteModal.submitLabel}
                        </>
                      )}
                    </motion.button>
                  )}
                </div>

                <p className="mt-2 text-center font-mono text-[8.5px] uppercase tracking-[0.14em] text-zinc-600">
                  <MessageCircle className="mr-1 inline h-2.5 w-2.5 text-emerald-500" />
                  Respuesta promedio: 15 min · {siteConfig.contact.whatsappDisplay}
                </p>
              </div>
            </form>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
