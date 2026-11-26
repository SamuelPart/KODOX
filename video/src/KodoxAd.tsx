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
  card: "rgba(16,16,24,0.84)",
  white: "#FFFFFF",
  muted: "#B4B4BC",
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

const sp = (
  frame: number,
  fps: number,
  delay: number,
  cfg: { mass?: number; damping?: number; stiffness?: number; overshootClamping?: boolean } = { damping: 200 }
) => spring({ frame: frame - delay, fps, config: cfg });
const fade = (frame: number, start: number, end: number) =>
  interpolate(frame, [start, start + 6], [0, 1], clamp) *
  interpolate(frame, [end - 8, end], [1, 0], clamp);
const vis = (frame: number, start: number, end: number) => frame >= start - 2 && frame <= end + 2;

/* ─── BG ─── */
const BG: React.FC<{ frame: number; mood?: "problem" | "solution" | "neutral" }> = ({ frame, mood = "neutral" }) => {
  const d = interpolate(frame, [0, 1800], [0, 90]);
  const hot = mood === "problem" ? 0.22 : 0.06;
  const cool = mood === "solution" ? 0.24 : 0.14;
  return (
    <AbsoluteFill style={{ backgroundColor: C.bg }}>
      <div style={{ position: "absolute", inset: 0, backgroundImage: `
        radial-gradient(ellipse 70% 45% at ${20 + d * 0.12}% ${18 + d * 0.06}%, rgba(99,102,241,${cool}), transparent 62%),
        radial-gradient(ellipse 55% 40% at ${80 - d * 0.1}% ${25 + d * 0.05}%, rgba(34,211,238,${mood === "solution" ? 0.2 : 0.12}), transparent 60%),
        radial-gradient(ellipse 75% 55% at 50% ${82 - d * 0.08}%, rgba(251,113,133,${hot}), transparent 68%)
      ` }} />
      <div style={{ position: "absolute", inset: 0, opacity: 0.32, backgroundImage:
        "linear-gradient(to right, rgba(255,255,255,0.045) 1px, transparent 1px), linear-gradient(to bottom, rgba(255,255,255,0.045) 1px, transparent 1px)",
        backgroundSize: "56px 56px" }} />
    </AbsoluteFill>
  );
};

/* ─── Text ─── */
const Big: React.FC<{ text: string; frame: number; fps: number; delay: number; size: number; color?: string; gradient?: boolean }> = ({
  text, frame, fps, delay, size, color = C.white, gradient,
}) => {
  const p = sp(frame, fps, delay, { damping: 12, stiffness: 200, mass: 0.6 });
  return (
    <div style={{
      fontFamily: font, fontSize: size, fontWeight: 900, letterSpacing: "-0.045em", lineHeight: 0.92,
      color: gradient ? "transparent" : color,
      background: gradient ? "linear-gradient(100deg,#818cf8 0%,#a78bfa 40%,#22d3ee 100%)" : undefined,
      WebkitBackgroundClip: gradient ? "text" : undefined,
      opacity: interpolate(p, [0, 1], [0, 1]),
      transform: `translateY(${interpolate(p, [0, 1], [70, 0])}px) scale(${interpolate(p, [0, 1], [0.65, 1])})`,
      textAlign: "center",
    }}>{text}</div>
  );
};

const Tag: React.FC<{ text: string; frame: number; fps: number; delay: number; color: string }> = ({ text, frame, fps, delay, color }) => {
  const p = sp(frame, fps, delay, { damping: 14, stiffness: 180 });
  return (
    <div style={{
      opacity: interpolate(p, [0, 1], [0, 1]),
      transform: `scale(${interpolate(p, [0, 1], [0.7, 1])})`,
      padding: "12px 22px", borderRadius: 999, border: `1px solid ${color}66`,
      background: `${color}18`, color: C.white, fontFamily: font, fontSize: 22,
      fontWeight: 700, letterSpacing: "0.1em", textTransform: "uppercase",
    }}>{text}</div>
  );
};

/* ─── Visual pieces ─── */
const Phone: React.FC<{ frame: number; fps: number; delay: number; x: number; y: number; w: number; h: number; children?: React.ReactNode; glow?: string }> = ({
  frame, fps, delay, x, y, w, h, children, glow = C.indigo,
}) => {
  const p = sp(frame, fps, delay, { damping: 14, stiffness: 160 });
  return (
    <div style={{
      position: "absolute", left: x, top: y, width: w, height: h,
      borderRadius: w * 0.14, border: "2px solid rgba(255,255,255,0.14)",
      background: "linear-gradient(180deg,#18181f,#0a0a0f)",
      boxShadow: `0 40px 100px -30px rgba(0,0,0,0.9), 0 0 80px -30px ${glow}88`,
      overflow: "hidden", opacity: interpolate(p, [0, 1], [0, 1]),
      transform: `translateY(${interpolate(p, [0, 1], [90, 0])}px) scale(${interpolate(p, [0, 1], [0.88, 1])})`,
    }}>
      <div style={{ position: "absolute", top: 12, left: "50%", transform: "translateX(-50%)", width: w * 0.28, height: 6, borderRadius: 99, background: "rgba(255,255,255,0.12)" }} />
      <div style={{ position: "absolute", inset: 28 }}>{children}</div>
    </div>
  );
};

const Notif: React.FC<{ label: string; sub?: string; frame: number; fps: number; delay: number; y: number; color?: string; bad?: boolean }> = ({
  label, sub, frame, fps, delay, y, color = C.rose, bad,
}) => {
  const p = sp(frame, fps, delay, { damping: 14, stiffness: 180 });
  return (
    <div style={{
      position: "absolute", left: 16, right: 16, top: y,
      padding: "14px 14px", borderRadius: 16,
      background: bad ? "rgba(251,113,133,0.08)" : "rgba(255,255,255,0.06)",
      border: `1px solid ${bad ? "rgba(251,113,133,0.3)" : "rgba(255,255,255,0.1)"}`,
      display: "flex", justifyContent: "space-between", alignItems: "center",
      opacity: interpolate(p, [0, 1], [0, 1]),
      transform: `translateX(${interpolate(p, [0, 1], [60, 0])}px)`,
    }}>
      <div>
        <div style={{ fontFamily: font, fontSize: 19, color: C.white, fontWeight: 600 }}>{label}</div>
        {sub && <div style={{ fontFamily: font, fontSize: 14, color: C.dim, marginTop: 2 }}>{sub}</div>}
      </div>
      <div style={{ fontFamily: font, fontSize: 14, color, fontWeight: 700, background: `${color}20`, padding: "4px 10px", borderRadius: 99 }}>
        {bad ? "!" : "OK"}
      </div>
    </div>
  );
};

const FlowStep: React.FC<{ icon: string; label: string; x: number; y: number; frame: number; fps: number; delay: number; active?: boolean }> = ({
  icon, label, x, y, frame, fps, delay, active,
}) => {
  const p = sp(frame, fps, delay, { damping: 14, stiffness: 170 });
  const col = active ? C.green : C.cyan;
  return (
    <div style={{
      position: "absolute", left: x, top: y, width: 180, textAlign: "center",
      opacity: interpolate(p, [0, 1], [0, 1]),
      transform: `translateY(${interpolate(p, [0, 1], [40, 0])}px) scale(${interpolate(p, [0, 1], [0.8, 1])})`,
    }}>
      <div style={{
        width: 96, height: 96, borderRadius: 30, margin: "0 auto",
        background: `${col}18`, border: `1px solid ${col}55`,
        display: "flex", alignItems: "center", justifyContent: "center", fontSize: 42,
        boxShadow: active ? `0 0 50px -10px ${col}88` : undefined,
      }}>{icon}</div>
      <div style={{ fontFamily: font, fontSize: 24, fontWeight: 700, color: C.white, marginTop: 14 }}>{label}</div>
    </div>
  );
};

const Arrow: React.FC<{ x: number; y: number; frame: number; fps: number; delay: number }> = ({ x, y, frame, fps, delay }) => {
  const p = sp(frame, fps, delay, { damping: 200 });
  return (
    <div style={{
      position: "absolute", left: x, top: y, width: 60, height: 4, background: "rgba(255,255,255,0.2)", borderRadius: 99,
      opacity: interpolate(p, [0, 1], [0, 1]), transform: `scaleX(${interpolate(p, [0, 1], [0, 1])})`, transformOrigin: "left",
    }}>
      <div style={{ position: "absolute", right: -2, top: -6, width: 0, height: 0, borderTop: "8px solid transparent", borderBottom: "8px solid transparent", borderLeft: "14px solid rgba(255,255,255,0.3)" }} />
    </div>
  );
};

const Sfx: React.FC<{ name: string; at: number; volume?: number }> = ({ name, at, volume = 0.45 }) => (
  <Sequence from={at} durationInFrames={45}>
    <Audio src={staticFile(`sfx/${name}.wav`)} volume={volume} />
  </Sequence>
);

export const KodoxAd: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps, width, height, durationInFrames } = useVideoConfig();
  const isV = height > width;
  const cx = width / 2;

  const S = (a: number, b: number) => ({ o: fade(frame, a, b), v: vis(frame, a, b) });
  const s1 = S(0, 90);      // Hook
  const s2 = S(85, 210);    // Problems
  const s3 = S(205, 330);   // Pain
  const s4 = S(325, 420);   // Turn
  const s5 = S(415, 720);   // Solution flow
  const s6 = S(715, 960);   // Benefits
  const s7 = S(955, 1200);  // Outcome
  const s8 = S(1195, 1500); // Control
  const s9 = S(1495, 1800); // Closing

  const mood: "problem" | "solution" | "neutral" =
    frame < 330 ? "problem" : frame < 420 ? "neutral" : "solution";

  return (
    <AbsoluteFill style={{ backgroundColor: C.bg, fontFamily: font }}>
      <BG frame={frame} mood={mood} />

      {/* S1 HOOK */}
      {s1.v && (
        <AbsoluteFill style={{ opacity: s1.o, justifyContent: "center", alignItems: "center" }}>
          <div style={{ textAlign: "center", padding: isV ? 50 : 120 }}>
            <Tag text="Atención" frame={frame} fps={fps} delay={0} color={C.rose} />
            <div style={{ height: 28 }} />
            <Big text="¿PIERDES" frame={frame} fps={fps} delay={4} size={isV ? 118 : 130} />
            <Big text="CLIENTES?" frame={frame} fps={fps} delay={10} size={isV ? 118 : 130} color={C.rose} />
            <div style={{ height: 28 }} />
            <div style={{
              fontFamily: font, fontSize: isV ? 38 : 34, color: C.muted, fontWeight: 500,
              opacity: interpolate(sp(frame,fps,20),[0,1],[0,1]),
              transform: `translateY(${interpolate(sp(frame,fps,20),[0,1],[20,0])}px)`,
            }}>Sin darte cuenta.</div>
          </div>
          {[0,1,2].map(i => {
            const r = interpolate(frame, [6+i*10, 55+i*10], [0, 1], clamp);
            return <div key={i} style={{ position:"absolute", left:cx-(isV?300:360), top:"50%", marginTop:-(isV?300:360), width:isV?600:720, height:isV?600:720, borderRadius:"50%", border:`2px solid rgba(251,113,133,${0.3*(1-r)})`, transform:`scale(${0.5+r*0.9})` }} />;
          })}
        </AbsoluteFill>
      )}

      {/* S2 PROBLEMS */}
      {s2.v && (
        <AbsoluteFill style={{ opacity: s2.o }}>
          <div style={{ position:"absolute", top:isV?120:70, left:0, right:0, textAlign:"center" }}>
            <Big text="CAOS DIARIO" frame={frame-85} fps={fps} delay={0} size={isV?88:96} color={C.amber} />
          </div>
          <Phone frame={frame-85} fps={fps} delay={5} x={cx-(isV?185:170)} y={isV?360:280} w={isV?370:340} h={isV?660:560} glow={C.rose}>
            <Notif label="Mensaje sin leer" sub="Hace 3 horas" frame={frame-85} fps={fps} delay={12} y={20} bad />
            <Notif label="¿Precio?" sub="Pregunta repetida" frame={frame-85} fps={fps} delay={24} y={100} bad />
            <Notif label="Pedido confundido" sub="Mesa 4 / Delivery" frame={frame-85} fps={fps} delay={36} y={180} bad />
            <Notif label="Cliente perdido" sub="No respondió a tiempo" frame={frame-85} fps={fps} delay={48} y={260} bad />
            <div style={{ position:"absolute", bottom:24, left:0, right:0, textAlign:"center" }}>
              <div style={{ fontFamily:font, fontSize:56, fontWeight:900, color:C.rose, opacity:interpolate(sp(frame-85,fps,60),[0,1],[0,1]) }}>-38%</div>
              <div style={{ fontFamily:font, fontSize:16, color:C.dim, letterSpacing:"0.1em", fontWeight:600 }}>VENTAS PERDIDAS</div>
            </div>
          </Phone>
        </AbsoluteFill>
      )}

      {/* S3 PAIN */}
      {s3.v && (
        <AbsoluteFill style={{ opacity: s3.o, justifyContent:"center", alignItems:"center", padding:isV?"100px 56px":"80px 120px" }}>
          <div style={{ textAlign:"center", width:"100%", maxWidth:940 }}>
            <Big text="NO ES TU" frame={frame-205} fps={fps} delay={0} size={isV?90:96} />
            <Big text="ESFUERZO" frame={frame-205} fps={fps} delay={8} size={isV?90:96} color={C.amber} />
            <div style={{ height:36 }} />
            <div style={{
              fontFamily:font, fontSize:isV?44:40, color:C.white, fontWeight:700, lineHeight:1.25,
              opacity:interpolate(sp(frame-205,fps,22),[0,1],[0,1]),
            }}>
              Te falta un <span style={{ color:C.cyan }}>sistema claro</span>.
            </div>
            <div style={{ height:44 }} />
            <div style={{ display:"flex", justifyContent:"center", gap:20, flexWrap:"wrap" }}>
              {["Mensajes","Pedidos","Clientes"].map((t,i) => {
                const p = sp(frame-205,fps,35+i*10,{damping:13,stiffness:170});
                return (
                  <div key={t} style={{
                    padding:"20px 28px", borderRadius:20, background:"rgba(251,191,36,0.1)", border:"1px solid rgba(251,191,36,0.3)",
                    fontFamily:font, fontSize:isV?30:26, fontWeight:700, color:C.amber,
                    opacity:interpolate(p,[0,1],[0,1]), transform:`scale(${interpolate(p,[0,1],[0.7,1])})`,
                  }}>{t} por todos lados</div>
                );
              })}
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* S4 TURN */}
      {s4.v && (
        <AbsoluteFill style={{ opacity: s4.o, justifyContent:"center", alignItems:"center" }}>
          <div style={{ textAlign:"center", padding:isV?60:120 }}>
            <div style={{
              width:110, height:110, borderRadius:34, margin:"0 auto 30px",
              background:"linear-gradient(145deg, rgba(99,102,241,0.3), rgba(16,16,24,0.9) 55%, rgba(34,211,238,0.25))",
              border:"1px solid rgba(255,255,255,0.14)", display:"flex", alignItems:"center", justifyContent:"center",
              boxShadow:"0 30px 80px -30px rgba(99,102,241,0.8)",
              opacity:interpolate(sp(frame-325,fps,0),[0,1],[0,1]),
              transform:`scale(${interpolate(sp(frame-325,fps,0,{damping:12,stiffness:160}),[0,1],[0.5,1])})`,
            }}>
              <span style={{ fontFamily:font, fontWeight:900, fontSize:48, color:C.white }}>K</span>
            </div>
            <Big text="EN KODOX" frame={frame-325} fps={fps} delay={8} size={isV?96:106} gradient />
            <div style={{ height:14 }} />
            <Big text="LO RESOLVEMOS" frame={frame-325} fps={fps} delay={18} size={isV?74:82} />
          </div>
        </AbsoluteFill>
      )}

      {/* S5 SOLUTION FLOW */}
      {s5.v && (
        <AbsoluteFill style={{ opacity: s5.o }}>
          <div style={{ position:"absolute", top:isV?110:60, left:0, right:0, textAlign:"center" }}>
            <Tag text="La solución" frame={frame-415} fps={fps} delay={0} color={C.cyan} />
            <div style={{ height:20 }} />
            <Big text="TODO EN UN LUGAR" frame={frame-415} fps={fps} delay={8} size={isV?72:80} />
          </div>

          {/* Flow: Menu -> Pedido -> WhatsApp */}
          {(() => {
            const baseY = isV ? 470 : 320;
            const gap = isV ? 0 : 240;
            const startX = isV ? cx - 90 : cx - 330;
            const positions = isV
              ? [{ x: startX, y: baseY }, { x: startX, y: baseY + 230 }, { x: startX, y: baseY + 460 }]
              : [{ x: startX, y: baseY }, { x: startX + gap, y: baseY }, { x: startX + gap * 2, y: baseY }];
            const items = [
              { icon: "📋", label: "Tu menú" },
              { icon: "🛒", label: "Pedido claro" },
              { icon: "💬", label: "WhatsApp" },
            ];
            return (
              <>
                {items.map((it, i) => (
                  <FlowStep key={it.label} icon={it.icon} label={it.label} x={positions[i].x} y={positions[i].y}
                    frame={frame-415} fps={fps} delay={20 + i * 25} active={i === 2 && frame - 415 > 90} />
                ))}
                {!isV && (
                  <>
                    <Arrow x={startX + 170} y={baseY + 48} frame={frame-415} fps={fps} delay={45} />
                    <Arrow x={startX + 170 + gap} y={baseY + 48} frame={frame-415} fps={fps} delay={70} />
                  </>
                )}
                {isV && (
                  <>
                    <div style={{ position:"absolute", left:cx-2, top:baseY+110, width:4, height:80, background:"rgba(255,255,255,0.15)", borderRadius:99, opacity:interpolate(sp(frame-415,fps,45),[0,1],[0,1]) }} />
                    <div style={{ position:"absolute", left:cx-2, top:baseY+340, width:4, height:80, background:"rgba(255,255,255,0.15)", borderRadius:99, opacity:interpolate(sp(frame-415,fps,70),[0,1],[0,1]) }} />
                  </>
                )}
              </>
            );
          })()}

          {/* Result badges */}
          <div style={{ position:"absolute", bottom:isV?180:90, left:0, right:0, display:"flex", justifyContent:"center", gap:16, flexWrap:"wrap", padding:"0 40px" }}>
            {["Rápido","Claro","Profesional"].map((t,i) => {
              const p = sp(frame-415,fps,120+i*12,{damping:13,stiffness:170});
              return (
                <div key={t} style={{
                  padding:"16px 24px", borderRadius:16, background:"rgba(52,211,153,0.1)", border:"1px solid rgba(52,211,153,0.35)",
                  fontFamily:font, fontSize:isV?28:24, fontWeight:700, color:C.green,
                  opacity:interpolate(p,[0,1],[0,1]), transform:`scale(${interpolate(p,[0,1],[0.7,1])})`,
                }}>{t}</div>
              );
            })}
          </div>
        </AbsoluteFill>
      )}

      {/* S6 BENEFITS */}
      {s6.v && (
        <AbsoluteFill style={{ opacity: s6.o, justifyContent:"center", alignItems:"center", padding:isV?"100px 50px":"80px 100px" }}>
          <div style={{ width:"100%", maxWidth:980, textAlign:"center" }}>
            <Big text="SIN CAOS" frame={frame-715} fps={fps} delay={0} size={isV?96:104} gradient />
            <div style={{ height:44 }} />
            <div style={{ display:"flex", flexDirection:isV?"column":"row", gap:20, justifyContent:"center" }}>
              {[
                { t:"Sin confusión", c:C.cyan },
                { t:"Sin mensajes perdidos", c:C.indigo },
                { t:"Sin perder ventas", c:C.green },
              ].map((b,i) => {
                const p = sp(frame-715,fps,20+i*14,{damping:13,stiffness:170});
                return (
                  <div key={b.t} style={{
                    flex:1, padding:"28px 20px", borderRadius:24, background:`${b.c}12`, border:`1px solid ${b.c}44`,
                    fontFamily:font, fontSize:isV?36:30, fontWeight:800, color:C.white,
                    opacity:interpolate(p,[0,1],[0,1]), transform:`translateY(${interpolate(p,[0,1],[50,0])}px) scale(${interpolate(p,[0,1],[0.85,1])})`,
                  }}>
                    <div style={{ fontSize:40, marginBottom:10 }}>✓</div>
                    {b.t}
                  </div>
                );
              })}
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* S7 OUTCOME */}
      {s7.v && (
        <AbsoluteFill style={{ opacity: s7.o, justifyContent:"center", alignItems:"center", padding:isV?"100px 56px":"80px 120px" }}>
          <div style={{ width:"100%", maxWidth:960, textAlign:"center" }}>
            <Big text="TU NEGOCIO" frame={frame-955} fps={fps} delay={0} size={isV?82:90} />
            <Big text="AL SIGUIENTE NIVEL" frame={frame-955} fps={fps} delay={10} size={isV?62:70} gradient />
            <div style={{ height:50 }} />
            <div style={{ display:"flex", flexDirection:isV?"column":"row", gap:20, justifyContent:"center" }}>
              {[
                { t:"Imagen", v:"Profesional", c:C.indigo },
                { t:"Clientes", v:"Encuentran todo", c:C.cyan },
                { t:"Tú", v:"Tienes control", c:C.green },
              ].map((m,i) => {
                const p = sp(frame-955,fps,30+i*14,{damping:13,stiffness:160});
                return (
                  <div key={m.t} style={{
                    flex:1, padding:"26px 20px", borderRadius:24, background:C.card, border:`1px solid ${m.c}44`,
                    opacity:interpolate(p,[0,1],[0,1]), transform:`translateY(${interpolate(p,[0,1],[40,0])}px)`,
                    textAlign:"left",
                  }}>
                    <div style={{ fontFamily:font, fontSize:15, color:C.dim, fontWeight:700, letterSpacing:"0.1em", textTransform:"uppercase" }}>{m.t}</div>
                    <div style={{ fontFamily:font, fontSize:isV?36:30, color:C.white, fontWeight:800, marginTop:8 }}>{m.v}</div>
                    <div style={{ height:5, borderRadius:99, background:`${m.c}33`, marginTop:14 }}><div style={{ height:"100%", width:`${interpolate(sp(frame-955,fps,40+i*14),[0,1],[0,100])}%`, background:m.c, borderRadius:99 }} /></div>
                  </div>
                );
              })}
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* S8 CONTROL */}
      {s8.v && (
        <AbsoluteFill style={{ opacity: s8.o, justifyContent:"center", alignItems:"center", padding:isV?"100px 60px":"80px 120px" }}>
          <div style={{ textAlign:"center", maxWidth:920 }}>
            <Big text="MÁS CLARIDAD" frame={frame-1195} fps={fps} delay={0} size={isV?78:86} color={C.cyan} />
            <Big text="MÁS PEDIDOS" frame={frame-1195} fps={fps} delay={12} size={isV?78:86} color={C.indigo} />
            <Big text="MÁS TIEMPO" frame={frame-1195} fps={fps} delay={24} size={isV?78:86} gradient />
            <div style={{ height:40 }} />
            <div style={{
              fontFamily:font, fontSize:isV?36:32, color:C.muted, fontWeight:500,
              opacity:interpolate(sp(frame-1195,fps,45),[0,1],[0,1]),
            }}>
              Para ti y para tu negocio.
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* S9 CLOSING */}
      {s9.v && (
        <AbsoluteFill style={{ opacity: s9.o, justifyContent:"center", alignItems:"center", padding:isV?"100px 64px":"80px 120px" }}>
          <div style={{ textAlign:"center", maxWidth:1000, display:"flex", flexDirection:"column", alignItems:"center" }}>
            <div style={{
              width:110, height:110, borderRadius:34, marginBottom:32,
              background:"linear-gradient(145deg, rgba(99,102,241,0.3), rgba(16,16,24,0.9) 55%, rgba(34,211,238,0.25))",
              border:"1px solid rgba(255,255,255,0.14)", display:"flex", alignItems:"center", justifyContent:"center",
              boxShadow:"0 30px 80px -30px rgba(99,102,241,0.8)",
              opacity:interpolate(sp(frame-1495,fps,5),[0,1],[0,1]),
              transform:`scale(${interpolate(sp(frame-1495,fps,5,{damping:12,stiffness:150}),[0,1],[0.6,1])})`,
            }}>
              <span style={{ fontFamily:font, fontWeight:900, fontSize:48, color:C.white }}>K</span>
            </div>
            <Big text="KODOX" frame={frame-1495} fps={fps} delay={12} size={isV?110:120} gradient />
            <div style={{ height:30 }} />
            <div style={{
              fontFamily:font, fontSize:isV?44:40, fontWeight:700, color:C.white, lineHeight:1.3, maxWidth:860,
              opacity:interpolate(sp(frame-1495,fps,30),[0,1],[0,1]),
              transform:`translateY(${interpolate(sp(frame-1495,fps,30),[0,1],[30,0])}px)`,
            }}>
              Haz que tu negocio trabaje
              <br /><span style={{ color:C.cyan }}>contigo</span>, no contra ti.
            </div>
            <div style={{ height:36 }} />
            <div style={{
              padding:"18px 34px", borderRadius:999, background:"linear-gradient(90deg,#6366F1,#8B5CF6,#22D3EE)",
              fontFamily:font, fontSize:isV?28:24, fontWeight:800, color:C.white,
              opacity:interpolate(sp(frame-1495,fps,55),[0,1],[0,1]),
              transform:`scale(${interpolate(sp(frame-1495,fps,55,{damping:12,stiffness:160}),[0,1],[0.8,1])})`,
              boxShadow:"0 20px 60px -20px rgba(99,102,241,0.8)",
            }}>
              Digital Studio · Trujillo
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* Progress */}
      <div style={{ position:"absolute", left:isV?56:80, right:isV?56:80, bottom:isV?56:36, height:3, background:"rgba(255,255,255,0.08)", borderRadius:99, overflow:"hidden" }}>
        <div style={{ height:"100%", width:`${(frame/(durationInFrames-1))*100}%`, background:"linear-gradient(90deg,#6366F1,#22D3EE)", borderRadius:99 }} />
      </div>

      {/* SFX every second */}
      {[
        ["bass",0,0.6],["hit",12,0.55],["whoosh",30,0.38],["hit-soft",60,0.35],["pop",90,0.32],
        ["tick",120,0.24],["pop",150,0.3],["hit-soft",180,0.34],["transition",210,0.36],
        ["whoosh",240,0.34],["pop",270,0.3],["reveal",300,0.32],["bass",330,0.55],
        ["hit",345,0.5],["whoosh",360,0.38],["reveal",390,0.34],["pop",420,0.32],
        ["whoosh-soft",450,0.3],["pop",480,0.32],["reveal",510,0.34],["tick",540,0.24],
        ["pop",570,0.3],["whoosh",600,0.36],["hit-soft",630,0.34],["pop",660,0.32],
        ["reveal",690,0.34],["bass",720,0.5],["hit",735,0.45],["pop",750,0.32],
        ["pop",780,0.32],["reveal",810,0.34],["whoosh",840,0.36],["hit-soft",870,0.34],
        ["pop",900,0.32],["reveal",930,0.34],["bass",960,0.5],["whoosh",990,0.36],
        ["pop",1020,0.32],["reveal",1050,0.34],["hit-soft",1080,0.34],["pop",1110,0.32],
        ["whoosh-soft",1140,0.32],["reveal",1170,0.34],["bass",1200,0.52],["hit",1215,0.48],
        ["whoosh",1230,0.38],["pop",1260,0.32],["reveal",1290,0.34],["pop",1320,0.32],
        ["hit-soft",1350,0.34],["whoosh",1380,0.36],["pop",1410,0.32],["reveal",1440,0.34],
        ["transition",1470,0.36],["bass",1500,0.58],["hit",1515,0.52],["whoosh",1530,0.4],
        ["reveal",1560,0.36],["pop",1590,0.34],["hit-soft",1620,0.36],["reveal",1650,0.36],
        ["whoosh",1680,0.38],["pop",1710,0.32],["bass",1740,0.45],["reveal",1770,0.3],
      ].map(([name, at, vol], i) => (
        <Sfx key={`${name}-${i}`} name={name as string} at={at as number} volume={vol as number} />
      ))}

      {/* Voiceover */}
      <Audio src={staticFile("audio/voiceover.mp3")} volume={1} />
    </AbsoluteFill>
  );
};
