import { useRef } from 'react';
import {
  motion,
  useMotionValue,
  useReducedMotion,
  useScroll,
  useSpring,
  useTransform,
} from 'framer-motion';
import {
  ArrowDownRight,
  ArrowRight,
  BadgeCheck,
  CheckCircle2,
  Clock3,
  MessageCircle,
  Play,
  Sparkles,
  Star,
  TrendingUp,
  Wallet,
  Zap,
} from 'lucide-react';
import { siteConfig, buildWhatsAppLink } from '../data/portfolioData';

/* ------------------------------------------------------------------ Helpers */

const EASE = [0.16, 1, 0.3, 1];

/** Titular que se revela palabra por palabra con máscara. */
function MaskedHeadline({ lines }) {
  const reduce = useReducedMotion();
  let counter = 0;

  return (
    <h1 className="font-display text-[2.35rem] font-bold leading-[0.98] tracking-[-0.03em] text-white sm:text-6xl lg:text-[4.35rem]">
      {lines.map((line, lineIndex) => (
        <span key={`line-${lineIndex}`} className="block">
          {line.map((word) => {
            const delay = 0.35 + counter * 0.075;
            counter += 1;
            const highlight = /^(máquinas|ventas|digitales\.?)$/i.test(word);

            return (
              <span
                key={`${word}-${counter}`}
                className="inline-block overflow-hidden pb-[0.09em] align-bottom"
              >
                <motion.span
                  className={`inline-block ${
                    highlight
                      ? 'bg-gradient-to-r from-indigo-400 via-purple-400 to-cyan-300 bg-clip-text text-transparent'
                      : ''
                  }`}
                  initial={reduce ? false : { y: '115%', rotate: 4 }}
                  animate={{ y: '0%', rotate: 0 }}
                  transition={{ duration: 1.05, delay, ease: EASE }}
                >
                  {word}
                  {'\u00A0'}
                </motion.span>
              </span>
            );
          })}
        </span>
      ))}
    </h1>
  );
}

/** Tarjeta 3D simulada con pedidos entrando en vivo. */
function PhoneMockup() {
  const data = siteConfig.hero.phoneMockup;
  const reduce = useReducedMotion();
  const cardRef = useRef(null);

  const rotateX = useMotionValue(0);
  const rotateY = useMotionValue(0);
  const springX = useSpring(rotateX, { stiffness: 140, damping: 18 });
  const springY = useSpring(rotateY, { stiffness: 140, damping: 18 });

  const handlePointerMove = (event) => {
    if (reduce || !cardRef.current) return;
    const rect = cardRef.current.getBoundingClientRect();
    const px = (event.clientX - rect.left) / rect.width - 0.5;
    const py = (event.clientY - rect.top) / rect.height - 0.5;
    rotateY.set(px * 16);
    rotateX.set(py * -14);
  };

  const handlePointerLeave = () => {
    rotateX.set(0);
    rotateY.set(0);
  };

  return (
    <div
      ref={cardRef}
      onPointerMove={handlePointerMove}
      onPointerLeave={handlePointerLeave}
      className="perspective-1000 relative mx-auto w-full max-w-[360px]"
    >
      {/* Halos detrás del celular */}
      <div
        aria-hidden="true"
        className="absolute -inset-10 -z-10 rounded-[3rem] bg-gradient-to-br from-indigo-600/30 via-purple-600/15 to-cyan-500/25 blur-[70px]"
      />

      {/* Anillo orbital */}
      <motion.div
        aria-hidden="true"
        className="absolute -inset-6 -z-10 rounded-[3rem] border border-dashed border-white/10"
        animate={reduce ? {} : { rotate: 360 }}
        transition={{ duration: 40, repeat: Infinity, ease: 'linear' }}
      />

      <motion.div
        style={{ rotateX: springX, rotateY: springY, transformStyle: 'preserve-3d' }}
        initial={reduce ? false : { opacity: 0, y: 60, scale: 0.94 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        transition={{ duration: 1.1, delay: 0.45, ease: EASE }}
        className="preserve-3d relative"
      >
        <motion.div
          animate={reduce ? {} : { y: [0, -12, 0] }}
          transition={{ duration: 7, repeat: Infinity, ease: 'easeInOut' }}
          className="phone-frame relative overflow-hidden rounded-[2.6rem] p-[3px]"
          style={{ transform: 'translateZ(30px)' }}
        >
          {/* Pantalla */}
          <div className="relative overflow-hidden rounded-[2.45rem] bg-[#0A0A10]">
            {/* Notch */}
            <div className="absolute left-1/2 top-2 z-20 h-[22px] w-[92px] -translate-x-1/2 rounded-full bg-black/90 ring-1 ring-white/5">
              <div className="absolute right-3 top-1/2 h-1.5 w-1.5 -translate-y-1/2 rounded-full bg-cyan-400/60" />
            </div>

            <div className="bg-grid-sm relative px-3.5 pb-4 pt-9">
              {/* Barra de estado */}
              <div className="mb-3 flex items-center justify-between font-mono text-[8.5px] text-zinc-500">
                <span>9:41</span>
                <span className="flex items-center gap-1">
                  <span className="h-1 w-1 rounded-full bg-emerald-400" /> 5G
                  <span className="ml-1 inline-block h-2 w-4 rounded-sm border border-zinc-600">
                    <span className="block h-full w-3/4 bg-emerald-400/80" />
                  </span>
                </span>
              </div>

              {/* Encabezado de la app */}
              <div className="mb-3 flex items-center justify-between">
                <div>
                  <p className="font-mono text-[8px] uppercase tracking-[0.2em] text-indigo-400">
                    {data.appName}
                  </p>
                  <p className="mt-0.5 font-display text-[15px] font-semibold text-white">
                    {data.storeName}
                  </p>
                </div>
                <span className="flex items-center gap-1 rounded-full border border-emerald-500/30 bg-emerald-500/10 px-2 py-1 font-mono text-[7.5px] uppercase tracking-wider text-emerald-300">
                  <span className="relative flex h-1.5 w-1.5">
                    <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-emerald-400 opacity-75" />
                    <span className="relative inline-flex h-1.5 w-1.5 rounded-full bg-emerald-400" />
                  </span>
                  Live
                </span>
              </div>

              {/* KPIs */}
              <div className="mb-3 grid grid-cols-2 gap-2">
                <motion.div
                  initial={reduce ? false : { opacity: 0, y: 12 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.9, duration: 0.6, ease: EASE }}
                  className="rounded-xl border border-white/10 bg-white/[0.04] p-2.5"
                >
                  <div className="flex items-center gap-1 text-[8px] uppercase tracking-wider text-zinc-500">
                    <TrendingUp className="h-2.5 w-2.5 text-emerald-400" /> Hoy
                  </div>
                  <p className="mt-1 font-display text-[17px] font-bold text-white">
                    {data.totalToday}
                  </p>
                  <p className="font-mono text-[7.5px] text-emerald-400">+24% vs ayer</p>
                </motion.div>

                <motion.div
                  initial={reduce ? false : { opacity: 0, y: 12 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 1.05, duration: 0.6, ease: EASE }}
                  className="rounded-xl border border-white/10 bg-white/[0.04] p-2.5"
                >
                  <div className="flex items-center gap-1 text-[8px] uppercase tracking-wider text-zinc-500">
                    <Wallet className="h-2.5 w-2.5 text-cyan-400" /> Pedidos
                  </div>
                  <p className="mt-1 font-display text-[17px] font-bold text-white">
                    {data.ordersToday}
                  </p>
                  <div className="mt-1 flex h-4 items-end gap-[2px]">
                    {data.chart.map((value, index) => (
                      <motion.span
                        key={index}
                        initial={{ height: 2 }}
                        animate={{ height: `${Math.round(value * 0.16)}px` }}
                        transition={{ delay: 1.2 + index * 0.055, duration: 0.4 }}
                        className="w-full rounded-full bg-gradient-to-t from-indigo-500/40 to-cyan-400"
                      />
                    ))}
                  </div>
                </motion.div>
              </div>

              {/* Notificaciones */}
              <div className="space-y-1.5">
                {data.notifications.map((notification, index) => (
                  <motion.div
                    key={notification.title + index}
                    initial={reduce ? false : { opacity: 0, x: 34, filter: 'blur(4px)' }}
                    animate={{
                      opacity: 1,
                      x: 0,
                      filter: 'blur(0px)',
                      transition: { delay: 1.15 + index * 0.28, duration: 0.6, ease: EASE },
                    }}
                    className="relative overflow-hidden rounded-xl border border-white/10 bg-zinc-900/85 p-2.5 backdrop-blur-md"
                  >
                    <div className="flex items-start gap-2">
                      <span
                        className={`mt-0.5 grid h-6 w-6 shrink-0 place-items-center rounded-lg text-[9px] font-bold text-white ${
                          notification.icon === 'yape'
                            ? 'bg-gradient-to-br from-violet-500 to-fuchsia-600'
                            : 'bg-[#16A34A]'
                        }`}
                      >
                        {notification.icon === 'yape' ? 'Y' : <MessageCircle className="h-3 w-3" />}
                      </span>

                      <div className="min-w-0 flex-1">
                        <div className="flex items-center justify-between gap-1">
                          <p className="truncate text-[9.5px] font-semibold text-white">
                            {notification.title}
                          </p>
                          <span className="shrink-0 font-mono text-[6.5px] uppercase text-zinc-500">
                            {notification.time}
                          </span>
                        </div>
                        <p className="truncate text-[8px] text-zinc-400">
                          {notification.detail}
                        </p>
                        <div className="mt-1 flex items-center justify-between">
                          <span className="font-display text-[11px] font-bold text-white">
                            {notification.amount}
                          </span>
                          <span
                            className={`rounded-md px-1.5 py-[1px] font-mono text-[6.5px] uppercase tracking-wider ${
                              notification.method === 'Yape'
                                ? 'bg-violet-500/15 text-violet-300'
                                : 'bg-zinc-700/50 text-zinc-300'
                            }`}
                          >
                            {notification.method}
                          </span>
                        </div>
                      </div>
                    </div>
                    <span className="pointer-events-none absolute inset-0 -translate-x-full animate-shimmer bg-gradient-to-r from-transparent via-white/[0.07] to-transparent" />
                  </motion.div>
                ))}
              </div>

              {/* Footer app */}
              <div className="mt-3 flex items-center justify-between rounded-xl border border-white/10 bg-gradient-to-r from-indigo-600/25 to-cyan-500/15 px-2.5 py-2">
                <div className="flex items-center gap-1.5">
                  <Zap className="h-3 w-3 text-cyan-300" />
                  <span className="text-[8.5px] font-medium text-white">Pedidos a WhatsApp</span>
                </div>
                <CheckCircle2 className="h-3 w-3 text-emerald-400" />
              </div>
            </div>
          </div>
        </motion.div>

        {/* Tarjeta flotante: pago */}
        <motion.div
          initial={reduce ? false : { opacity: 0, x: -40, y: 20 }}
          animate={{ opacity: 1, x: 0, y: 0 }}
          transition={{ delay: 1.5, duration: 0.8, ease: EASE }}
          style={{ transform: 'translateZ(70px)' }}
          className="absolute -left-6 bottom-24 hidden sm:block"
        >
          <motion.div
            animate={reduce ? {} : { y: [0, -9, 0] }}
            transition={{ duration: 5.5, repeat: Infinity, ease: 'easeInOut', delay: 0.4 }}
            className="flex items-center gap-2.5 rounded-2xl border border-white/10 bg-zinc-900/90 px-3 py-2.5 shadow-2xl backdrop-blur-xl"
          >
            <span className="grid h-8 w-8 place-items-center rounded-xl bg-[#16A34A]/15 text-[#22C55E]">
              <MessageCircle className="h-4 w-4" />
            </span>
            <div>
              <p className="font-mono text-[7.5px] uppercase tracking-[0.15em] text-zinc-500">
                WhatsApp
              </p>
              <p className="text-[11px] font-semibold text-white">Pedido confirmado</p>
            </div>
          </motion.div>
        </motion.div>

        {/* Tarjeta flotante: velocidad */}
        <motion.div
          initial={reduce ? false : { opacity: 0, x: 40, y: -20 }}
          animate={{ opacity: 1, x: 0, y: 0 }}
          transition={{ delay: 1.65, duration: 0.8, ease: EASE }}
          style={{ transform: 'translateZ(60px)' }}
          className="absolute -right-5 top-28 hidden sm:block"
        >
          <motion.div
            animate={reduce ? {} : { y: [0, 10, 0] }}
            transition={{ duration: 6.2, repeat: Infinity, ease: 'easeInOut', delay: 1 }}
            className="rounded-2xl border border-white/10 bg-zinc-900/90 px-3 py-2.5 shadow-2xl backdrop-blur-xl"
          >
            <p className="font-mono text-[7.5px] uppercase tracking-[0.15em] text-zinc-500">
              Lighthouse
            </p>
            <div className="mt-1 flex items-center gap-1.5">
              <span className="font-display text-lg font-bold text-white">99</span>
              <span className="rounded-md bg-emerald-500/15 px-1.5 py-[1px] font-mono text-[7px] text-emerald-300">
                A+
              </span>
            </div>
            <div className="mt-1 h-1 w-16 overflow-hidden rounded-full bg-white/10">
              <motion.div
                initial={{ width: 0 }}
                animate={{ width: '99%' }}
                transition={{ delay: 2, duration: 1.2, ease: EASE }}
                className="h-full rounded-full bg-gradient-to-r from-emerald-400 to-cyan-400"
              />
            </div>
          </motion.div>
        </motion.div>
      </motion.div>

      {/* Sombra en el piso */}
      <div
        aria-hidden="true"
        className="mx-auto mt-6 h-6 w-3/4 rounded-full bg-indigo-500/25 blur-2xl"
      />
    </div>
  );
}

/* ------------------------------------------------------------------- Hero */

export default function HeroSection() {
  const reduce = useReducedMotion();
  const sectionRef = useRef(null);

  const { scrollYProgress } = useScroll({
    target: sectionRef,
    offset: ['start start', 'end start'],
  });

  const yText = useTransform(scrollYProgress, [0, 1], [0, -90]);
  const yVisual = useTransform(scrollYProgress, [0, 1], [0, 120]);
  const opacityHero = useTransform(scrollYProgress, [0, 0.75], [1, 0]);
  const scaleVisual = useTransform(scrollYProgress, [0, 1], [1, 0.92]);

  const stats = siteConfig.hero.stats;

  return (
    <section
      id="inicio"
      ref={sectionRef}
      className="relative overflow-hidden pb-16 pt-28 sm:pb-24 sm:pt-32 lg:pb-28 lg:pt-36"
    >
      {/* Fondo */}
      <div aria-hidden="true" className="pointer-events-none absolute inset-0 -z-10">
        <div className="bg-grid grid-fade absolute inset-0 opacity-[0.55]" />
        <div className="absolute left-1/2 top-0 h-[520px] w-[820px] -translate-x-1/2 -translate-y-1/3 rounded-full bg-indigo-600/20 blur-[130px]" />

        <motion.div
          animate={reduce ? {} : { y: [0, -28, 0], scale: [1, 1.06, 1] }}
          transition={{ duration: 11, repeat: Infinity, ease: 'easeInOut' }}
          className="absolute -left-24 top-24 h-72 w-72 rounded-full bg-indigo-600/30 blur-[100px]"
        />
        <motion.div
          animate={reduce ? {} : { y: [0, 24, 0], scale: [1, 1.08, 1] }}
          transition={{ duration: 13, repeat: Infinity, ease: 'easeInOut', delay: 1 }}
          className="absolute -right-20 top-56 h-80 w-80 rounded-full bg-cyan-500/20 blur-[110px]"
        />
        <motion.div
          animate={reduce ? {} : { y: [0, -18, 0], scale: [1, 1.05, 1] }}
          transition={{ duration: 9, repeat: Infinity, ease: 'easeInOut', delay: 0.5 }}
          className="absolute bottom-10 left-1/3 h-64 w-64 rounded-full bg-violet-600/20 blur-[120px]"
        />
        <div className="absolute inset-x-0 bottom-0 h-40 bg-gradient-to-b from-transparent to-obsidian" />
      </div>

      <motion.div
        style={reduce ? {} : { opacity: opacityHero }}
        className="relative mx-auto grid max-w-6xl grid-cols-1 items-center gap-14 px-4 sm:px-6 lg:grid-cols-[1.08fr_1fr] lg:gap-8"
      >
        {/* ---------- Columna de texto ---------- */}
        <motion.div style={reduce ? {} : { y: yText }} className="relative">
          {/* Badge de disponibilidad */}
          <motion.div
            initial={reduce ? false : { opacity: 0, y: 14 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.7, delay: 0.15, ease: EASE }}
            className="inline-flex items-center gap-2.5 rounded-full border border-emerald-500/25 bg-emerald-500/[0.07] px-3.5 py-1.5 backdrop-blur-md"
          >
            <span className="relative flex h-2 w-2">
              <span className="absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-80 animate-pulse-ring" />
              <span className="relative inline-flex h-2 w-2 rounded-full bg-emerald-400 shadow-[0_0_10px_3px_rgba(52,211,153,0.55)]" />
            </span>
            <span className="font-mono text-[9.5px] uppercase leading-tight tracking-[0.16em] text-emerald-200/90 sm:text-[10px]">
              {siteConfig.hero.badge}
            </span>
          </motion.div>

          <div className="mt-6">
            <MaskedHeadline lines={siteConfig.hero.titleLines} />
          </div>

          <motion.p
            initial={reduce ? false : { opacity: 0, y: 18 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 1.05, ease: EASE }}
            className="mt-6 max-w-xl text-[15px] leading-relaxed text-zinc-400 sm:text-[16.5px]"
          >
            {siteConfig.hero.subtitle}
          </motion.p>

          {/* CTAs */}
          <motion.div
            initial={reduce ? false : { opacity: 0, y: 18 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 1.2, ease: EASE }}
            className="mt-8 flex flex-col gap-3 sm:flex-row sm:items-center"
          >
            <motion.a
              href="#proyectos"
              onClick={(event) => {
                event.preventDefault();
                const target = document.querySelector('#proyectos');
                if (target) {
                  window.scrollTo({
                    top: target.getBoundingClientRect().top + window.scrollY - 88,
                    behavior: 'smooth',
                  });
                }
              }}
              whileHover={{ y: -3 }}
              whileTap={{ scale: 0.97 }}
              className="btn-primary group px-6 py-4 text-[14px] sm:px-7"
            >
              <Play className="h-4 w-4 fill-white/20" />
              {siteConfig.ctaLabels.heroPrimary}
              <ArrowDownRight className="h-4 w-4 transition-transform duration-300 group-hover:translate-x-1 group-hover:translate-y-1" />
            </motion.a>

            <motion.a
              href={buildWhatsAppLink()}
              target="_blank"
              rel="noopener noreferrer"
              whileHover={{ y: -3 }}
              whileTap={{ scale: 0.97 }}
              className="btn-whatsapp group px-6 py-4 text-[14px] sm:px-7"
            >
              <MessageCircle className="h-4 w-4" />
              {siteConfig.ctaLabels.heroSecondary}
              <ArrowRight className="h-4 w-4 transition-transform duration-300 group-hover:translate-x-1" />
            </motion.a>
          </motion.div>

          <motion.div
            initial={reduce ? false : { opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.9, delay: 1.4 }}
            className="mt-5 flex flex-wrap items-center gap-x-4 gap-y-2 font-mono text-[10px] uppercase tracking-[0.12em] text-zinc-500"
          >
            <span className="inline-flex items-center gap-1.5">
              <BadgeCheck className="h-3.5 w-3.5 text-indigo-400" /> Pago único
            </span>
            <span className="inline-flex items-center gap-1.5">
              <Clock3 className="h-3.5 w-3.5 text-cyan-400" /> Entrega 24–72 h
            </span>
            <span className="inline-flex items-center gap-1.5">
              <Star className="h-3.5 w-3.5 fill-amber-400 text-amber-400" /> 5.0 en reseñas
            </span>
          </motion.div>

          {/* Stats */}
          <motion.dl
            initial={reduce ? false : { opacity: 0, y: 22 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 1.55, ease: EASE }}
            className="mt-9 grid grid-cols-2 gap-px overflow-hidden rounded-2xl border border-white/10 bg-white/[0.02] sm:grid-cols-4"
          >
            {stats.map((stat) => (
              <div
                key={stat.label}
                className="group relative bg-obsidian/40 px-3.5 py-3.5 transition-colors duration-300 hover:bg-white/[0.03]"
              >
                <dt className="font-mono text-[8.5px] uppercase leading-tight tracking-[0.14em] text-zinc-500">
                  {stat.label}
                </dt>
                <dd className="mt-1 font-display text-lg font-bold text-white transition-colors group-hover:text-cyan-300 sm:text-xl">
                  {stat.value}
                </dd>
              </div>
            ))}
          </motion.dl>
        </motion.div>

        {/* ---------- Columna visual ---------- */}
        <motion.div style={reduce ? {} : { y: yVisual, scale: scaleVisual }} className="relative">
          <PhoneMockup />

          <motion.div
            initial={reduce ? false : { opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 1.9, duration: 0.8 }}
            className="mt-6 flex items-center justify-center gap-2 font-mono text-[9px] uppercase tracking-[0.18em] text-zinc-500"
          >
            <Sparkles className="h-3 w-3 text-indigo-400" />
            Demo interactiva · muévete sobre el celular
          </motion.div>
        </motion.div>
      </motion.div>

      {/* Cinta inferior de confianza */}
      <motion.div
        initial={{ opacity: 0 }}
        whileInView={{ opacity: 1 }}
        viewport={{ once: true }}
        transition={{ duration: 0.8, delay: 0.2 }}
        className="relative mx-auto mt-14 max-w-6xl px-4 sm:px-6"
      >
        <div className="divider-glow" />
      </motion.div>
    </section>
  );
}
