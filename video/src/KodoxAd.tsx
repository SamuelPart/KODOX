import React from "react";
import {
  AbsoluteFill,
  interpolate,
  spring,
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
  line: "rgba(255,255,255,0.08)",
};

const fontDisplay = '"Inter", "Segoe UI", "Helvetica Neue", Arial, sans-serif';
const fontBody = '"Inter", "Segoe UI", "Helvetica Neue", Arial, sans-serif';

type SceneProps = {
  frame: number;
  fps: number;
  width: number;
  height: number;
  isVertical: boolean;
};

const FadeSlide: React.FC<{
  children: React.ReactNode;
  delay: number;
  frame: number;
  fps: number;
  fromY?: number;
}> = ({ children, delay, frame, fps, fromY = 28 }) => {
  const p = spring({ frame: frame - delay, fps, config: { damping: 200 } });
  const opacity = interpolate(p, [0, 1], [0, 1]);
  const y = interpolate(p, [0, 1], [fromY, 0]);
  return <div style={{ opacity, transform: `translateY(${y}px)` }}>{children}</div>;
};

const Background: React.FC<{ frame: number; width: number; height: number }> = ({
  frame,
  width,
  height,
}) => {
  const drift = interpolate(frame, [0, 750], [0, 40]);
  return (
    <AbsoluteFill style={{ backgroundColor: palette.bg, overflow: "hidden" }}>
      <div
        style={{
          position: "absolute",
          inset: 0,
          backgroundImage: `
            radial-gradient(ellipse 60% 45% at ${20 + drift * 0.2}% ${15 + drift * 0.1}%, rgba(99,102,241,0.22), transparent 60%),
            radial-gradient(ellipse 55% 40% at ${80 - drift * 0.15}% ${20 + drift * 0.05}%, rgba(34,211,238,0.14), transparent 62%),
            radial-gradient(ellipse 70% 55% at 50% ${90 - drift * 0.1}%, rgba(139,92,246,0.14), transparent 68%)
          `,
        }}
      />
      <div
        style={{
          position: "absolute",
          inset: 0,
          backgroundImage:
            "linear-gradient(to right, rgba(255,255,255,0.035) 1px, transparent 1px), linear-gradient(to bottom, rgba(255,255,255,0.035) 1px, transparent 1px)",
          backgroundSize: "64px 64px",
          opacity: 0.55,
        }}
      />
      <div
        style={{
          position: "absolute",
          top: -height * 0.2,
          right: -width * 0.1,
          width: width * 0.45,
          height: width * 0.45,
          borderRadius: "50%",
          border: "1px solid rgba(99,102,241,0.12)",
          transform: `rotate(${drift * 0.8}deg)`,
        }}
      />
      <div
        style={{
          position: "absolute",
          bottom: -height * 0.25,
          left: -width * 0.08,
          width: width * 0.4,
          height: width * 0.4,
          borderRadius: "50%",
          border: "1px solid rgba(34,211,238,0.1)",
          transform: `rotate(${-drift * 0.5}deg)`,
        }}
      />
    </AbsoluteFill>
  );
};

const SceneLabel: React.FC<{ text: string }> = ({ text }) => (
  <div
    style={{
      display: "inline-flex",
      alignItems: "center",
      gap: 10,
      padding: "10px 18px",
      borderRadius: 999,
      border: "1px solid rgba(99,102,241,0.3)",
      background: "rgba(99,102,241,0.08)",
      color: "#A5B4FC",
      fontFamily: fontBody,
      fontSize: 14,
      fontWeight: 600,
      letterSpacing: "0.18em",
      textTransform: "uppercase",
    }}
  >
    <span
      style={{
        width: 7,
        height: 7,
        borderRadius: "50%",
        background: palette.cyan,
        boxShadow: "0 0 12px rgba(34,211,238,0.9)",
      }}
    />
    {text}
  </div>
);

const ProblemCard: React.FC<{
  title: string;
  detail: string;
  accent: string;
  index: number;
  frame: number;
  fps: number;
}> = ({ title, detail, accent, index, frame, fps }) => {
  const delay = 40 + index * 18;
  const p = spring({ frame: frame - delay, fps, config: { damping: 200 } });
  const opacity = interpolate(p, [0, 1], [0, 1]);
  const y = interpolate(p, [0, 1], [36, 0]);
  const scale = interpolate(p, [0, 1], [0.96, 1]);
  return (
    <div
      style={{
        opacity,
        transform: `translateY(${y}px) scale(${scale})`,
        padding: "28px 28px 26px",
        borderRadius: 24,
        border: "1px solid rgba(255,255,255,0.1)",
        background: "rgba(16,16,24,0.72)",
        backdropFilter: "blur(10px)",
        boxShadow: "0 30px 80px -40px rgba(0,0,0,0.9)",
        flex: 1,
        minWidth: 0,
      }}
    >
      <div
        style={{
          width: 44,
          height: 44,
          borderRadius: 14,
          background: `linear-gradient(135deg, ${accent}, rgba(255,255,255,0.06))`,
          border: "1px solid rgba(255,255,255,0.12)",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          marginBottom: 20,
          fontSize: 20,
        }}
      >
        <span style={{ color: palette.white, fontWeight: 700, fontFamily: fontDisplay }}>
          {index + 1}
        </span>
      </div>
      <div
        style={{
          fontFamily: fontDisplay,
          fontSize: 28,
          lineHeight: 1.2,
          fontWeight: 600,
          color: palette.white,
          letterSpacing: "-0.02em",
          marginBottom: 12,
        }}
      >
        {title}
      </div>
      <div
        style={{
          fontFamily: fontBody,
          fontSize: 18,
          lineHeight: 1.6,
          color: palette.muted,
        }}
      >
        {detail}
      </div>
    </div>
  );
};

const SceneIntro: React.FC<SceneProps> = ({ frame, fps, isVertical }) => {
  const titleSize = isVertical ? 72 : 88;
  const subSize = isVertical ? 30 : 32;
  return (
    <AbsoluteFill
      style={{
        justifyContent: "center",
        padding: isVertical ? "120px 72px" : "0 140px",
      }}
    >
      <FadeSlide delay={0} frame={frame} fps={fps}>
        <SceneLabel text="Una realidad cotidiana" />
      </FadeSlide>
      <div style={{ height: isVertical ? 36 : 40 }} />
      <FadeSlide delay={10} frame={frame} fps={fps} fromY={36}>
        <div
          style={{
            fontFamily: fontDisplay,
            fontSize: titleSize,
            lineHeight: 1.05,
            fontWeight: 700,
            letterSpacing: "-0.04em",
            color: palette.white,
            maxWidth: 1200,
          }}
        >
          Hay problemas que
          <br />
          <span
            style={{
              background: "linear-gradient(100deg, #818cf8 0%, #a78bfa 40%, #22d3ee 100%)",
              WebkitBackgroundClip: "text",
              color: "transparent",
            }}
          >
            se repiten todos los días.
          </span>
        </div>
      </FadeSlide>
      <div style={{ height: isVertical ? 32 : 36 }} />
      <FadeSlide delay={22} frame={frame} fps={fps}>
        <div
          style={{
            fontFamily: fontBody,
            fontSize: subSize,
            lineHeight: 1.55,
            color: palette.muted,
            maxWidth: 900,
          }}
        >
          No por falta de esfuerzo, sino porque la información no siempre
          llega clara, a tiempo y en el lugar correcto.
        </div>
      </FadeSlide>
    </AbsoluteFill>
  );
};

const SceneProblems: React.FC<SceneProps> = ({ frame, fps, isVertical }) => {
  const problems = [
    {
      title: "Mensajes que se pierden",
      detail:
        "Consultas importantes quedan enterradas entre chats, llamadas y anotaciones sueltas.",
      accent: "rgba(99,102,241,0.35)",
    },
    {
      title: "Clientes que esperan",
      detail:
        "Cuando la información no está a mano, responder tarde se vuelve parte de la rutina.",
      accent: "rgba(34,211,238,0.28)",
    },
    {
      title: "Procesos que cansan",
      detail:
        "Tareas simples terminan consumiendo tiempo que podría dedicarse a lo que realmente importa.",
      accent: "rgba(139,92,246,0.3)",
    },
  ];

  return (
    <AbsoluteFill
      style={{
        justifyContent: "center",
        padding: isVertical ? "100px 56px" : "0 110px",
      }}
    >
      <FadeSlide delay={0} frame={frame} fps={fps}>
        <SceneLabel text="Lo que vemos a menudo" />
      </FadeSlide>
      <div style={{ height: isVertical ? 30 : 34 }} />
      <FadeSlide delay={8} frame={frame} fps={fps}>
        <div
          style={{
            fontFamily: fontDisplay,
            fontSize: isVertical ? 58 : 64,
            lineHeight: 1.1,
            fontWeight: 700,
            letterSpacing: "-0.035em",
            color: palette.white,
            maxWidth: 980,
          }}
        >
          Pequeñas fricciones, grandes consecuencias.
        </div>
      </FadeSlide>
      <div style={{ height: isVertical ? 42 : 52 }} />
      <div
        style={{
          display: "flex",
          flexDirection: isVertical ? "column" : "row",
          gap: isVertical ? 20 : 24,
        }}
      >
        {problems.map((problem, index) => (
          <ProblemCard
            key={problem.title}
            {...problem}
            index={index}
            frame={frame}
            fps={fps}
          />
        ))}
      </div>
    </AbsoluteFill>
  );
};

const SceneShift: React.FC<SceneProps> = ({ frame, fps, isVertical }) => {
  const lineWidth = interpolate(frame, [20, 90], [0, 100], {
    extrapolateLeft: "clamp",
    extrapolateRight: "clamp",
    easing: Easing.out(Easing.cubic),
  });

  return (
    <AbsoluteFill
      style={{
        justifyContent: "center",
        alignItems: "center",
        padding: isVertical ? "120px 64px" : "0 160px",
        textAlign: "center",
      }}
    >
      <FadeSlide delay={0} frame={frame} fps={fps}>
        <SceneLabel text="Un cambio de enfoque" />
      </FadeSlide>
      <div style={{ height: isVertical ? 44 : 52 }} />
      <FadeSlide delay={12} frame={frame} fps={fps} fromY={40}>
        <div
          style={{
            fontFamily: fontDisplay,
            fontSize: isVertical ? 66 : 82,
            lineHeight: 1.08,
            fontWeight: 700,
            letterSpacing: "-0.04em",
            color: palette.white,
            maxWidth: 1100,
          }}
        >
          Cuando la información es clara,
          <br />
          <span style={{ color: "#A5B4FC" }}>las personas toman mejores decisiones.</span>
        </div>
      </FadeSlide>
      <div style={{ height: 34 }} />
      <div
        style={{
          width: `${lineWidth}%`,
          maxWidth: 420,
          height: 2,
          background: "linear-gradient(90deg, transparent, #6366F1, #22D3EE, transparent)",
          borderRadius: 999,
        }}
      />
      <div style={{ height: 34 }} />
      <FadeSlide delay={28} frame={frame} fps={fps}>
        <div
          style={{
            fontFamily: fontBody,
            fontSize: isVertical ? 28 : 30,
            lineHeight: 1.6,
            color: palette.muted,
            maxWidth: 860,
          }}
        >
          La claridad reduce la fricción. Y cuando hay menos fricción,
          hay más tiempo para atender, crear y avanzar.
        </div>
      </FadeSlide>
    </AbsoluteFill>
  );
};

const SceneClosing: React.FC<SceneProps> = ({ frame, fps, isVertical }) => {
  const markScale = spring({ frame: frame - 10, fps, config: { damping: 200 } });
  const markOpacity = interpolate(markScale, [0, 1], [0, 1]);

  return (
    <AbsoluteFill
      style={{
        justifyContent: "center",
        alignItems: "center",
        padding: isVertical ? "120px 64px" : "0 160px",
        textAlign: "center",
      }}
    >
      <div
        style={{
          opacity: markOpacity,
          transform: `scale(${interpolate(markScale, [0, 1], [0.92, 1])})`,
          marginBottom: 36,
        }}
      >
        <div
          style={{
            width: 88,
            height: 88,
            borderRadius: 28,
            border: "1px solid rgba(255,255,255,0.12)",
            background:
              "linear-gradient(145deg, rgba(99,102,241,0.22), rgba(16,16,24,0.85) 55%, rgba(34,211,238,0.18))",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            boxShadow: "0 30px 80px -30px rgba(99,102,241,0.65)",
          }}
        >
          <div
            style={{
              fontFamily: fontDisplay,
              fontWeight: 700,
              fontSize: 34,
              letterSpacing: "-0.04em",
              color: palette.white,
            }}
          >
            K
          </div>
        </div>
      </div>

      <FadeSlide delay={14} frame={frame} fps={fps} fromY={34}>
        <div
          style={{
            fontFamily: fontDisplay,
            fontSize: isVertical ? 62 : 76,
            lineHeight: 1.1,
            fontWeight: 700,
            letterSpacing: "-0.04em",
            color: palette.white,
            maxWidth: 1050,
          }}
        >
          La tecnología debería
          <br />
          <span
            style={{
              background: "linear-gradient(100deg, #818cf8 0%, #a78bfa 40%, #22d3ee 100%)",
              WebkitBackgroundClip: "text",
              color: "transparent",
            }}
          >
            simplificar la vida,
          </span>
          <br />
          no complicarla.
        </div>
      </FadeSlide>

      <div style={{ height: 40 }} />
      <FadeSlide delay={30} frame={frame} fps={fps}>
        <div
          style={{
            fontFamily: fontBody,
            fontSize: isVertical ? 24 : 26,
            lineHeight: 1.6,
            color: palette.dim,
            maxWidth: 760,
          }}
        >
          Diseñar con claridad es una forma de respetar el tiempo de las personas.
        </div>
      </FadeSlide>

      <div style={{ height: 52 }} />
      <FadeSlide delay={44} frame={frame} fps={fps}>
        <div
          style={{
            fontFamily: fontBody,
            fontSize: 15,
            letterSpacing: "0.24em",
            textTransform: "uppercase",
            color: "rgba(161,161,170,0.75)",
          }}
        >
          KODOX · Digital Studio
        </div>
      </FadeSlide>
    </AbsoluteFill>
  );
};

export const KodoxAd: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps, width, height, durationInFrames } = useVideoConfig();
  const isVertical = height > width;

  const sceneLength = Math.floor(durationInFrames / 4);
  const sceneIndex = Math.min(Math.floor(frame / sceneLength), 3);
  const sceneFrame = frame - sceneIndex * sceneLength;

  const fadeIn = interpolate(sceneFrame, [0, 18], [0, 1], {
    extrapolateLeft: "clamp",
    extrapolateRight: "clamp",
  });
  const fadeOut = interpolate(
    sceneFrame,
    [sceneLength - 18, sceneLength],
    [1, 0],
    { extrapolateLeft: "clamp", extrapolateRight: "clamp" }
  );
  const sceneOpacity = Math.min(fadeIn, sceneIndex === 3 ? 1 : fadeOut);

  const scenes = [SceneIntro, SceneProblems, SceneShift, SceneClosing];
  const ActiveScene = scenes[sceneIndex];

  return (
    <AbsoluteFill style={{ backgroundColor: palette.bg, fontFamily: fontBody }}>
      <Background frame={frame} width={width} height={height} />
      <AbsoluteFill style={{ opacity: sceneOpacity }}>
        <ActiveScene
          frame={sceneFrame}
          fps={fps}
          width={width}
          height={height}
          isVertical={isVertical}
        />
      </AbsoluteFill>

      {/* subtle progress line */}
      <div
        style={{
          position: "absolute",
          left: isVertical ? 48 : 72,
          right: isVertical ? 48 : 72,
          bottom: isVertical ? 56 : 44,
          height: 2,
          background: "rgba(255,255,255,0.08)",
          borderRadius: 999,
          overflow: "hidden",
        }}
      >
        <div
          style={{
            height: "100%",
            width: `${(frame / (durationInFrames - 1)) * 100}%`,
            background: "linear-gradient(90deg, #6366F1, #22D3EE)",
            borderRadius: 999,
          }}
        />
      </div>
    </AbsoluteFill>
  );
};
