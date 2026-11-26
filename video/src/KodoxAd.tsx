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
  Easing,
} from "remotion";

export const KODOX_AD_DURATION = 750; // 25s @ 30fps

const palette = {
  bg: "#050507",
  bgSoft: "#101018",
  white: "#FFFFFF",
  muted: "#A1A1AA",
  dim: "#71717A",
  indigo: "#6366F1",
  violet: "#8B5CF6",
  cyan: "#22D3EE",
  rose: "#FB7185",
  amber: "#FBBF24",
};

const font = '"Inter", "Segoe UI", "Helvetica Neue", Arial, sans-serif';

const clamp = {
  extrapolateLeft: "clamp" as const,
  extrapolateRight: "clamp" as const,
};

type SceneCtx = {
  frame: number;
  fps: number;
  width: number;
  height: number;
  isV: boolean;
};

const BG: React.FC<{ frame: number; intensity?: number }> = ({ frame, intensity = 1 }) => {
  const d = interpolate(frame, [0, 750], [0, 60]);
  return (
    <AbsoluteFill style={{ backgroundColor: palette.bg }}>
      <div
        style={{
          position: "absolute",
          inset: 0,
          backgroundImage: `
            radial-gradient(ellipse 70% 45% at ${25 + d * 0.2}% ${20 + d * 0.1}%, rgba(99,102,241,${0.28 * intensity}), transparent 62%),
            radial-gradient(ellipse 60% 40% at ${78 - d * 0.15}% ${25 + d * 0.08}%, rgba(34,211,238,${0.18 * intensity}), transparent 60%),
            radial-gradient(ellipse 80% 55% at 50% ${85 - d * 0.12}%, rgba(139,92,246,${0.16 * intensity}), transparent 68%)
          `,
        }}
      />
      <div
        style={{
          position: "absolute",
          inset: 0,
          backgroundImage:
            "linear-gradient(to right, rgba(255,255,255,0.04) 1px, transparent 1px), linear-gradient(to bottom, rgba(255,255,255,0.04) 1px, transparent 1px)",
          backgroundSize: "56px 56px",
          opacity: 0.45,
        }}
      />
    </AbsoluteFill>
  );
};

const Caption: React.FC<{ text: string; frame: number; fps: number; enter: number; exit: number; isV: boolean }> = ({
  text,
  frame,
  fps,
  enter,
  exit,
  isV,
}) => {
  const i = spring({ frame: frame - enter, fps, config: { damping: 200 } });
  const o = interpolate(frame, [exit - 10, exit], [1, 0], clamp);
  const opacity = interpolate(i, [0, 1], [0, 1]) * o;
  const y = interpolate(i, [0, 1], [24, 0]);
  if (frame < enter - 2 || frame > exit + 2) return null;
  return (
    <div
      style={{
        position: "absolute",
        left: isV ? 56 : 120,
        right: isV ? 56 : 120,
        bottom: isV ? 150 : 90,
        display: "flex",
        justifyContent: "center",
        opacity,
        transform: `translateY(${y}px)`,
      }}
    >
      <div
        style={{
          fontFamily: font,
          fontSize: isV ? 42 : 34,
          lineHeight: 1.35,
          fontWeight: 600,
          color: palette.white,
          textAlign: "center",
          textShadow: "0 4px 30px rgba(0,0,0,0.9)",
          background: "rgba(5,5,7,0.45)",
          border: "1px solid rgba(255,255,255,0.08)",
          backdropFilter: "blur(8px)",
          padding: isV ? "18px 28px" : "14px 24px",
          borderRadius: 18,
          maxWidth: isV ? 920 : 1100,
        }}
      >
        {text}
      </div>
    </div>
  );
};

const BigWord: React.FC<{ text: string; frame: number; fps: number; delay: number; size: number; color?: string; gradient?: boolean }> = ({
  text,
  frame,
  fps,
  delay,
  size,
  color = palette.white,
  gradient = false,
}) => {
  const p = spring({ frame: frame - delay, fps, config: { damping: 14, stiffness: 180, mass: 0.7 } });
  const opacity = interpolate(p, [0, 1], [0, 1]);
  const scale = interpolate(p, [0, 1], [0.7, 1]);
  const y = interpolate(p, [0, 1], [50, 0]);
  return (
    <div
      style={{
        fontFamily: font,
        fontSize: size,
        fontWeight: 800,
        letterSpacing: "-0.045em",
        lineHeight: 1.0,
        color: gradient ? "transparent" : color,
        background: gradient ? "linear-gradient(100deg,#818cf8 0%,#a78bfa 40%,#22d3ee 100%)" : undefined,
        WebkitBackgroundClip: gradient ? "text" : undefined,
        opacity,
        transform: `translateY(${y}px) scale(${scale})`,
        textAlign: "center",
      }}
    >
      {text}
    </div>
  );
};

const Pill: React.FC<{ text: string; frame: number; fps: number; delay: number; color?: string }> = ({ text, frame, fps, delay, color = palette.indigo }) => {
  const p = spring({ frame: frame - delay, fps, config: { damping: 200 } });
  return (
    <div
      style={{
        opacity: interpolate(p, [0, 1], [0, 1]),
        transform: `translateY(${interpolate(p, [0, 1], [20, 0])}px)`,
        display: "inline-flex",
        alignItems: "center",
        gap: 10,
        padding: "12px 20px",
        borderRadius: 999,
        border: `1px solid ${color}55`,
        background: `${color}18`,
        color: "#C7D2FE",
        fontFamily: font,
        fontSize: 20,
        fontWeight: 700,
        letterSpacing: "0.12em",
        textTransform: "uppercase",
      }}
    >
      <span style={{ width: 8, height: 8, borderRadius: "50%", background: palette.cyan, boxShadow: `0 0 14px ${palette.cyan}` }} />
      {text}
    </div>
  );
};

const ProblemCard: React.FC<{ icon: string; title: string; desc: string; frame: number; fps: number; delay: number; isV: boolean; accent: string }> = ({
  icon, title, desc, frame, fps, delay, isV, accent,
}) => {
  const p = spring({ frame: frame - delay, fps, config: { damping: 16, stiffness: 160 } });
  const opacity = interpolate(p, [0, 1], [0, 1]);
  const y = interpolate(p, [0, 1], [60, 0]);
  const scale = interpolate(p, [0, 1], [0.9, 1]);
  return (
    <div
      style={{
        opacity,
        transform: `translateY(${y}px) scale(${scale})`,
        padding: isV ? "32px 28px" : "28px 24px",
        borderRadius: 28,
        border: "1px solid rgba(255,255,255,0.1)",
        background: "rgba(16,16,24,0.78)",
        backdropFilter: "blur(10px)",
        width: "100%",
        boxShadow: "0 30px 80px -40px rgba(0,0,0,0.9)",
      }}
    >
      <div style={{ display: "flex", alignItems: "center", gap: 16, marginBottom: 14 }}>
        <div
          style={{
            width: 56, height: 56, borderRadius: 18,
            background: `${accent}22`, border: `1px solid ${accent}55`,
            display: "flex", alignItems: "center", justifyContent: "center",
            fontSize: 28,
          }}
        >
          {icon}
        </div>
        <div style={{ fontFamily: font, fontSize: isV ? 40 : 32, fontWeight: 700, color: palette.white, letterSpacing: "-0.03em", lineHeight: 1.1 }}>
          {title}
        </div>
      </div>
      <div style={{ fontFamily: font, fontSize: isV ? 28 : 22, lineHeight: 1.5, color: palette.muted }}>
        {desc}
      </div>
    </div>
  );
};

const StatChip: React.FC<{ text: string; frame: number; fps: number; delay: number; accent: string }> = ({ text, frame, fps, delay, accent }) => {
  const p = spring({ frame: frame - delay, fps, config: { damping: 200 } });
  return (
    <div
      style={{
        opacity: interpolate(p, [0, 1], [0, 1]),
        transform: `scale(${interpolate(p, [0, 1], [0.85, 1])})`,
        padding: "16px 22px",
        borderRadius: 16,
        border: `1px solid ${accent}44`,
        background: `${accent}14`,
        color: palette.white,
        fontFamily: font,
        fontSize: 28,
        fontWeight: 600,
      }}
    >
      {text}
    </div>
  );
};

const Sfx: React.FC<{ name: string; at: number; volume?: number; duration?: number }> = ({ name, at, volume = 0.5, duration = 45 }) => (
  <Sequence from={at} durationInFrames={duration}>
    <Audio src={staticFile(`sfx/${name}.wav`)} volume={volume} />
  </Sequence>
);

export const KodoxAd: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps, width, height, durationInFrames } = useVideoConfig();
  const isV = height > width;

  // Scene opacity helpers
  const scene = (start: number, end: number) => {
    const i = interpolate(frame, [start, start + 10], [0, 1], clamp);
    const o = interpolate(frame, [end - 12, end], [1, 0], clamp);
    return { opacity: i * o, visible: frame >= start - 2 && frame <= end + 2 };
  };

  const sHook = scene(0, 90);
  const sProb = scene(85, 210);
  const sChaos = scene(205, 330);
  const sShift = scene(325, 450);
  const sHuman = scene(445, 570);
  const sClose = scene(565, 750);

  return (
    <AbsoluteFill style={{ backgroundColor: palette.bg, fontFamily: font }}>
      <BG frame={frame} intensity={1} />

      {/* HOOK 0-3s */}
      {sHook.visible && (
        <AbsoluteFill style={{ opacity: sHook.opacity, justifyContent: "center", alignItems: "center", padding: isV ? 64 : 120 }}>
          <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: isV ? 28 : 24 }}>
            <Pill text="Realidad diaria" frame={frame} fps={fps} delay={0} />
            <BigWord text="¿Y si el problema" frame={frame} fps={fps} delay={4} size={isV ? 86 : 92} />
            <BigWord text="NO es tu trabajo?" frame={frame} fps={fps} delay={10} size={isV ? 92 : 100} gradient />
            <div style={{ height: 10 }} />
            <div
              style={{
                fontFamily: font,
                fontSize: isV ? 38 : 34,
                color: palette.muted,
                textAlign: "center",
                maxWidth: isV ? 860 : 900,
                lineHeight: 1.45,
                opacity: interpolate(spring({ frame: frame - 22, fps, config: { damping: 200 } }), [0, 1], [0, 1]),
                transform: `translateY(${interpolate(spring({ frame: frame - 22, fps, config: { damping: 200 } }), [0, 1], [20, 0])}px)`,
              }}
            >
              Es todo lo que pasa alrededor.
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* PROBLEMS 3-7s */}
      {sProb.visible && (
        <AbsoluteFill style={{ opacity: sProb.opacity, justifyContent: "center", padding: isV ? "120px 56px" : "80px 140px" }}>
          <div style={{ display: "flex", flexDirection: "column", gap: isV ? 26 : 22, maxWidth: isV ? 960 : 1100, margin: "0 auto", width: "100%" }}>
            <Pill text="Lo que pasa todos los días" frame={frame - 85} fps={fps} delay={0} color={palette.rose} />
            <div style={{ fontFamily: font, fontSize: isV ? 64 : 60, fontWeight: 800, color: palette.white, letterSpacing: "-0.04em", lineHeight: 1.05, marginBottom: 8 }}>
              Pequeñas fricciones.
              <br />
              <span style={{ color: "#FDA4AF" }}>Gran desgaste.</span>
            </div>
            <ProblemCard icon="💬" title="Mensajes que se pierden" desc="Consultas importantes quedan enterradas entre chats y pendientes." frame={frame - 85} fps={fps} delay={10} isV={isV} accent={palette.rose} />
            <ProblemCard icon="⏳" title="Preguntas repetidas" desc="La misma información se explica una y otra vez, todos los días." frame={frame - 85} fps={fps} delay={22} isV={isV} accent={palette.amber} />
            <ProblemCard icon="⚡" title="Tiempo que se va" desc="Tareas simples consumen energía que podría ir a lo importante." frame={frame - 85} fps={fps} delay={34} isV={isV} accent={palette.cyan} />
          </div>
        </AbsoluteFill>
      )}

      {/* CHAOS / DISPERSO 7-11s */}
      {sChaos.visible && (
        <AbsoluteFill style={{ opacity: sChaos.opacity, justifyContent: "center", alignItems: "center", padding: isV ? "120px 56px" : "80px 120px" }}>
          <div style={{ width: "100%", maxWidth: isV ? 940 : 1100, display: "flex", flexDirection: "column", alignItems: "center", gap: isV ? 36 : 30 }}>
            <Pill text="El costo invisible" frame={frame - 205} fps={fps} delay={0} color={palette.amber} />
            <div style={{ fontFamily: font, fontSize: isV ? 68 : 72, fontWeight: 800, color: palette.white, letterSpacing: "-0.04em", lineHeight: 1.05, textAlign: "center" }}>
              Cuando la info está
              <br />
              <span style={{ background: "linear-gradient(100deg,#fbbf24,#fb7185)", WebkitBackgroundClip: "text", color: "transparent" }}>dispersa,</span> todos esperan.
            </div>
            <div style={{ display: "flex", flexWrap: "wrap", justifyContent: "center", gap: 16, marginTop: 10 }}>
              {["Horarios", "Precios", "Ubicación", "Disponibilidad", "Respuestas", "Detalles"].map((t, i) => (
                <StatChip key={t} text={t} frame={frame - 205} fps={fps} delay={12 + i * 6} accent={i % 2 === 0 ? palette.amber : palette.rose} />
              ))}
            </div>
            <div
              style={{
                fontFamily: font, fontSize: isV ? 32 : 28, color: palette.muted, textAlign: "center", lineHeight: 1.5, maxWidth: 820,
                opacity: interpolate(spring({ frame: frame - 205 - 50, fps, config: { damping: 200 } }), [0, 1], [0, 1]),
              }}
            >
              Un cliente escribe y la respuesta llega tarde.
              <br />No por descuido, sino por falta de claridad.
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* SHIFT 11-15s */}
      {sShift.visible && (
        <AbsoluteFill style={{ opacity: sShift.opacity, justifyContent: "center", alignItems: "center", padding: isV ? "120px 64px" : "80px 140px" }}>
          <div style={{ maxWidth: isV ? 920 : 1050, textAlign: "center", display: "flex", flexDirection: "column", alignItems: "center", gap: isV ? 30 : 26 }}>
            <Pill text="Un cambio de enfoque" frame={frame - 325} fps={fps} delay={0} color={palette.cyan} />
            <div style={{ fontFamily: font, fontSize: isV ? 74 : 78, fontWeight: 800, color: palette.white, letterSpacing: "-0.04em", lineHeight: 1.06 }}>
              Cuando la info es clara,
              <br />
              <span style={{ background: "linear-gradient(100deg,#818cf8,#22d3ee)", WebkitBackgroundClip: "text", color: "transparent" }}>todo fluye mejor.</span>
            </div>
            <div style={{ width: 160, height: 3, background: "linear-gradient(90deg, transparent, #6366F1, #22D3EE, transparent)", borderRadius: 999 }} />
            <div style={{ display: "flex", flexDirection: isV ? "column" : "row", gap: 18, width: "100%", marginTop: 10 }}>
              {[
                { t: "Respuestas rápidas", c: palette.cyan },
                { t: "Menos fricción", c: palette.indigo },
                { t: "Menos estrés", c: palette.violet },
              ].map((item, i) => (
                <div
                  key={item.t}
                  style={{
                    flex: 1,
                    padding: "24px 20px",
                    borderRadius: 20,
                    border: `1px solid ${item.c}44`,
                    background: `${item.c}12`,
                    fontFamily: font,
                    fontSize: isV ? 34 : 28,
                    fontWeight: 700,
                    color: palette.white,
                    opacity: interpolate(spring({ frame: frame - 325 - 18 - i * 10, fps, config: { damping: 200 } }), [0, 1], [0, 1]),
                    transform: `translateY(${interpolate(spring({ frame: frame - 325 - 18 - i * 10, fps, config: { damping: 200 } }), [0, 1], [30, 0])}px)`,
                  }}
                >
                  {item.t}
                </div>
              ))}
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* HUMAN 15-19s */}
      {sHuman.visible && (
        <AbsoluteFill style={{ opacity: sHuman.opacity, justifyContent: "center", alignItems: "center", padding: isV ? "120px 64px" : "80px 140px" }}>
          <div style={{ maxWidth: isV ? 920 : 1000, textAlign: "center", display: "flex", flexDirection: "column", alignItems: "center", gap: 28 }}>
            <Pill text="Lo que importa" frame={frame - 445} fps={fps} delay={0} color={palette.violet} />
            <div style={{ fontFamily: font, fontSize: isV ? 70 : 74, fontWeight: 800, color: palette.white, letterSpacing: "-0.04em", lineHeight: 1.08 }}>
              Organizar la información
              <br />
              <span style={{ color: "#C4B5FD" }}>no es un lujo.</span>
            </div>
            <div
              style={{
                fontFamily: font, fontSize: isV ? 38 : 34, color: palette.muted, lineHeight: 1.5, maxWidth: 820,
                opacity: interpolate(spring({ frame: frame - 445 - 16, fps, config: { damping: 200 } }), [0, 1], [0, 1]),
              }}
            >
              Es una forma de cuidar el tiempo de las personas.
            </div>
            <div style={{ display: "flex", gap: 20, marginTop: 12, flexWrap: "wrap", justifyContent: "center" }}>
              {["Más claridad", "Más confianza", "Más tiempo"].map((t, i) => (
                <StatChip key={t} text={t} frame={frame - 445} fps={fps} delay={26 + i * 8} accent={palette.violet} />
              ))}
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* CLOSING 19-25s */}
      {sClose.visible && (
        <AbsoluteFill style={{ opacity: sClose.opacity, justifyContent: "center", alignItems: "center", padding: isV ? "120px 64px" : "80px 140px" }}>
          <div style={{ maxWidth: isV ? 920 : 1050, textAlign: "center", display: "flex", flexDirection: "column", alignItems: "center" }}>
            <div
              style={{
                width: 96, height: 96, borderRadius: 30, marginBottom: 36,
                border: "1px solid rgba(255,255,255,0.12)",
                background: "linear-gradient(145deg, rgba(99,102,241,0.22), rgba(16,16,24,0.85) 55%, rgba(34,211,238,0.18))",
                display: "flex", alignItems: "center", justifyContent: "center",
                boxShadow: "0 30px 80px -30px rgba(99,102,241,0.65)",
                opacity: interpolate(spring({ frame: frame - 565 - 8, fps, config: { damping: 200 } }), [0, 1], [0, 1]),
                transform: `scale(${interpolate(spring({ frame: frame - 565 - 8, fps, config: { damping: 200 } }), [0, 1], [0.8, 1])})`,
              }}
            >
              <span style={{ fontFamily: font, fontWeight: 800, fontSize: 40, color: palette.white }}>K</span>
            </div>
            <BigWord text="La tecnología debería" frame={frame - 565} fps={fps} delay={12} size={isV ? 68 : 72} />
            <div style={{ height: 6 }} />
            <BigWord text="simplificar la vida," frame={frame - 565} fps={fps} delay={20} size={isV ? 76 : 80} gradient />
            <div style={{ height: 6 }} />
            <BigWord text="no complicarla." frame={frame - 565} fps={fps} delay={28} size={isV ? 68 : 72} />
            <div style={{ height: 44 }} />
            <div style={{ display: "flex", gap: 14, flexWrap: "wrap", justifyContent: "center" }}>
              {["Claridad", "Orden", "Tranquilidad"].map((t, i) => (
                <div
                  key={t}
                  style={{
                    padding: "14px 24px",
                    borderRadius: 999,
                    border: "1px solid rgba(255,255,255,0.12)",
                    background: "rgba(255,255,255,0.04)",
                    color: palette.white,
                    fontFamily: font,
                    fontSize: isV ? 30 : 26,
                    fontWeight: 600,
                    opacity: interpolate(spring({ frame: frame - 565 - 55 - i * 10, fps, config: { damping: 200 } }), [0, 1], [0, 1]),
                    transform: `translateY(${interpolate(spring({ frame: frame - 565 - 55 - i * 10, fps, config: { damping: 200 } }), [0, 1], [20, 0])}px)`,
                  }}
                >
                  {t}
                </div>
              ))}
            </div>
            <div
              style={{
                marginTop: 44,
                fontFamily: font,
                fontSize: 18,
                letterSpacing: "0.24em",
                textTransform: "uppercase",
                color: "rgba(161,161,170,0.7)",
                opacity: interpolate(spring({ frame: frame - 565 - 90, fps, config: { damping: 200 } }), [0, 1], [0, 1]),
              }}
            >
              KODOX · Digital Studio
            </div>
          </div>
        </AbsoluteFill>
      )}

      {/* Captions */}
      <Caption text="¿Y si el problema no es tu trabajo?" frame={frame} fps={fps} enter={5} exit={55} isV={isV} />
      <Caption text="Es todo lo que pasa alrededor." frame={frame} fps={fps} enter={55} exit={90} isV={isV} />
      <Caption text="Mensajes que se pierden. Preguntas repetidas." frame={frame} fps={fps} enter={95} exit={145} isV={isV} />
      <Caption text="Tiempo que se va en tareas simples." frame={frame} fps={fps} enter={145} exit={200} isV={isV} />
      <Caption text="Un cliente escribe… y la respuesta llega tarde." frame={frame} fps={fps} enter={210} exit={265} isV={isV} />
      <Caption text="No por descuido, sino por falta de claridad." frame={frame} fps={fps} enter={265} exit={325} isV={isV} />
      <Caption text="Cuando la información es clara, todo fluye mejor." frame={frame} fps={fps} enter={335} exit={395} isV={isV} />
      <Caption text="Menos fricción. Menos estrés." frame={frame} fps={fps} enter={395} exit={445} isV={isV} />
      <Caption text="Organizar la información es cuidar el tiempo de los demás." frame={frame} fps={fps} enter={450} exit={520} isV={isV} />
      <Caption text="La tecnología debería simplificar la vida, no complicarla." frame={frame} fps={fps} enter={570} exit={660} isV={isV} />
      <Caption text="Claridad. Orden. Tranquilidad." frame={frame} fps={fps} enter={665} exit={730} isV={isV} />

      {/* Progress bar */}
      <div style={{ position: "absolute", left: isV ? 56 : 80, right: isV ? 56 : 80, bottom: isV ? 64 : 40, height: 3, background: "rgba(255,255,255,0.08)", borderRadius: 999, overflow: "hidden" }}>
        <div style={{ height: "100%", width: `${(frame / (durationInFrames - 1)) * 100}%`, background: "linear-gradient(90deg,#6366F1,#22D3EE)", borderRadius: 999 }} />
      </div>

      {/* SFX - every second, escalating */}
      <Sfx name="bass" at={0} volume={0.55} />
      <Sfx name="hit" at={10} volume={0.45} />
      <Sfx name="whoosh" at={30} volume={0.32} />
      <Sfx name="pop" at={60} volume={0.3} />
      <Sfx name="tick" at={90} volume={0.22} />
      <Sfx name="whoosh-soft" at={120} volume={0.28} />
      <Sfx name="transition" at={150} volume={0.32} />
      <Sfx name="hit-soft" at={180} volume={0.3} />
      <Sfx name="pop" at={210} volume={0.3} />
      <Sfx name="reveal" at={240} volume={0.28} />
      <Sfx name="transition" at={270} volume={0.34} />
      <Sfx name="tick" at={300} volume={0.22} />
      <Sfx name="pop" at={330} volume={0.28} />
      <Sfx name="whoosh" at={360} volume={0.34} />
      <Sfx name="reveal" at={390} volume={0.3} />
      <Sfx name="pop" at={420} volume={0.32} />
      <Sfx name="hit" at={450} volume={0.38} />
      <Sfx name="reveal" at={480} volume={0.3} />
      <Sfx name="transition" at={510} volume={0.34} />
      <Sfx name="bass" at={540} volume={0.42} />
      <Sfx name="whoosh" at={570} volume={0.34} />
      <Sfx name="hit" at={600} volume={0.42} />
      <Sfx name="pop" at={630} volume={0.32} />
      <Sfx name="pop" at={660} volume={0.32} />
      <Sfx name="reveal" at={690} volume={0.34} />
      <Sfx name="bass" at={720} volume={0.4} />

      {/* Voiceover */}
      <Audio src={staticFile("audio/voiceover.mp3")} volume={1} />
    </AbsoluteFill>
  );
};
