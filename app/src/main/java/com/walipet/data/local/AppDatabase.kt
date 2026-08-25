package com.walipet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.walipet.data.local.entity.AutomationLogEntity
import com.walipet.data.local.entity.PetEntity
import com.walipet.data.local.entity.RuleEntity

/**
 * Base de datos local de WaliPet (Room).
 *
 * Tablas:
 *  - `pet`             : estado de la mascota (nivel, XP, racha, …).
 *  - `rules`           : reglas lógicas de automatización.
 *  - `automation_logs` : historial de respuestas exitosas (estadísticas).
 *
 * Todos los datos permanecen en el dispositivo: los mensajes de WhatsApp
 * nunca salen del teléfono.
 */
@Database(
    entities = [PetEntity::class, RuleEntity::class, AutomationLogEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao
    abstract fun ruleDao(): RuleDao
    abstract fun automationLogDao(): AutomationLogDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "walipet.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
