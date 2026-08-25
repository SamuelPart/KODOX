/**
 * Módulo `engine`: el corazón de WaliPet.
 *
 * Es una biblioteca Kotlin 100 % pura (JVM) SIN dependencias de Android.
 * Contiene el motor de reglas, el sistema de XP/rachas/evolución, el parser
 * de mensajes de WhatsApp y las estadísticas. Al ser puro Kotlin se puede
 * probar de forma unitaria de manera rápida y aislada.
 */
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Tests con kotlin.test sobre JUnit4 (estándar en Android Studio).
    testImplementation(kotlin("test-junit"))
    testImplementation(libs.junit)
}

tasks.test {
    useJUnit()
}
