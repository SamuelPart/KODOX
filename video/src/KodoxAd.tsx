import React from "react";
import {
  AbsoluteFill,
  Audio,
  Sequence,
  interpolate,
  spring,
  staticFile,
  useCurrentFrame,
  useVideoConfig,
} from "remotion";

export const KODOX_AD_DURATION = 1800; // 60s @ 30fps

const C = {
  bg: "#050507",
  card: "rgba(16,16,24,0.82)",
  white: "#FFFFFF",
  muted: "#A1A1AA",
  dim: "#71717A",
  indigo: "#6366F1",
  violet: "#8B5CF6",
  cyan: "#22D3EE",
  rose: "#FB7185",
  amber: "#FBBF24",
  green: "#34D399",
  red: "#F87171",
};
const font = '"Inter","Segoe UI","Helvetica Neue",Arial,sans-serif';
const clamp = { extrapolateLeft: "clamp" as const, extrapolateRight: "clamp" as const };

/* ─── Helpers ─── */
const sp = (
  frame: number,
  fps: number,
  delay: number,
  cfg: { mass?: number; damping?: number; stiffness?: number; overshootClamping?: boolean } = { damping: 200 }
) => spring({ frame: frame - delay, fps, config: cfg });
const fade = (frame: number, start: number, end: number) =>
  interpolate(frame, [start, start + 8], [0, 1], clamp) *
  interpolate(frame, [end - 10, end], [1, 0], clamp);
const vis = (frame: number, start: number, end: number) => frame >= start - 2 && frame <= end + 2;

/* ─── Background ─── */
const BG: React.FC<{ frame: number; mood?: "chaos" | "calm" | "neutral" }> = ({ frame, mood = "neutral" }) => {
  const d = interpolate(frame, [0, 1800], [0, 80]);
  const a = mood === "chaos" ? 0.22 : mood === "calm" ? 0.18 : 0.2;
  const b = mood === "chaos" ? 0.18 : mood === "calm" ? 0.22 : 0.16;
  return (
    <AbsoluteFill style={{ backgroundColor: C.bg }}>
      <div style={{ position: "absolute", inset: 0, backgroundImage: `
        radial-gradient(ellipse 65% 45% at ${22 + d * 0.15}% ${18 + d * 0.08}%, rgba(99,102,241,${a}), transparent 62%),
        radial-gradient(ellipse 55% 40% at ${78 - d * 0.12}% ${22 + d * 0.06}%, rgba(34,211,238,${b}), transparent 60%),
        radial-gradient(ellipse 75% 55% at 50% ${85 - d * 0.1}%, rgba(139,92,246,0.14), transparent 68%)
      ` }} />
      <div style={{ position: "absolute", inset: 0, opacity: 0.35, backgroundImage:
        "linear-gradient(to right, rgba(255,255,255,0.04) 1px, transparent 1px), linear-gradient(to bottom, rgba(255,255,255,0.04) 1px, transparent 1px)",
        backgroundSize: "56px 56px" }} />
    </AbsoluteFill>
  );
};

/* ─── Punch subtitle ─── */
const Sub: React.FC<{ text: string; frame: number; fps: number; enter: number; exit: number; isV: boolean; big?: boolean }> = ({
  text, frame, fps, enter, exit, isV, big,
}) => {
  if (frame < enter - 2 || frame > exit + 2) return null;
  const p = sp(frame, fps, enter, { damping: 18, stiffness: 180 });
  const o = interpolate(p, [0, 1], [0, 1]) * interpolate(frame, [exit - 8, exit], [1, 0], clamp);
  const y = interpolate(p, [0, 1], [30, 0]);
  const sc = interpolate(p, [0, 1], [0.92, 1]);
  return (
    <div style={{ position: "absolute", left: isV ? 48 : 120, right: isV ? 48 : 120, bottom: isV ? 140 : 80, display: "flex", justifyContent: "center", opacity: o, transform: `translateY(${y}px) scale(${sc})` }}>
      <div style={{
        fontFamily: font, fontSize: big ? (isV ? 52 : 44) : (isV ? 40 : 34), fontWeight: 800,
        letterSpacing: "0.01em", lineHeight: 1.2, color: C.white, textAlign: "center",
        textShadow: "0 6px 40px rgba(0,0,0,0.95)",
        background: "rgba(5,5,7,0.55)", border: "1px solid rgba(255,255,255,0.08)",
        backdropFilter: "blur(8px)", padding: isV ? "16px 26px" : "12px 22px", borderRadius: 16,
      }}>{text}</div>
    </div>
  );
};

/* ─── Big center word ─── */
const Big: React.FC<{ text: string; frame: number; fps: number; delay: number; size: number; color?: string; gradient?: boolean; y?: number }> = ({
  text, frame, fps, delay, size, color = C.white, gradient, y: oy = 0,
}) => {
  const p = sp(frame, fps, delay, { damping: 14, stiffness: 170, mass: 0.7 });
  return (
    <div style={{
      fontFamily: font, fontSize: size, fontWeight: 900, letterSpacing: "-0.045em", lineHeight: 0.95,
      color: gradient ? "transparent" : color,
      background: gradient ? "linear-gradient(100deg,#818cf8 0%,#a78bfa 40%,#22d3ee 100%)" : undefined,
      WebkitBackgroundClip: gradient ? "text" : undefined,
      opacity: interpolate(p, [0, 1], [0, 1]),
      transform: `translateY(${interpolate(p, [0, 1], [60 + oy, oy])}px) scale(${interpolate(p, [0, 1], [0.7, 1])})`,
      textAlign: "center",
    }}>{text}</div>
  );
};

/* ─── Visual primitives ─── */
const Phone: React.FC<{ frame: number; fps: number; delay: number; x: number; y: number; w: number; h: number; children?: React.ReactNode }> = ({
  frame, fps, delay, x, y, w, h, children,
}) => {
  const p = sp(frame, fps, delay, { damping: 16, stiffness: 140 });
  return (
    <div style={{
      position: "absolute", left: x, top: y, width: w, height: h,
      borderRadius: w * 0.14, border: "2px solid rgba(255,255,255,0.14)",
      background: "linear-gradient(180deg,#18181f,#0a0a0f)",
      boxShadow: "0 40px 100px -30px rgba(0,0,0,0.9), 0 0 0 1px rgba(255,255,255,0.05)",
      overflow: "hidden", opacity: interpolate(p, [0, 1], [0, 1]),
      transform: `translateY(${interpolate(p, [0, 1], [80, 0])}px) scale(${interpolate(p, [0, 1], [0.9, 1])})`,
    }}>
      <div style={{ position: "absolute", top: 12, left: "50%", transform: "translateX(-50%)", width: w * 0.28, height: 6, borderRadius: 99, background: "rgba(255,255,255,0.12)" }} />
      <div style={{ position: "absolute", inset: 28 }}>{children}</div>
    </div>
  );
};

const Bubble: React.FC<{ text: string; color: string; frame: number; fps: number; delay: number; x: number; y: number; w: number; float?: number }> = ({
  text, color, frame, fps, delay, x, y, w, float = 0,
}) => {
  const p = sp(frame, fps, delay, { damping: 14, stiffness: 160 });
  const fy = Math.sin((frame + delay) * 0.08) * float;
  return (
    <div style={{
      position: "absolute", left: x, top: y + fy, width: w, padding: "14px 16px",
      borderRadius: 18, background: `${color}22`, border: `1px solid ${color}55`,
      fontFamily: font, fontSize: 22, fontWeight: 600, color: C.white,
      opacity: interpolate(p, [0, 1], [0, 1]),
      transform: `scale(${interpolate(p, [0, 1], [0.6, 1])})`,
    }}>{text}</div>
  );
};

const Notif: React.FC<{ label: string; count: string; frame: number; fps: number; delay: number; y: number; gone?: boolean; goneAt?: number }> = ({
  label, count, frame, fps, delay, y, gone, goneAt = 9999,
}) => {
  const p = sp(frame, fps, delay, { damping: 18, stiffness: 160 });
  const g = gone ? interpolate(frame, [goneAt, goneAt + 15], [0, 1], clamp) : 0;
  return (
    <div style={{
      position: "absolute", left: 20, right: 20, top: y,
      padding: "14px 16px", borderRadius: 16,
      background: "rgba(255,255,255,0.06)", border: "1px solid rgba(255,255,255,0.1)",
      display: "flex", justifyContent: "space-between", alignItems: "center",
      opacity: interpolate(p, [0, 1], [0, 1]) * (1 - g),
      transform: `translateY(${interpolate(p, [0, 1], [30, 0]) + g * 60}px) scale(${1 - g * 0.15})`,
    }}>
      <span style={{ fontFamily: font, fontSize: 20, color: C.white, fontWeight: 500 }}>{label}</span>
      <span style={{ fontFamily: font, fontSize: 16, color: C.rose, fontWeight: 700, background: "rgba(251,113,133,0.15)", padding: "4px 10px", borderRadius: 99 }}>{count}</span>
    </div>
  );
};

const Ring: React.FC<{ size: number; color: string; frame: number; delay: number; x: number; y: number; label?: string }> = ({
  size, color, frame, delay, x, y, label,
}) => {
  const p = interpolate(frame, [delay, delay + 20], [0, 1], clamp);
  const r = (size / 2 - 8);
  const circ = 2 * Math.PI * r;
  return (
    <div style={{ position: "absolute", left: x - size / 2, top: y - size / 2, width: size, height: size, opacity: p }}>
      <svg width={size} height={size} style={{ transform: "rotate(-90deg)" }}>
        <circle cx={size/2} cy={size/2} r={r} stroke="rgba(255,255,255,0.08)" strokeWidth={8} fill="none" />
        <circle cx={size/2} cy={size/2} r={r} stroke={color} strokeWidth={8} fill="none" strokeLinecap="round"
          strokeDasharray={circ} strokeDashoffset={circ * (1 - p)} />
      </svg>
      {label && <div style={{ position: "absolute", inset: 0, display: "flex", alignItems: "center", justifyContent: "center", fontFamily: font, fontWeight: 800, fontSize: size * 0.22, color: C.white }}>{label}</div>}
    </div>
  );
};

const IconCard: React.FC<{ label: string; emoji: string; x: number; y: number; frame: number; fps: number; delay: number; scatter?: { x: number; y: number }; gatherAt?: number }> = ({
  label, emoji, x, y, frame, fps, delay, scatter, gatherAt = 9999,
}) => {
  const p = sp(frame, fps, delay, { damping: 14, stiffness: 150 });
  const g = interpolate(frame, [gatherAt, gatherAt + 25], [0, 1], clamp);
  const sx = scatter ? scatter.x * (1 - g) : 0;
  const sy = scatter ? scatter.y * (1 - g) : 0;
  return (
    <div style={{
      position: "absolute", left: x + sx, top: y + sy, width: 150, height: 150,
      borderRadius: 28, background: C.card, border: "1px solid rgba(255,255,255,0.1)",
      display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", gap: 10,
      opacity: interpolate(p, [0, 1], [0, 1]),
      transform: `scale(${interpolate(p, [0, 1], [0.5, 1])})`,
      boxShadow: "0 20px 60px -30px rgba(0,0,0,0.8)",
    }}>
      <div style={{ fontSize: 44 }}>{emoji}</div>
      <div style={{ fontFamily: font, fontSize: 18, fontWeight: 600, color: C.muted }}>{label}</div>
    </div>
  );
};

/* ─── SFX ─── */
const Sfx: React.FC<{ name: string; at: number; volume?: number }> = ({ name, at, volume = 0.45 }) => (
  <Sequence from={at} durationInFrames={45}>
    <Audio src={staticFile(`sfx/${name}.wav`)} volume={volume} />
  </Sequence>
);

/* ─── Main composition ─── */
export const KodoxAd: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps, width, height, durationInFrames } = useVideoConfig();
  const isV = height > width;
  const cx = width / 2;
  const cy = height / 2;

  // Scene visibility
  const S = (a: number, b: number) => ({ o: fade(frame, a, b), v: vis(frame, a, b) });
  const s1 = S(0, 75);      // Hook
  const s2 = S(70, 240);    // Problem: missed messages
  const s3 = S(235, 420);   // Problem: waiting/repeated
  const s4 = S(415, 600);   // Problem: scattered info
  const s5 = S(595, 780);   // Turn: clarity
  const s6 = S(775, 1020);  // Benefits flow
  const s7 = S(1015, 1260); // Human impact
  const s8 = S(1255, 1500); // Time back
  const s9 = S(1495, 1800); // Closing

  const mood: "chaos" | "calm" | "neutral" =
    frame < 600 ? "chaos" : frame < 780 ? "neutral" : "calm";

  return (
    <AbsoluteFill style={{ backgroundColor: C.bg, fontFamily: font }}>
      <BG frame={frame} mood={mood} />

      {/* ── S1 HOOK 0-2.5s ── */}
      {s1.v && (
        <AbsoluteFill style={{ opacity: s1.o, justifyContent: "center", alignItems: "center" }}>
          <div style={{ textAlign: "center", padding: isV ? 60 : 120 }}>
            <div style={{ fontFamily: font, fontSize: isV ? 44 : 36, fontWeight: 700, color: C.rose, letterSpacing: "0.2em", textTransform: "uppercase", opacity: interpolate(sp(frame, fps, 0), [0,1],[0,1]) }}>
              Cada día
            </div>
            <div style={{ height: isV ? 30 : 20 }} />
            <Big text="OPORTUNIDADES" frame={frame} fps={fps} delay={5} size={isV ? 110 : 130} />
            <Big text="PERDIDAS" frame={frame} fps={fps} delay={12} size={isV ? 120 : 140} gradient />
            <div style={{ height: isV ? 30 : 20 }} />
            <div style={{ fontFamily: font, fontSize: isV ? 44 : 38, color: C.muted, opacity: interpolate(sp(frame,fps,22),[0,1],[0,1]), transform: `translateY(${interpolate(sp(frame,fps,22),[0,1],[20,0])}px)` }}>
              en silencio.
            </div>
          </div>
          {/* pulsing rings */}
          {[0,1,2].map(i => {
            const r = interpolate(frame, [10 + i*8, 60 + i*8], [0, 1], clamp);
            return <div key={i} style={{ position:"absolute", left:cx-(isV?280:350), top:cy-(isV?280:350), width:isV?560:700, height:isV?560:700, borderRadius:"50%", border:`2px solid rgba(251,113,133,${0.35*(1-r)})`, transform:`scale(${0.6+r*0.8})` }} />;
          })}
        </AbsoluteFill>
      )}

      {/* ── S2 MISSED MESSAGES 2.3-8s ── */}
      {s2.v && (
        <AbsoluteFill style={{ opacity: s2.o }}>
          <div style={{ position:"absolute", top: isV?140:80, left:0, right:0, textAlign:"center" }}>
            <Big text="MENSAJES" frame={frame-70} fps={fps} delay={0} size={isV?88:96} color={C.white} />
            <Big text="SIN RESPUESTA" frame={frame-70} fps={fps} delay={8} size={isV?72:80} color={C.rose} />
          </div>
          <Phone frame={frame-70} fps={fps} delay={5} x={cx-(isV?190:170)} y={isV?420:320} w={isV?380:340} h={isV?680:580}>
            <Notif label="¿Tienen horario?" count="hace 2h" frame={frame-70} fps={fps} delay={15} y={20} gone goneAt={90} />
            <Notif label="¿Cuál es el precio?" count="hace 1h" frame={frame-70} fps={fps} delay={25} y={95} />
            <Notif label="¿Hacen envíos?" count="hace 45m" frame={frame-70} fps={fps} delay={35} y={170} />
            <Notif label="¿Me pueden atender?" count="hace 20m" frame={frame-70} fps={fps} delay={45} y={245} />
            <div style={{ position:"absolute", bottom:20, left:0, right:0, textAlign:"center", fontFamily:font, fontSize:52, fontWeight:900, color:C.rose, opacity:interpolate(sp(frame-70,fps,55),[0,1],[0,1]) }}>
              +27
              <div style={{ fontSize:18, color:C.muted, fontWeight:600, letterSpacing:"0.1em" }}>SIN LEER</div>
            </div>
          </Phone>
        </AbsoluteFill>
      )}

      {/* ── S3 WAITING / REPEATED 7.8-14s ── */}
      {s3.v && (
        <AbsoluteFill style={{ opacity: s3.o }}>
          <div style={{ position:"absolute", top: isV?130:70, left:0, right:0, textAlign:"center" }}>
            <Big text="EL CLIENTE" frame={frame-235} fps={fps} delay={0} size={isV?80:88} />
            <Big text="ESPERA" frame={frame-235} fps={fps} delay={8} size={isV?100:110} color={C.amber} />
          </div>
          {/* Clock */}
          <div style={{ position:"absolute", left:cx-120, top:isV?420:300, width:240, height:240, borderRadius:"50%", border:"3px solid rgba(251,191,36,0.3)", background:"rgba(251,191,36,0.06)" }}>
            <div style={{ position:"absolute", left:"50%", top:"50%", width:6, height:80, background:C.amber, borderRadius:99, transformOrigin:"bottom center", transform:`translate(-50%,-100%) rotate(${(frame-235)*12}deg)` }} />
            <div style={{ position:"absolute", left:"50%", top:"50%", width:4, height:55, background:C.white, borderRadius:99, transformOrigin:"bottom center", transform:`translate(-50%,-100%) rotate(${(frame-235)*1}deg)` }} />
            <div style={{ position:"absolute", left:"50%", top:"50%", width:14, height:14, background:C.amber, borderRadius:"50%", transform:"translate(-50%,-50%)" }} />
          </div>
          {/* Repeated questions */}
          {["¿Horario?","¿Precio?","¿Ubicación?"].map((q,i) => (
            <Bubble key={q} text={q} color={C.amber} frame={frame-235} fps={fps} delay={25+i*15} x={cx-(isV?200:320)+i*(isV?40:220)} y={isV?720+i*90:580} w={isV?360:200} float={4} />
          ))}
          <div style={{ position:"absolute", bottom:isV?220:120, left:0, right:0, textAlign:"center", fontFamily:font, fontSize:isV?34:30, color:C.muted, opacity:interpolate(sp(frame-235,fps,80),[0,1],[0,1]) }}>
            La misma pregunta, otra vez.
          </div>
        </AbsoluteFill>
      )}

      {/* ── S4 SCATTERED INFO 13.8-20s ── */}
      {s4.v && (
        <AbsoluteFill style={{ opacity: s4.o }}>
          <div style={{ position:"absolute", top:isV?120:70, left:0, right:0, textAlign:"center" }}>
            <Big text="INFO" frame={frame-415} fps={fps} delay={0} size={isV?100:110} />
            <Big text="DISPERSA" frame={frame-415} fps={fps} delay={8} size={isV?90:100} color={C.violet} />
          </div>
          {(() => {
            const baseY = isV ? 480 : 320;
            const baseX = cx - 230;
            const items = [
              { l:"Horario", e:"🕐", sx:-120, sy:-60 },
              { l:"Precios", e:"💰", sx:140, sy:-40 },
              { l:"Ubicación", e:"📍", sx:-140, sy:180 },
              { l:"Detalles", e:"📋", sx:130, sy:190 },
              { l:"Fotos", e:"🖼️", sx:-30, sy:320 },
              { l:"Respuestas", e:"💬", sx:60, sy:-160 },
            ];
            return items.map((it, i) => {
              const col = i % 2;
              const row = Math.floor(i / 2);
              return (
                <IconCard key={it.l} label={it.l} emoji={it.e}
                  x={baseX + col * 170} y={baseY + row * 170}
                  frame={frame-415} fps={fps} delay={15 + i * 6}
                  scatter={{ x: it.sx, y: it.sy }} gatherAt={120} />
              );
            });
          })()}
          <div style={{ position:"absolute", bottom:isV?220:100, left:0, right:0, textAlign:"center", fontFamily:font, fontSize:isV?34:30, color:C.muted, opacity:interpolate(sp(frame-415,fps,130),[0,1],[0,1]) }}>
            Todo en lugares distintos.
          </div>
        </AbsoluteFill>
      )}

      {/* ── S5 TURN / CLARITY 19.8-26s ── */}
      {s5.v && (
        <AbsoluteFill style={{ opacity: s5.o, justifyContent:"center", alignItems:"center" }}>
          <div style={{ textAlign:"center", padding: isV?64:120 }}>
            <div style={{ fontFamily:font, fontSize:isV?36:30, fontWeight:700, letterSpacing:"0.2em", color:C.cyan, textTransform:"uppercase", opacity:interpolate(sp(frame-595,fps,0),[0,1],[0,1]) }}>
              El cambio
            </div>
            <div style={{ height:isV?34:24 }} />
            <Big text="CUANDO TODO" frame={frame-595} fps={fps} delay={8} size={isV?92:100} />
            <Big text="ESTÁ CLARO" frame={frame-595} fps={fps} delay={18} size={isV?104:115} gradient />
            <div style={{ height:isV?40:30 }} />
            <div style={{ display:"flex", gap:20, justifyContent:"center", flexWrap:"wrap" }}>
              {["Respuestas rápidas","Menos fricción","Más confianza"].map((t,i) => {
                const p = sp(frame-595, fps, 45 + i*12);
                return (
                  <div key={t} style={{
                    padding:"20px 28px", borderRadius:20, border:`1px solid ${C.cyan}44`,
                    background:`${C.cyan}12`, fontFamily:font, fontSize:isV?32:28, fontWeight:700, color:C.white,
                    opacity:interpolate(p,[0,1],[0,1]), transform:`translateY(${interpolate(p,[0,1],[30,0])}px)`,
                  }}>{t}</div>
                );
              })}
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* ── S6 BENEFITS / FLOW 25.8-34s ── */}
      {s6.v && (
        <AbsoluteFill style={{ opacity: s6.o, justifyContent:"center", alignItems:"center", padding:isV?"100px 56px":"60px 120px" }}>
          <div style={{ width:"100%", maxWidth:isV?920:1100, display:"flex", flexDirection:"column", alignItems:"center", gap:isV?36:28 }}>
            <Big text="TODO FLUYE" frame={frame-775} fps={fps} delay={0} size={isV?96:104} gradient />
            {/* Before / after bars */}
            <div style={{ width:"100%", display:"flex", flexDirection:"column", gap:28, marginTop:20 }}>
              {[
                { label:"Tiempo de respuesta", before:90, after:15, bLabel:"2 horas", aLabel:"2 min", color:C.green },
                { label:"Preguntas repetidas", before:80, after:20, bLabel:"Muchas", aLabel:"Pocas", color:C.cyan },
                { label:"Estrés del día", before:85, after:25, bLabel:"Alto", aLabel:"Bajo", color:C.indigo },
              ].map((row, i) => {
                const p = sp(frame-775, fps, 20 + i*18);
                const showAfter = frame - 775 > 90 + i*18;
                const ap = sp(frame-775, fps, 90 + i*18);
                const w = showAfter ? interpolate(ap,[0,1],[row.before, row.after]) : row.before;
                return (
                  <div key={row.label} style={{ opacity:interpolate(p,[0,1],[0,1]), transform:`translateY(${interpolate(p,[0,1],[30,0])}px)` }}>
                    <div style={{ display:"flex", justifyContent:"space-between", fontFamily:font, fontSize:isV?26:22, color:C.muted, marginBottom:10 }}>
                      <span>{row.label}</span>
                      <span style={{ color: showAfter ? row.color : C.rose, fontWeight:700 }}>{showAfter ? row.aLabel : row.bLabel}</span>
                    </div>
                    <div style={{ height:18, borderRadius:99, background:"rgba(255,255,255,0.08)", overflow:"hidden" }}>
                      <div style={{ height:"100%", width:`${w}%`, borderRadius:99, background: showAfter ? `linear-gradient(90deg,${row.color},${C.cyan})` : C.rose, transition:"none" }} />
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* ── S7 HUMAN IMPACT 33.8-42s ── */}
      {s7.v && (
        <AbsoluteFill style={{ opacity: s7.o, justifyContent:"center", alignItems:"center", padding:isV?"100px 60px":"80px 120px" }}>
          <div style={{ textAlign:"center", maxWidth:isV?920:1000 }}>
            <Big text="NO ES UN LUJO" frame={frame-1015} fps={fps} delay={0} size={isV?88:96} />
            <div style={{ height:16 }} />
            <div style={{ fontFamily:font, fontSize:isV?40:36, color:C.muted, lineHeight:1.4, opacity:interpolate(sp(frame-1015,fps,15),[0,1],[0,1]) }}>
              Es cuidar el tiempo de las personas.
            </div>
            <div style={{ height:isV?50:40 }} />
            <div style={{ display:"flex", justifyContent:"center", gap:isV?24:40, flexWrap:"wrap" }}>
              {[
                { v:"−70%", l:"fricción", c:C.cyan },
                { v:"+claridad", l:"en cada respuesta", c:C.indigo },
                { v:"+tiempo", l:"para lo importante", c:C.violet },
              ].map((s, i) => {
                const p = sp(frame-1015, fps, 30 + i*14, { damping:14, stiffness:140 });
                return (
                  <div key={s.l} style={{ opacity:interpolate(p,[0,1],[0,1]), transform:`scale(${interpolate(p,[0,1],[0.7,1])})`, textAlign:"center" }}>
                    <div style={{ fontFamily:font, fontSize:isV?72:64, fontWeight:900, color:s.c, letterSpacing:"-0.03em" }}>{s.v}</div>
                    <div style={{ fontFamily:font, fontSize:isV?24:20, color:C.muted, marginTop:6 }}>{s.l}</div>
                  </div>
                );
              })}
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* ── S8 TIME BACK 42-50s ── */}
      {s8.v && (
        <AbsoluteFill style={{ opacity: s8.o, justifyContent:"center", alignItems:"center", padding:isV?"100px 64px":"80px 140px" }}>
          <div style={{ textAlign:"center", maxWidth:isV?900:1000 }}>
            <div style={{ fontFamily:font, fontSize:isV?34:28, fontWeight:700, letterSpacing:"0.18em", color:C.green, textTransform:"uppercase", opacity:interpolate(sp(frame-1255,fps,0),[0,1],[0,1]) }}>
              Resultado
            </div>
            <div style={{ height:24 }} />
            <Big text="MÁS TIEMPO" frame={frame-1255} fps={fps} delay={8} size={isV?100:110} />
            <Big text="PARA LO IMPORTANTE" frame={frame-1255} fps={fps} delay={18} size={isV?64:72} color={C.green} />
            <div style={{ height:50 }} />
            {/* Simple visual: calm ordered list */}
            <div style={{ display:"flex", flexDirection:"column", gap:16, alignItems:"center" }}>
              {["Información clara","Respuestas a tiempo","Personas tranquilas"].map((t,i) => {
                const p = sp(frame-1255, fps, 40 + i*15);
                return (
                  <div key={t} style={{
                    display:"flex", alignItems:"center", gap:16, padding:"18px 28px", borderRadius:16,
                    background:"rgba(52,211,153,0.08)", border:"1px solid rgba(52,211,153,0.25)",
                    opacity:interpolate(p,[0,1],[0,1]), transform:`translateX(${interpolate(p,[0,1],[-40,0])}px)`,
                  }}>
                    <div style={{ width:32, height:32, borderRadius:"50%", background:C.green, display:"flex", alignItems:"center", justifyContent:"center", fontFamily:font, fontWeight:800, fontSize:18, color:"#052e1a" }}>✓</div>
                    <span style={{ fontFamily:font, fontSize:isV?34:30, fontWeight:600, color:C.white }}>{t}</span>
                  </div>
                );
              })}
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* ── S9 CLOSING 49.8-60s ── */}
      {s9.v && (
        <AbsoluteFill style={{ opacity: s9.o, justifyContent:"center", alignItems:"center", padding:isV?"100px 64px":"80px 120px" }}>
          <div style={{ textAlign:"center", maxWidth:isV?920:1050, display:"flex", flexDirection:"column", alignItems:"center" }}>
            <Big text="La tecnología debería" frame={frame-1495} fps={fps} delay={10} size={isV?62:68} />
            <div style={{ height:8 }} />
            <Big text="SIMPLIFICAR" frame={frame-1495} fps={fps} delay={22} size={isV?96:104} gradient />
            <div style={{ height:8 }} />
            <Big text="la vida." frame={frame-1495} fps={fps} delay={32} size={isV?72:78} />
            <div style={{ height:isV?70:50 }} />
            {/* KODOX mark */}
            <div style={{
              opacity: interpolate(sp(frame-1495,fps,70),[0,1],[0,1]),
              transform: `scale(${interpolate(sp(frame-1495,fps,70, {damping:14,stiffness:140}),[0,1],[0.7,1])})`,
              display:"flex", flexDirection:"column", alignItems:"center", gap:18,
            }}>
              <div style={{
                width:100, height:100, borderRadius:32,
                border:"1px solid rgba(255,255,255,0.12)",
                background:"linear-gradient(145deg, rgba(99,102,241,0.25), rgba(16,16,24,0.9) 55%, rgba(34,211,238,0.2))",
                display:"flex", alignItems:"center", justifyContent:"center",
                boxShadow:"0 30px 80px -30px rgba(99,102,241,0.7)",
              }}>
                <span style={{ fontFamily:font, fontWeight:900, fontSize:44, color:C.white, letterSpacing:"-0.04em" }}>K</span>
              </div>
              <div style={{ fontFamily:font, fontSize:44, fontWeight:800, color:C.white, letterSpacing:"-0.03em" }}>KODOX</div>
              <div style={{ fontFamily:font, fontSize:18, letterSpacing:"0.28em", textTransform:"uppercase", color:"rgba(161,161,170,0.75)" }}>
                Claridad que se nota
              </div>
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* ── Subtitles (punchy, short) ── */}
      <Sub text="Cada día se pierden oportunidades…" frame={frame} fps={fps} enter={8} exit={55} isV={isV} big />
      <Sub text="…en silencio." frame={frame} fps={fps} enter={55} exit={75} isV={isV} />
      <Sub text="Mensajes sin responder." frame={frame} fps={fps} enter={90} exit={130} isV={isV} />
      <Sub text="Preguntas que llegan tarde." frame={frame} fps={fps} enter={130} exit={175} isV={isV} />
      <Sub text="Clientes que se cansan de esperar." frame={frame} fps={fps} enter={175} exit={230} isV={isV} />
      <Sub text="No es falta de ganas." frame={frame} fps={fps} enter={240} exit={290} isV={isV} />
      <Sub text="Es falta de claridad." frame={frame} fps={fps} enter={290} exit={340} isV={isV} />
      <Sub text="Información dispersa." frame={frame} fps={fps} enter={345} exit={390} isV={isV} />
      <Sub text="Horarios, precios, detalles… en mil lugares." frame={frame} fps={fps} enter={390} exit={460} isV={isV} />
      <Sub text="Todo se vuelve más difícil." frame={frame} fps={fps} enter={460} exit={520} isV={isV} />
      <Sub text="Pero cuando todo está claro…" frame={frame} fps={fps} enter={600} exit={660} isV={isV} big />
      <Sub text="Las respuestas llegan rápido." frame={frame} fps={fps} enter={660} exit={720} isV={isV} />
      <Sub text="La confusión desaparece." frame={frame} fps={fps} enter={720} exit={775} isV={isV} />
      <Sub text="Menos fricción. Menos estrés." frame={frame} fps={fps} enter={800} exit={870} isV={isV} />
      <Sub text="El tiempo vuelve a lo importante." frame={frame} fps={fps} enter={870} exit={950} isV={isV} />
      <Sub text="Organizar la información no es un lujo." frame={frame} fps={fps} enter={1020} exit={1100} isV={isV} />
      <Sub text="Es cuidar el tiempo de las personas." frame={frame} fps={fps} enter={1100} exit={1180} isV={isV} />
      <Sub text="Más claridad. Más confianza." frame={frame} fps={fps} enter={1180} exit={1255} isV={isV} />
      <Sub text="Más tiempo para lo importante." frame={frame} fps={fps} enter={1260} exit={1340} isV={isV} />
      <Sub text="La tecnología debería simplificar la vida…" frame={frame} fps={fps} enter={1500} exit={1600} isV={isV} big />
      <Sub text="…no complicarla." frame={frame} fps={fps} enter={1600} exit={1670} isV={isV} big />
      <Sub text="KODOX · Claridad que se nota." frame={frame} fps={fps} enter={1680} exit={1780} isV={isV} />

      {/* ── Progress ── */}
      <div style={{ position:"absolute", left:isV?56:80, right:isV?56:80, bottom:isV?56:36, height:3, background:"rgba(255,255,255,0.08)", borderRadius:99, overflow:"hidden" }}>
        <div style={{ height:"100%", width:`${(frame/(durationInFrames-1))*100}%`, background:"linear-gradient(90deg,#6366F1,#22D3EE)", borderRadius:99 }} />
      </div>

      {/* ── SFX every second (60 cues) ── */}
      {[
        ["bass",0,0.55],["hit",15,0.5],["whoosh",30,0.35],["pop",60,0.3],["tick",90,0.22],
        ["pop",120,0.28],["whoosh-soft",150,0.28],["hit-soft",180,0.3],["pop",210,0.3],
        ["transition",240,0.32],["tick",270,0.22],["pop",300,0.28],["reveal",330,0.3],
        ["whoosh",360,0.32],["hit-soft",390,0.3],["pop",420,0.28],["transition",450,0.32],
        ["tick",480,0.22],["pop",510,0.28],["whoosh-soft",540,0.28],["reveal",570,0.3],
        ["bass",600,0.45],["whoosh",630,0.34],["pop",660,0.3],["reveal",690,0.32],
        ["hit-soft",720,0.3],["pop",750,0.3],["transition",780,0.34],["whoosh",810,0.34],
        ["pop",840,0.3],["reveal",870,0.32],["hit",900,0.38],["pop",930,0.3],
        ["whoosh-soft",960,0.3],["reveal",990,0.32],["bass",1020,0.45],["hit-soft",1050,0.32],
        ["pop",1080,0.3],["reveal",1110,0.32],["whoosh",1140,0.32],["pop",1170,0.3],
        ["transition",1200,0.34],["reveal",1230,0.32],["pop",1260,0.32],["whoosh-soft",1290,0.3],
        ["hit-soft",1320,0.32],["pop",1350,0.3],["reveal",1380,0.32],["whoosh",1410,0.34],
        ["transition",1440,0.34],["pop",1470,0.3],["bass",1500,0.5],["whoosh",1530,0.36],
        ["hit",1560,0.45],["reveal",1590,0.34],["pop",1620,0.32],["whoosh-soft",1650,0.3],
        ["reveal",1680,0.36],["bass",1710,0.42],["pop",1740,0.28],["whoosh-soft",1770,0.25],
      ].map(([name, at, vol], i) => (
        <Sfx key={`${name}-${i}`} name={name as string} at={at as number} volume={vol as number} />
      ))}

      {/* Voiceover */}
      <Audio src={staticFile("audio/voiceover.mp3")} volume={1} />
    </AbsoluteFill>
  );
};
