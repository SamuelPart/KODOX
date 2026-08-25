package com.walipet.engine

import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.primaryConstructor
import kotlin.test.Test

/**
 * Ejecutor mínimo de tests para verificar el motor SIN Gradle (solo con
 * `kotlinc`). Busca funciones anotadas con @kotlin.test.Test y las invoca.
 *
 * Android Studio/Gradle lo ignoran (los tests reales corren con JUnit 4).
 *
 * Uso:
 *   java -cp "<out>:<jars>" com.walipet.engine.TestRunnerKt \
 *        com.walipet.engine.RuleEngineTest com.walipet.engine.XpEngineTest ...
 */
fun main(args: Array<String>) {
    var passed = 0
    var failed = 0
    val failures = mutableListOf<String>()

    for (className in args) {
        val clazz = Class.forName(className).kotlin
        val testFunctions = clazz.memberFunctions
            .filter { fn -> fn.annotations.any { it.annotationClass == Test::class } }
            .sortedBy { it.name }

        val instance = runCatching { clazz.createInstance() }.getOrElse {
            runCatching { clazz.primaryConstructor!!.call() }.getOrElse { null }
        }
        requireNotNull(instance) { "No se puede instanciar $className" }

        for (fn in testFunctions) {
            val label = "$className.${fn.name}"
            try {
                fn.call(instance)
                passed++
                println("PASS  $label")
            } catch (t: Throwable) {
                failed++
                failures += "$label -> ${t.cause?.message ?: t.message}"
                println("FAIL  $label -> ${t.cause?.message ?: t.message}")
            }
        }
    }

    println()
    println("Resultado: $passed pasaron, $failed fallaron (${args.size} clases)")
    if (failures.isNotEmpty()) {
        failures.forEach { println("  - $it") }
        kotlin.system.exitProcess(1)
    }
}
