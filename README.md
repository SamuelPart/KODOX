# KODOX — Digital Studio Trujillo

Sitio web oficial de marca personal y estudio de desarrollo web. React 18 + Vite + Tailwind CSS v3 + Framer Motion + Lucide React. Listo para desplegar en Vercel.

![Stack](https://img.shields.io/badge/React-18-61DAFB?style=flat-square&logo=react&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-5-646CFF?style=flat-square&logo=vite&logoColor=white)
![Tailwind](https://img.shields.io/badge/Tailwind-3-06B6D4?style=flat-square&logo=tailwindcss&logoColor=white)
![Framer Motion](https://img.shields.io/badge/Framer_Motion-11-E035C2?style=flat-square&logo=framer&logoColor=white)

---

## 1. Arranque rápido

```bash
npm install
npm run dev      # http://localhost:5173
npm run build    # estático en /dist
npm run preview  # sirve el build en http://localhost:4173
```

## 2. Todo se edita en un solo archivo

`src/data/portfolioData.js` contiene **todos** los textos, precios y enlaces del sitio:

| Bloque | Qué controla |
| --- | --- |
| `siteConfig.brand` | Nombre del sitio (`KODOX`), "Digital Studio Trujillo", tu nombre, métricas. |
| `siteConfig.contact` | WhatsApp (`51999999999`), correo, redes, horario, dominio. |
| `siteConfig.hero` | Badge, titular, subtítulo, KPIs y datos del mockup de celular. |
| `siteConfig.marquee` | Tecnologías y capacidades de la cinta infinita. |
| `siteConfig.portfolio` | Filtros + los 6 proyectos con `url` real de Vercel, imágenes de Unsplash y tags. |
| `siteConfig.services` | Bento grid de los 6+ servicios. |
| `siteConfig.process` | Los 4 pasos del proceso con tiempos y entregables. |
| `siteConfig.pricing` | Planes en Soles (S/ 150 / 250 / 400), características y garantías. |
| `siteConfig.quoteModal` | Opciones del cotizador (tipo de negocio, necesidad, presupuesto, plazos). |
| `siteConfig.faq` | Acordeón de preguntas frecuentes. |
| `siteConfig.footer` | CTA final, columnas de enlaces y textos legales. |

### Cambiar el número de WhatsApp

```js
// src/data/portfolioData.js
contact: {
  whatsapp: '51987654321',        // formato internacional, sin "+" ni espacios
  whatsappDisplay: '+51 987 654 321',
}
```

Todos los botones (hero, navbar, tarjetas, planes, cotizador, footer y botón flotante) usan `buildWhatsAppLink()` y se actualizan automáticamente.

### Cambiar los enlaces de las demos

```js
projects: [
  { id: 'wira', url: 'https://cevicheria-wira.vercel.app', /* ... */ },
]
```

### Cambiar el nombre de la marca

```js
brand: { name: 'KODOX', studio: 'Digital Studio Trujillo' }
```

También actualiza `<title>` y el favicon en `index.html` si quieres que coincida al 100 %.

## 3. Dirección de arte

- **Fondo:** negro obsidiana `#050507` con malla `bg-grid`, resplandores radiales índigo `#4F46E5` y cian `#06B6D4`.
- **Tarjetas:** vidrio oscuro `bg-zinc-900/60 backdrop-blur-xl border-white/10` con borde iluminado al hover.
- **Acentos:** degradado `indigo → purple → cyan` y verde WhatsApp `#16A34A` para conversión.
- **Tipografías:** Space Grotesk (títulos), Inter (texto), JetBrains Mono (badges técnicos).
- **Scrollbar oscuro**, glassmorphism, noise sutil y `prefers-reduced-motion` respetado.

## 4. Motion implementado

| Requisito | Dónde |
| --- | --- |
| Barra de progreso con `useScroll` + `useSpring` | `Navbar.jsx` + `ReadingProgress` en `App.jsx` |
| Titular palabra por palabra con máscara | `HeroSection.jsx` → `MaskedHeadline` |
| Orbes flotantes con `y`/`scale` en bucle | `HeroSection.jsx` |
| Mockup 3D interactivo (tilt con `useMotionValue` + `useSpring`) | `HeroSection.jsx` → `PhoneMockup` |
| Parallax del hero | `useScroll` + `useTransform` en `HeroSection.jsx` |
| Marquee infinito sin cortes | `TechMarquee.jsx` (dos filas, una en reversa, pausa al hover) |
| Filtros con `layoutId="portfolioFilterPill"` | `PortfolioShowcase.jsx` |
| `AnimatePresence mode="popLayout"` | `PortfolioShowcase.jsx` |
| Línea de progreso dibujada al hacer scroll | `ProcessTimeline.jsx` (`useScroll` vinculado al contenedor) |
| Microinteracciones `whileHover={{ y: -6 }}` / `whileTap={{ scale: 0.96 }}` | Todas las tarjetas y botones |
| Acordeón animado con altura automática | `FaqSection.jsx` |
| Modal con pasos, progreso y resumen | `InteractiveQuoteModal.jsx` |

## 5. Cotizador por WhatsApp

El botón "Cotizar mi Web" (navbar), "Quiero una así" (cada proyecto), "Cotizar este servicio" y los planes abren el modal con los datos preseleccionados. Al enviar, se genera un mensaje estructurado con negocio, necesidad, presupuesto, plazo, nombre y detalles, y se abre WhatsApp.

## 6. Despliegue en Vercel

1. Sube el repositorio a GitHub.
2. En Vercel → **Add New Project** → importa el repo.
3. Framework preset: **Vite**. Build: `npm run build`. Output: `dist`.
4. Deploy. `vercel.json` ya incluye rewrites SPA, cache de assets y cabeceras de seguridad.

## 7. Estructura

```
├── index.html                  # Fuentes, SEO, favicon SVG
├── package.json
├── tailwind.config.js
├── postcss.config.js
├── vite.config.js
├── vercel.json
└── src
    ├── App.jsx                 # Composición + botones flotantes + modal
    ├── index.css               # Grid, glass, botones, scrollbar oscuro
    ├── main.jsx
    ├── data
    │   └── portfolioData.js    # ⚙️ TODO lo editable
    └── components
        ├── SectionPrimitives.jsx   # Reveal, SectionHeading, helpers de motion
        ├── Navbar.jsx
        ├── HeroSection.jsx
        ├── TechMarquee.jsx
        ├── PortfolioShowcase.jsx
        ├── ServicesGrid.jsx
        ├── ProcessTimeline.jsx
        ├── PricingPlans.jsx
        ├── InteractiveQuoteModal.jsx
        ├── FaqSection.jsx
        └── Footer.jsx
```

> `SectionPrimitives.jsx` es el único archivo extra del plan original: contiene los componentes de revelado y encabezados reutilizados por todas las secciones para evitar duplicar código.
