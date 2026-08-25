# Módulo `engine` — motor puro de WaliPet

Biblioteca Kotlin **sin dependencias de Android** con toda la lógica de negocio:

| Paquete | Contenido |
|---|---|
| `com.walipet.engine.model` | Modelos puros: `Rule`, `PetState`, `AutomationLog`, `Achievement`, `EvolutionStage`, `PetMood`, `MatchMode`, `ChatScope`, `ParsedWhatsAppMessage`… |
| `com.walipet.engine.rule` | `RuleEngine` (evaluación de reglas), `MessageMatcher` (exacta/contiene/prefijo/regex), `WhatsAppMessageParser` (notificaciones → mensaje normalizado). |
| `com.walipet.engine.voice` | Clasificador de notificaciones de WhatsApp y planificador de voz en off (última hora exacta / «N mensajes de M chats» / silencio si no es un chat). |
| `com.walipet.engine.pet` | `XpEngine` (curva 100×nivel), `StreakEngine` (rachas y energía/felicidad), `PetMoodResolver`, `EvolutionResolver`, `AchievementChecker` (12 logros). |
| `com.walipet.engine.stats` | `StatsCalculator` (hoy/semana/mes, tiempo ahorrado, gráfico diario). |
| `com.walipet.engine.time` | `Clock` (abstracción de tiempo inyectable para tests). |

## Tests (67 unitarios)

En Android Studio:

```bash
./gradlew :engine:test
```

**Sin Gradle** (verificación rápida con `kotlinc`): se necesitan los JARs de
JUnit 4.13.2, hamcrest-core 1.3 y kotlin-test (disponibles en Maven Central o
en la distribución oficial de Kotlin):

```bash
# Descargar una vez:
#   junit-4.13.2.jar, hamcrest-core-1.3.jar  → https://repo1.maven.org/maven2/junit/junit/4.13.2/
#   kotlin-test.jar, kotlin-test-junit.jar    → <kotlinc-dist>/lib/
LIBS="libs/junit-4.13.2.jar:libs/hamcrest-core-1.3.jar:libs/kotlin-test.jar:libs/kotlin-test-junit.jar:libs/kotlin-reflect.jar"

kotlinc -cp "$LIBS" -jvm-target 11 -d /tmp/engine-out src/main/kotlin src/test/kotlin \
  && java -cp "/tmp/engine-out:$LIBS" \
     com.walipet.engine.TestRunnerKt \
     com.walipet.engine.MessageMatcherTest com.walipet.engine.RuleEngineTest \
     com.walipet.engine.WhatsAppMessageParserTest com.walipet.engine.XpEngineTest \
     com.walipet.engine.StreakEngineTest com.walipet.engine.EvolutionAndMoodTest \
     com.walipet.engine.StatsCalculatorTest com.walipet.engine.AchievementCheckerTest \
     com.walipet.engine.WhatsAppNotificationClassifierTest com.walipet.engine.VoiceOverPlannerTest
```

> El runner `TestRunnerKt` es un pequeño ejecutor de tests con reflexión que
> vive en `src/test/kotlin/com/walipet/engine/TestRunner.kt` (solo para el
> build offline; Gradle usa JUnit 4 directamente).

## Diseño

- **Sin Android:** todas las clases son JVM puras (`java.time`), por lo que se
  prueban en milisegundos sin emulador.
- **Determinista:** el tiempo se inyecta vía `Clock` (los tests usan
  `FakeClock`).
- **Efectos sin sorpresas:** las mutaciones de estado devuelven copias
  (`data class copy`); la persistencia real vive en la capa `data` de la app.
