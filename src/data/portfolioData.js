/* ============================================================================
   KODOX · Digital Studio Trujillo
   ----------------------------------------------------------------------------
   ESTE ES EL ÚNICO ARCHIVO QUE NECESITAS EDITAR PARA PERSONALIZAR TODO EL SITIO.
   Cambia tu nombre, tu número de WhatsApp, los enlaces de tus demos, precios,
   servicios, proceso y preguntas frecuentes. Nada más.
   ========================================================================== */

export const siteConfig = {
  /* ---------------------------------------------------------------- MARCA */
  brand: {
    name: 'KODOX', // Nombre del sitio / estudio (editable)
    studio: 'Digital Studio Trujillo',
    tagline: 'Webs, apps e IA que venden',
    shortDescription:
      'Estudio de desarrollo web y producto digital en Trujillo, Perú. Creamos experiencias ultrarrápidas para negocios que quieren vender más.',
    owner: 'Samuel Part', // Tu nombre real / de marca personal
    role: 'Principal Creative Developer & Lead UI/UX Designer',
    location: 'Trujillo, La Libertad — Perú',
    experienceYears: '4+',
    projectsDelivered: '40+',
    deliveryTime: '24 – 72 h',
  },

  /* ------------------------------------------------------------- CONTACTO */
  contact: {
    // Número en formato internacional SIN "+" y SIN espacios.
    whatsapp: '51999999999',
    whatsappDisplay: '+51 999 999 999',
    email: 'hola@kodox.studio',
    instagram: 'https://instagram.com/kodox.studio',
    github: 'https://github.com/SamuelPart',
    linkedin: 'https://linkedin.com/in/kodox',
    tiktok: 'https://tiktok.com/@kodox.studio',
    schedule: 'Lun a Sáb · 9:00 a 21:00 (hora Perú)',
    domain: 'https://kodox.vercel.app',
  },

  /* ------------------------------------------------------ NAVEGACIÓN / CTA */
  navigation: [
    { label: 'Proyectos', href: '#proyectos' },
    { label: 'Servicios', href: '#servicios' },
    { label: 'Proceso', href: '#proceso' },
    { label: 'Planes', href: '#planes' },
    { label: 'FAQ', href: '#faq' },
  ],

  ctaLabels: {
    nav: 'Cotizar mi Web',
    heroPrimary: 'Ver Demos en Vivo',
    heroSecondary: 'Hablar por WhatsApp',
    demo: 'Probar Demo en Vivo',
    wantThis: 'Quiero una así',
    quote: 'Cotizar por WhatsApp',
  },

  /* ------------------------------------------------------------------ HERO */
  hero: {
    badge: 'Disponible para nuevos proyectos en Trujillo y todo el Perú',
    titleLines: [
      ['Transformo', 'negocios', 'locales'],
      ['en', 'máquinas', 'de'],
      ['ventas', 'digitales.'],
    ],
    titlePlain: 'Transformo negocios locales en máquinas de ventas digitales.',
    subtitle:
      'Desarrollo páginas web ultrarrápidas, catálogos interactivos con pedidos a WhatsApp, aplicaciones Android e integraciones con IA 100% a medida. Pago único, sin mensualidades.',
    secondaryNote: 'Respuesta en menos de 1 hora · Sin adelantos para cotizar',
    stats: [
      { value: '40+', label: 'Proyectos entregados' },
      { value: '24–72 h', label: 'Tiempo de entrega' },
      { value: '100%', label: 'Pago único' },
      { value: 'S/ 150', label: 'Desde, sin mensualidades' },
    ],
    phoneMockup: {
      appName: 'Pedidos en vivo',
      storeName: 'Cevichería Wira',
      status: 'Abierto · Delivery activo',
      notifications: [
        {
          source: 'WhatsApp',
          icon: 'whatsapp',
          title: 'Nuevo pedido #A-128',
          detail: '2× Ceviche mixto · 1× Chicha 1L',
          amount: 'S/ 64.00',
          method: 'Yape',
          time: 'ahora',
        },
        {
          source: 'Yape',
          icon: 'yape',
          title: 'Pago confirmado',
          detail: 'Marcos R. · Mesa 4',
          amount: 'S/ 38.50',
          method: 'Yape',
          time: 'hace 1 min',
        },
        {
          source: 'WhatsApp',
          icon: 'whatsapp',
          title: 'Nuevo pedido #A-127',
          detail: '1× Arroz con mariscos · Delivery',
          amount: 'S/ 32.00',
          method: 'Efectivo',
          time: 'hace 3 min',
        },
      ],
      totalToday: 'S/ 1,284.00',
      ordersToday: 37,
      chart: [32, 48, 40, 62, 55, 74, 68, 88, 79, 96],
    },
  },

  /* ------------------------------------------------------------ MARQUEE */
  marquee: {
    top: [
      'React + Vite',
      'Apps Android',
      'Cartas Digitales QR',
      'IA Integrada',
      'Tiendas Online',
      'Pedidos WhatsApp',
      'Diseño a Medida',
      'Sin Mensualidades',
    ],
    bottom: [
      'Tailwind CSS',
      'Framer Motion',
      'Node & APIs',
      'Supabase',
      'Vercel Edge',
      'Google Sheets API',
      'MediaPipe IA',
      'Pagos Yape / Plin',
    ],
  },

  /* ---------------------------------------------------------- PORTAFOLIO */
  portfolio: {
    eyebrow: 'Portafolio',
    title: 'Trabajos y Demos Interactivas',
    subtitle:
      'No son capturas de pantalla: son demos reales funcionando en Vercel. Toca cualquiera y pruébala como si fueras tu cliente.',
    filters: [
      { id: 'all', label: 'Todos' },
      { id: 'gastronomia', label: 'Gastronomía & Cartas QR' },
      { id: 'moda', label: 'Moda & Boutiques' },
      { id: 'fitness', label: 'Fitness & Salud' },
      { id: 'belleza', label: 'Belleza & Spa' },
      { id: 'apps', label: 'Apps & IA' },
    ],
    projects: [
      {
        id: 'wira',
        title: 'Cevichería Wira',
        subtitle: 'Carta Digital QR & Delivery',
        category: 'gastronomia',
        categoryLabel: 'Gastronomía & Cartas QR',
        description:
          'Carta con 40 platos, selector de ají, pedidos en mesa o delivery y cálculo automático de vuelto.',
        image:
          'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?auto=format&fit=crop&w=1400&q=80',
        alt: 'Plato de ceviche peruano servido en mesa',
        url: 'https://cevicheria-wira.vercel.app', // <-- reemplaza con tu URL real
        tags: ['40 platos', 'Selector de ají', 'Mesa / Delivery', 'Cálculo de vuelto'],
        tech: ['React', 'Vite', 'Tailwind', 'WhatsApp API'],
        accent: 'from-cyan-500/25 via-indigo-500/15 to-transparent',
        quoteType: 'Carta digital QR con pedidos a WhatsApp',
        metrics: [
          { value: '40', label: 'platos' },
          { value: '< 1 s', label: 'carga' },
          { value: '+38%', label: 'pedidos' },
        ],
        featured: true,
        year: '2025',
      },
      {
        id: 'alitas-barrio',
        title: 'Alitas Barrio',
        subtitle: 'Gastrobar & Pedidos Nocturnos',
        category: 'gastronomia',
        categoryLabel: 'Gastronomía & Cartas QR',
        description:
          'Sistema dark-mode con fotos reales, combos por nivel de picante y cierre directo a WhatsApp.',
        image:
          'https://images.unsplash.com/photo-1567620832903-9fc6debc209f?auto=format&fit=crop&w=1400&q=80',
        alt: 'Alitas de pollo glaseadas en bar oscuro',
        url: 'https://alitas-barrio.vercel.app',
        tags: ['Dark mode', 'Combos', 'Nivel de picante', 'Pedido nocturno'],
        tech: ['React', 'Tailwind', 'Framer Motion', 'WhatsApp API'],
        accent: 'from-amber-500/25 via-orange-500/12 to-transparent',
        quoteType: 'Gastrobar con pedidos por WhatsApp',
        metrics: [
          { value: '12', label: 'combos' },
          { value: '100%', label: 'móvil' },
          { value: '×2', label: 'ticket' },
        ],
        featured: false,
        year: '2025',
      },
      {
        id: 'alvina',
        title: 'Alviña Primavera',
        subtitle: 'Panadería & Cafetería',
        category: 'gastronomia',
        categoryLabel: 'Gastronomía & Cartas QR',
        description:
          'Catálogo cálido para pedidos rápidos por QR: panes, postres y café listos para recojo.',
        image:
          'https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=1400&q=80',
        alt: 'Panes artesanales recién horneados',
        url: 'https://alvina-primavera.vercel.app',
        tags: ['Catálogo QR', 'Recojo en tienda', 'Horarios', 'Nov promociones'],
        tech: ['React', 'Vite', 'Tailwind', 'Google Sheets'],
        accent: 'from-orange-400/22 via-rose-400/10 to-transparent',
        quoteType: 'Catálogo digital con pedidos por WhatsApp',
        metrics: [
          { value: '60+', label: 'productos' },
          { value: '2 s', label: 'pedido' },
          { value: '24/7', label: 'carta activa' },
        ],
        featured: false,
        year: '2024',
      },
      {
        id: 'activewear',
        title: 'Activewear & Boutique Store',
        subtitle: 'Catálogo Moda & Envíos',
        category: 'moda',
        categoryLabel: 'Moda & Boutiques',
        description:
          'Tienda minimalista con selector de talla, color y envío nacional por Shalom.',
        image:
          'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1400&q=80',
        alt: 'Prendas de moda colgadas en boutique minimalista',
        url: 'https://activewear-boutique.vercel.app',
        tags: ['Tallas & colores', 'Carrito WhatsApp', 'Shalom nacional', 'Stock editable'],
        tech: ['React', 'Tailwind', 'Framer Motion', 'Supabase'],
        accent: 'from-fuchsia-500/22 via-purple-500/12 to-transparent',
        quoteType: 'Tienda con carrito de WhatsApp',
        metrics: [
          { value: 'S/ 0', label: 'mensualidad' },
          { value: '24 h', label: 'envíos' },
          { value: '+54%', label: 'ventas' },
        ],
        featured: true,
        year: '2025',
      },
      {
        id: 'barberia-patron',
        title: 'Barbería El Patrón',
        subtitle: 'Asesor de Corte con IA',
        category: 'belleza',
        categoryLabel: 'Belleza & Spa',
        description:
          'Escáner facial en vivo con MediaPipe que detecta el tipo de rostro y agenda tu cita.',
        image:
          'https://images.unsplash.com/photo-1503951914875-452162b0f3f1?auto=format&fit=crop&w=1400&q=80',
        alt: 'Barbero cortando el cabello a un cliente',
        url: 'https://barberia-el-patron.vercel.app',
        tags: ['Escáner facial IA', 'MediaPipe', 'Agenda citas', 'Recomendación de corte'],
        tech: ['React', 'MediaPipe', 'TensorFlow.js', 'Tailwind'],
        accent: 'from-indigo-500/25 via-sky-500/12 to-transparent',
        quoteType: 'Web con integración de IA',
        metrics: [
          { value: 'IA', label: 'en navegador' },
          { value: '0 app', label: 'que instalar' },
          { value: '+62%', label: 'reservas' },
        ],
        featured: true,
        year: '2025',
      },
      {
        id: 'cadena-fitness',
        title: 'Cadena Fitness Multisede',
        subtitle: 'Portal & Calculadora IMC',
        category: 'fitness',
        categoryLabel: 'Fitness & Salud',
        description:
          'Web corporativa con calculadora de IMC, horarios de clases y enrutamiento por sede.',
        image:
          'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?auto=format&fit=crop&w=1400&q=80',
        alt: 'Gimnasio moderno con máquinas de entrenamiento',
        url: 'https://cadena-fitness.vercel.app',
        tags: ['Calculadora IMC', 'Horarios de clases', 'Enrutamiento por sede', 'Planes'],
        tech: ['React', 'Vite', 'Tailwind', 'Framer Motion'],
        accent: 'from-emerald-500/22 via-teal-500/12 to-transparent',
        quoteType: 'Web corporativa multisede',
        metrics: [
          { value: '4', label: 'sedes' },
          { value: '18', label: 'clases/sem' },
          { value: '+2.1k', label: 'leads' },
        ],
        featured: false,
        year: '2024',
      },
    ],
  },

  /* ------------------------------------------------------------ SERVICIOS */
  services: {
    eyebrow: 'Servicios',
    title: 'Lo que construyo a tu medida',
    subtitle:
      'Cada proyecto se diseña desde cero para tu marca. Nada de plantillas genéricas ni plugins que hacen lenta tu web.',
    items: [
      {
        id: 'cartas-qr',
        icon: 'QrCode',
        title: 'Cartas Digitales & Menús QR',
        description:
          'Tu carta siempre actualizada, sin reimprimir. El cliente escanea, elige y su pedido llega ordenado a tu WhatsApp.',
        idealFor: 'Restaurantes, cevicherías, pollerías y cafés',
        bullets: ['QR listo para imprimir', 'Fotos y precios editables', 'Pedidos a mesa o delivery'],
        span: 'lg:col-span-3',
        featured: true,
        badge: 'Más pedido',
      },
      {
        id: 'tiendas',
        icon: 'ShoppingBag',
        title: 'Tiendas & Catálogos con Carrito WhatsApp',
        description:
          'Tu catálogo completo con variantes de talla y color, carrito inteligente y checkout que abre WhatsApp con el pedido armado.',
        idealFor: 'Ropa, boutiques, importadoras y emprendimientos',
        bullets: ['Variantes talla / color', 'Envíos nacionales', 'Stock y precios desde tu celular'],
        span: 'lg:col-span-3',
        featured: true,
        badge: 'Alta conversión',
      },
      {
        id: 'corporativas',
        icon: 'MonitorSmartphone',
        title: 'Webs Corporativas & Landing Pages a Medida',
        description:
          'Diseño 100% personalizado desde cero o rediseño completo de tu web antigua y lenta, con foco en claridad y velocidad.',
        idealFor: 'Empresas, consultoras, clínicas y profesionales',
        bullets: ['Diseño a medida', 'SEO y velocidad', 'Formularios y WhatsApp'],
        span: 'lg:col-span-2',
        featured: false,
        badge: null,
      },
      {
        id: 'ia',
        icon: 'Sparkles',
        title: 'Integraciones con Inteligencia Artificial',
        description:
          'Asesores virtuales, escáner facial para barberías y salones, calculadoras interactivas que recomiendan productos.',
        idealFor: 'Negocios que quieren diferenciarse con tecnología',
        bullets: ['IA en el navegador', 'Sin apps extra', 'Costo cero por uso'],
        span: 'lg:col-span-2',
        featured: false,
        badge: 'IA integrada',
      },
      {
        id: 'android',
        icon: 'Smartphone',
        title: 'Aplicaciones Android a Medida',
        description:
          'Apps internas de pedidos, inventarios o catálogos offline para tu equipo, con la identidad visual de tu negocio.',
        idealFor: 'Negocios con operación diaria y varias sedes',
        bullets: ['Pedidos e inventario', 'Catálogo offline', 'Instalación guiada'],
        span: 'lg:col-span-2',
        featured: false,
        badge: null,
      },
      {
        id: 'panel',
        icon: 'LayoutDashboard',
        title: 'Panel Autoadministrable (Google Sheets / Admin)',
        description:
          'Cambia tus propios precios, fotos y stock desde tu celular sin saber programar. Tú tienes el control total.',
        idealFor: 'Todo negocio que no quiere depender de un técnico',
        bullets: ['Google Sheets o panel propio', 'Actualización instantánea', 'Capacitación incluida'],
        span: 'lg:col-span-3',
        featured: true,
        badge: 'Autonomía total',
      },
      {
        id: 'extras',
        icon: 'Gauge',
        title: 'Velocidad, SEO & Soporte Post-entrega',
        description:
          'Optimización de imágenes, Core Web Vitals, metadatos para Google y 15 días de soporte sin costo tras la entrega.',
        idealFor: 'Webs que cargan lento o no aparecen en Google',
        bullets: ['Auditoría Lighthouse', 'SEO técnico inicial', 'Soporte 15 días'],
        span: 'lg:col-span-3',
        featured: false,
        badge: null,
      },
    ],
  },

  /* ------------------------------------------------------------- PROCESO */
  process: {
    eyebrow: 'Proceso',
    title: '¿Cómo trabajamos?',
    subtitle:
      'En entre 24 y 72 horas tu negocio está online. Sin juntas eternas, sin letras chiquitas.',
    deliveryBadge: 'Entrega en 24 a 72 horas',
    steps: [
      {
        number: '01',
        icon: 'MessagesSquare',
        title: 'Conversamos tu idea',
        duration: 'Día 1 · 20 min',
        description:
          'Definimos diseño, colores y funciones exactas a tu gusto. Te muestro referencias y cerramos el alcance por WhatsApp.',
        deliverables: ['Brief express', 'Referencias visuales', 'Cotización cerrada'],
      },
      {
        number: '02',
        icon: 'Code2',
        title: 'Diseño y Desarrollo',
        duration: 'Día 1 – 2',
        description:
          'Construyo tu web con tecnología moderna: velocidad extrema en celulares, animaciones cuidadas y contenido real de tu negocio.',
        deliverables: ['Diseño UI a medida', 'Desarrollo React', 'Versión móvil impecable'],
      },
      {
        number: '03',
        icon: 'Wand2',
        title: 'Revisión y Ajustes en vivo',
        duration: 'Día 2 – 3',
        description:
          'Te comparto un enlace privado y perfeccionamos cada detalle contigo: textos, fotos, colores y flujos de pedido.',
        deliverables: ['Enlace de revisión', 'Ajustes ilimitados del alcance', 'QA en 3 navegadores'],
      },
      {
        number: '04',
        icon: 'Rocket',
        title: 'Entrega oficial + QR + Capacitación',
        duration: 'Día 3',
        description:
          'Publicamos en tu dominio, generamos tu código QR e imprimimos tu carta. Te enseño a manejar todo tú mismo.',
        deliverables: ['Deploy en Vercel', 'QR en alta resolución', 'Capacitación grabada'],
      },
    ],
  },

  /* --------------------------------------------------------------- PLANES */
  pricing: {
    eyebrow: 'Inversión',
    title: 'Inversión Transparente',
    subtitle: 'Pago único, sin mensualidades. El hosting gratuito está incluido de por vida.',
    note: 'Los precios son en Soles (S/) y sirven de punto de partida. El precio final depende de las secciones y funciones que necesites.',
    plans: [
      {
        id: 'emprendedor',
        name: 'Plan Emprendedor',
        tagline: 'Para salir al mundo digital hoy mismo',
        price: 'S/ 150',
        pricePrefix: 'Desde',
        period: 'Pago único',
        description:
          'Carta Digital o catálogo rápido con pedidos a WhatsApp, ideal para negocios que necesitan estar online sin complicaciones.',
        features: [
          'Carta digital o catálogo con pedidos a WhatsApp',
          'Diseño responsivo (celular, tablet y PC)',
          'Hosting gratuito de por vida',
          'Código QR listo para imprimir',
          'Hasta 20 productos o secciones',
          'Entrega en 24 – 48 horas',
        ],
        excluded: ['Panel autoadministrable', 'Carrito con variantes', 'Integración con IA'],
        cta: 'Empezar con este plan',
        highlighted: false,
        badge: null,
        icon: 'Sprout',
      },
      {
        id: 'profesional',
        name: 'Plan Profesional a Medida',
        tagline: 'El favorito de los negocios que venden en serio',
        price: 'S/ 250',
        pricePrefix: 'Desde',
        period: 'Pago único',
        description:
          'Diseño 100% personalizado a tu marca, animaciones premium y todas las variantes que un cliente necesita para comprar.',
        features: [
          'Diseño 100% personalizado a tu marca',
          'Animaciones premium con Framer Motion',
          'Variantes (tallas / colores / notas)',
          'Cálculo de delivery y vuelto',
          'Panel editable desde tu celular',
          'Productos y secciones ilimitadas',
          'Dominio propio + QR imprimible',
          'Entrega en 48 – 72 horas',
        ],
        excluded: ['App Android nativa', 'IA avanzada con escáner'],
        cta: 'Quiero este plan',
        highlighted: true,
        badge: 'Más Elegido',
        icon: 'Crown',
      },
      {
        id: 'empresa',
        name: 'Plan Empresa / IA / App',
        tagline: 'Proyectos que necesitan ir más allá de una web',
        price: 'S/ 400',
        pricePrefix: 'Desde',
        period: 'Pago único',
        description:
          'Web multisede, integración con IA en el navegador, app Android o desarrollo complejo según los requerimientos de tu operación.',
        features: [
          'Web multisede con enrutamiento',
          'Integración con IA en el navegador',
          'App Android a medida',
          'Integraciones con APIs y Google Sheets',
          'Panel administrador propio',
          'Soporte y mantenimiento preferente',
          'Capacitación al equipo completo',
        ],
        excluded: [],
        cta: 'Cotizar mi proyecto',
        highlighted: false,
        badge: null,
        icon: 'Building2',
      },
    ],
    guarantees: [
      'Sin mensualidades ni cobros ocultos',
      '15 días de soporte gratis',
      'Ajustes incluidos hasta la aprobación',
    ],
  },

  /* ------------------------------------------------------------ COTIZADOR */
  quoteModal: {
    eyebrow: 'Cotizador',
    title: 'Cotizador Rápido por WhatsApp',
    subtitle:
      'Responde 3 preguntas y te envío una propuesta con precio cerrado en menos de 1 hora.',
    businessTypes: [
      'Restaurante / Cevichería',
      'Bar / Gastrobar',
      'Panadería / Cafetería',
      'Boutique / Moda',
      'Barbería / Salón',
      'Gimnasio / Salud',
      'Inmobiliaria / Servicios',
      'Otro negocio',
    ],
    needs: [
      { id: 'from-scratch', label: 'Crear web desde cero', icon: 'Sparkles' },
      { id: 'redesign', label: 'Renovar mi web actual', icon: 'RefreshCw' },
      { id: 'carrito-ia', label: 'Agregar carrito WhatsApp / IA', icon: 'ShoppingCart' },
      { id: 'android', label: 'App Android', icon: 'Smartphone' },
    ],
    budgets: [
      { id: 'b1', label: 'S/ 150 – 250' },
      { id: 'b2', label: 'S/ 250 – 400' },
      { id: 'b3', label: 'S/ 400 – 800' },
      { id: 'b4', label: 'Aún no lo sé' },
    ],
    deadlines: [
      { id: 'urgente', label: 'Lo antes posible (24 h)' },
      { id: 'semana', label: 'Esta semana' },
      { id: 'mes', label: 'En el transcurso del mes' },
    ],
    placeholders: {
      name: 'Tu nombre',
      business: 'Nombre de tu negocio',
      details: 'Cuéntame en una línea qué necesitas (opcional)',
    },
    submitLabel: 'Enviar consulta por WhatsApp',
    successNote: 'Abrimos WhatsApp con tu consulta lista. Solo presiona enviar.',
  },

  /* ------------------------------------------------------------------ FAQ */
  faq: {
    eyebrow: 'Dudas frecuentes',
    title: 'Lo que todos me preguntan antes de empezar',
    subtitle: 'Y si te queda alguna duda, escríbeme por WhatsApp. Respondo yo, no un bot.',
    items: [
      {
        id: 'mensualidades',
        question: '¿Tengo que pagar mensualidades?',
        answer:
          'No. Todos mis planes son de pago único. El hosting lo mantengo gratuito de por vida en Vercel, así que no tienes cuotas mensuales ni cobros sorpresa. Solo pagarías un dominio propio si lo deseas (aprox. S/ 40 al año) y eso se compra a tu nombre, no al mío.',
      },
      {
        id: 'precios',
        question: '¿Puedo cambiar mis precios yo mismo?',
        answer:
          'Sí. Los planes Profesional y Empresa incluyen un panel editable desde tu celular (puede ser Google Sheets o un administrador propio) donde cambias precios, fotos, stock y horarios en segundos, sin saber programar. En el plan Emprendedor también puedes pedirme cambios por WhatsApp durante los primeros 15 días.',
      },
      {
        id: 'rediseno',
        question: '¿Ya tengo una web, puedes mejorarla?',
        answer:
          'Sí, es uno de mis servicios más solicitados. Analizo tu web actual, rescatamos tu contenido y tu posicionamiento, y reconstruimos el diseño con tecnología moderna para que cargue en menos de un segundo en celulares. Normalmente duplicamos la velocidad y la cantidad de consultas.',
      },
      {
        id: 'tiempo',
        question: '¿En cuánto tiempo está lista?',
        answer:
          'Entre 24 y 72 horas desde que me envías los textos, fotos y tu logo. Los proyectos con IA o app Android pueden tomar de 5 a 10 días. Si necesitas una entrega urgente para una inauguración, avísame y coordinamos prioridad.',
      },
      {
        id: 'pagos',
        question: '¿Cómo se paga y qué garantía tengo?',
        answer:
          'Acepto Yape, Plin y transferencia BCP / Interbank. Se paga 50% para iniciar y 50% al aprobar la demo. Si no te gusta el resultado en la primera revisión, no pagas el saldo: trabajo hasta que quede exactamente como lo imaginaste.',
      },
      {
        id: 'hosting',
        question: '¿Quién es el dueño del código y del hosting?',
        answer:
          'Tú. El proyecto se despliega en Vercel (o tu hosting preferido) bajo tu propia cuenta, y el código queda en un repositorio a tu nombre. No quedas atado a mí: te entrego todo documentado y con una capacitación grabada para que puedas administrarlo.',
      },
      {
        id: 'fuera',
        question: '¿Trabajas solo en Trujillo?',
        answer:
          'Mi base está en Trujillo, pero atiendo a todo el Perú y clientes en el extranjero. Todo el proceso se hace por WhatsApp, llamadas y enlaces de revisión en vivo, así que la distancia no es un problema.',
      },
    ],
  },

  /* ---------------------------------------------------------------- FOOTER */
  footer: {
    headline: 'Tu competencia ya está en internet.',
    headlineAccent: 'La pregunta es si tú vas a vender más que ellos.',
    description:
      'Cuéntame tu idea hoy y en 72 horas tu negocio puede estar recibiendo pedidos directo a WhatsApp.',
    columns: [
      {
        title: 'Navegación',
        links: [
          { label: 'Proyectos', href: '#proyectos' },
          { label: 'Servicios', href: '#servicios' },
          { label: 'Proceso', href: '#proceso' },
          { label: 'Planes', href: '#planes' },
          { label: 'FAQ', href: '#faq' },
        ],
      },
      {
        title: 'Servicios',
        links: [
          { label: 'Cartas digitales QR', href: '#servicios' },
          { label: 'Tiendas con carrito WhatsApp', href: '#servicios' },
          { label: 'Landing pages a medida', href: '#servicios' },
          { label: 'Integraciones con IA', href: '#servicios' },
          { label: 'Apps Android', href: '#servicios' },
        ],
      },
    ],
    legal: '© ' + new Date().getFullYear() + ' KODOX · Digital Studio Trujillo. Todos los derechos reservados.',
    builtWith: 'Diseñado y desarrollado en Trujillo, Perú — React + Vite + Tailwind + Framer Motion.',
    availability: 'Aceptando 2 proyectos nuevos este mes',
  },
};

/* ------------------------------------------------------------------ HELPERS */

/** Construye un enlace de WhatsApp con mensaje pre-cargado. */
export const buildWhatsAppLink = (
  message = 'Hola KODOX, quiero cotizar una página web para mi negocio.'
) => `https://wa.me/${siteConfig.contact.whatsapp}?text=${encodeURIComponent(message)}`;

/** CTA genérico de cotización. */
export const quoteLink = (context) =>
  buildWhatsAppLink(
    context
      ? `Hola KODOX, quiero cotizar ${context} para mi negocio. ¿Me puedes pasar más información?`
      : undefined
  );

/** Enlace específico para "Quiero una así" de un proyecto. */
export const projectInquiryLink = (project) =>
  buildWhatsAppLink(
    `Hola KODOX, vi la demo "${project.title} — ${project.subtitle}" y quiero una web así (${project.quoteType}) para mi negocio. ¿Me pasas el precio y los tiempos?`
  );

export const navLinks = siteConfig.navigation;
export const projects = siteConfig.portfolio.projects;
export const services = siteConfig.services.items;
export const processSteps = siteConfig.process.steps;
export const pricingPlans = siteConfig.pricing.plans;
export const faqs = siteConfig.faq.items;
export const filters = siteConfig.portfolio.filters;

export default siteConfig;
