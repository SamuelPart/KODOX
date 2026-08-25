package com.walipet

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.walipet.data.local.AppDatabase
import com.walipet.data.local.entity.AutomationLogEntity
import com.walipet.data.local.entity.PetEntity
import com.walipet.data.local.entity.RuleEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de integración de la base de datos Room (requieren dispositivo
 * o emulador). Verifican el ciclo escritura→lectura de las tres tablas.
 */
@RunWith(AndroidJUnit4::class)
class DatabaseWriteReadTest {

    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndReadRule() = runBlocking {
        val id = db.ruleDao().insert(
            RuleEntity(name = "Prueba", trigger = "hola", response = "¡Hola!", isActive = true),
        )
        val rules = db.ruleDao().getAll()
        assertEquals(1, rules.size)
        assertEquals("Prueba", rules.first().name)
        assertEquals(id, rules.first().id)
    }

    @Test
    fun petUpsertKeepsSingleRow() = runBlocking {
        db.petDao().upsert(PetEntity(level = 3, xp = 40))
        db.petDao().upsert(PetEntity(level = 5, xp = 10))
        val pet = db.petDao().get()
        assertEquals(5, pet?.level)
        assertEquals(10, pet?.xp)
    }

    @Test
    fun logsComeBackNewestFirst() = runBlocking {
        db.automationLogDao().insert(logEntity(occurredAtMillis = 100))
        db.automationLogDao().insert(logEntity(occurredAtMillis = 300))
        db.automationLogDao().insert(logEntity(occurredAtMillis = 200))

        val logs = db.automationLogDao().getAll()
        assertEquals(3, logs.size)
        assertEquals(300, logs.first().occurredAtMillis)
    }

    @Test
    fun toggleActiveUpdatesFlag() = runBlocking {
        val id = db.ruleDao().insert(RuleEntity(name = "R", trigger = "t", response = "r"))
        db.ruleDao().setActive(id, active = false)
        assertEquals(false, db.ruleDao().getAll().first().isActive)
    }

    private fun logEntity(occurredAtMillis: Long) = AutomationLogEntity(
        ruleId = 1,
        ruleName = "Regla",
        sender = "Juan",
        messageSnippet = "hola",
        responseText = "ok",
        xpGained = 15,
        occurredAtMillis = occurredAtMillis,
    )
}
