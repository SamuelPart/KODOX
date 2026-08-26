# WaliPet 🤖✨

**Tu mascota pixel art que responde WhatsApp por ti.**

WaliPet combina un *NotificationListenerService* con un motor de gamificación
estilo Tamagotchi: lee en voz alta los mensajes que te envían y, si coinciden
con una regla, puede responder automáticamente por `RemoteInput` (incluso con
la pantalla apagada). El huevo, el XP y la racha diaria crecen **solo** con
cada lectura en voz alta, no con las respuestas automáticas.

> 🔒 **Privacidad primero:** todos los mensajes se procesan **100 % localmente
> en tu dispositivo**. WaliPet no tiene permisos de red ni sube ningún texto
> a servidores.

---

## ✨ Funcionalidades

| Pantalla | Qué hace |
|---|---|
| **Hábitat** (Dashboard) | Mascota pixel art animada con estados de ánimo (feliz, triste, dormida), barras de XP/energía/felicidad que se colorean al avanzar, racha diaria 🔥, voz en off y actividad reciente. |
| **Reglas** (Rule Builder) | Crea reglas del tipo *"Si el mensaje contiene «precio» → responde «Hola, te enviaremos la información en un momento.»"* con modos de coincidencia (exacta, contiene, empieza con, regex), ámbito de chats (todos/privados/grupos) y mayúsculas sensibles. |
| **Codex** | Línea de evolución (Huevo → Cría → Joven → Épica) y 12 logros/medallas desbloqueables. |
| **Stats** | Métricas de impacto: respuestas hoy/semana/mes, XP total, tiempo ahorrado estimado (45 s por respuesta), gráfico de los últimos 7 días y feed de actividad en tiempo real. |
| **Onboarding** | Guía paso a paso de los permisos especiales (acceso a notificaciones, notificaciones de la app, exención de batería). |

**Gamificación (motor `engine`):**
- XP por lectura en voz alta: **+15 XP** (bonus **+10 XP** si es la primera del día). Las respuestas automáticas no dan XP ni racha.
- Curva de niveles progresiva: `100 × nivel` XP para subir (N1→2: 100, N2→3: 200, …).
- Racha diaria: leer al menos un mensaje en voz alta al día mantiene/crece la racha.
- Estados de ánimo derivados de la actividad reciente (energía y felicidad 0-100).
- **Voz en off** (TTS local): lee el texto exacto de los mensajes **que te envían** en la **última hora**; el resto lo resume como «N mensajes de M chats». No lee las respuestas automáticas de WaliPet ni avisos de WhatsApp que no son de un chat (llamadas, Web, copias de seguridad, estados…).

---

## 🏗️ Arquitectura y stack

```
app/    Android App (MVVM + Compose)     ── UI, Room, servicios Android
engine/ Biblioteca Kotlin pura (JVM)     ── motor de reglas, XP, rachas,
                                             parser de WhatsApp, stats
```

- **Lenguaje:** Kotlin 2.0 (corrutinas + Flow).
- **UI:** Jetpack Compose (Material 3) con tema retro/arcade propio y sprites
  pixel art dibujados por código (sin assets de imagen).
- **Arquitectura:** MVVM + módulo de dominio puro (`engine`) + capa de datos (Room).
- **Persistencia:** Room (`pet`, `rules`, `automation_logs`).
- **DI:** `ServiceLocator` manual (sin Hilt, build ligero y auditable).
- **Notificaciones:** `NotificationListenerService` + `RemoteInput`
  (inyección de respuestas sin abrir WhatsApp).

```
Notificación WhatsApp ──► NotificationListenerService
                              │
                              ▼
              NotificationExtractor (Android) ──► NotificationRawData
                              │
                              ▼
              WhatsAppMessageParser (engine, puro) ──► ParsedWhatsAppMessage
                              │
                              ▼
              RuleEngine (engine, puro) ──► regla coincidente
                              │
                              ▼
              VoiceOver ──► TTS («María dice hola»)
                              │
                              ▼
         PetRepository.recordSpokenMessage() (+XP, +racha, huevo)
                              │
              AutomatedReplier ──► RemoteInput (sin XP ni racha)  →  Log en Room
```

---

## 🛠️ Requisitos y build

- **Android Studio** (última estable) con JDK 17+.
- **compileSdk 35**, minSdk 26 (Android 8.0+), targetSdk 35.
- Gradle 8.11.1 (el *wrapper* del proyecto lo descarga automáticamente).

```bash
# Abrir el proyecto con Android Studio y esperar a la sincronización.
# Luego:
./gradlew :engine:test      # tests unitarios del motor
./gradlew :app:testDebugUnitTest   # tests unitarios de la app
./gradlew :app:installDebug # instalar en dispositivo/emulador
```

> Los tests del motor (`engine`) también se pueden ejecutar sin Gradle con un
> `kotlinc` local: el script de verificación se documenta en `engine/README.md`
> (el repo incluye los JARs de JUnit 4.13.2 y kotlin-test necesarios).

### Permisos que pide la app (y por qué)

1. **Acceso a notificaciones** (permiso especial de sistema): necesario para que
   el `NotificationListenerService` lea las notificaciones de WhatsApp.
   Se concede en *Ajustes → Acceso a notificaciones* (onboarding incluido).
2. **Notificaciones** (`POST_NOTIFICATIONS`, Android 13+): para avisarte cuando
   WaliPet responde o cuando una lectura sube de nivel al huevo.
3. **Exención de batería** (opcional): en teléfonos con ahorro agresivo de
   batería (Xiaomi, Huawei, Samsung…), evita que el sistema mate el servicio.
4. **`RECEIVE_BOOT_COMPLETED`**: el watchdog recuerda al usuario si el acceso a
   notificaciones se desactivó tras un reinicio.

---

## 🧪 Testing

- **Motor (unitarios):** reglas (exacta/contiene/prefijo/regex, ámbito, orden), voz en off (última hora exacta / resumen / ignorar no-chats),
  parser de WhatsApp (privados, grupos 👥, mensajes estructurados, casos
  límite), XP y curvas de nivel, rachas, estados de ánimo, estadísticas y
  logros. `./gradlew :engine:test`
- **App (unit):** formateadores de tiempo/duración.
- **App (instrumentación):** integración Room (escritura/lectura de las 3
  tablas) y flujo motor→regla en dispositivo. `./gradlew :app:connectedAndroidTest`

---

## ⚠️ Retos técnicos y limitaciones conocidas

1. **El formato de las notificaciones de WhatsApp cambia con el tiempo.**
   El parser usa heurísticas en cascada (mensajes estructurados
   `MessagingStyle`/`Wearable` → emoji 👥 → prefijo `Remitente: …` → chat
   privado). Si Meta cambia el empaquetado, puede requerir ajustes en
   `WhatsAppMessageParser` (los tests del motor permiten detectarlo y
   corregirlo rápido).
2. **`RemoteInput` por `PendingIntent.send()`** replica el mecanismo del botón
   "Responder" de la notificación. Funciona con pantalla apagada, pero si
   WhatsApp cambia su acción de respuesta, el envío podría requerir revisión.
3. **Fabricantes agresivos con la batería**: la exención del ahorro y el
   watchdog ayudan, pero algunas ROMs matan servicios de terceros; es el
   comportamiento esperado de Android y no se puede eludir sin permisos de
   sistema.
4. **Detección de grupos sin emoji 👥 ni mensajes estructurados** es
   heurística: se usa un prefijo corto (≤ 24 caracteres, ≤ 2 palabras) para
   evitar falsos positivos en chats privados con ":" en el texto.
5. **Android 13/14+ restringen el trabajo en segundo plano**: la app guía al
   usuario con onboarding y no intenta eludir las restricciones del sistema.

---

## 🗺️ Roadmap

- [ ] Servicio en primer plano opcional (FGS) con notificación persistente para
      ROMs agresivas.
- [ ] Sincronización de estadísticas (gráficos semanales/mensuales más ricos).
- [ ] Más modos de coincidencia (frases completas, "cualquiera de…").
- [ ] Sonido retro opcional al ganar XP (estilo Game Boy).
- [ ] Temas alternativos (Game Boy, Neo-Geo) y más sprites.

---

## 📄 Licencia

Proyecto educativo/open source. No está afiliado a Meta ni a WhatsApp Inc.
