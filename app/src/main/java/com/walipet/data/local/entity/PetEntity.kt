package com.walipet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.walipet.engine.model.PetState
import java.time.Instant
import java.time.LocalDate

/**
 * Fila única con el estado persistido de la mascota.
 * Se usa la misma tabla para toda la vida de la app (id fijo = 1).
 */
@Entity(tableName = "pet")
data class PetEntity(
    @PrimaryKey val id: Int = 1,
    val level: Int = 1,
    val xp: Int = 0,
    val streakDays: Int = 0,
    val longestStreak: Int = 0,
    val totalAutomations: Long = 0,
    val lastAutomationEpochDay: Long? = null,
    val lastAutomationAtMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
) {
    fun toDomain(): PetState {
        val lastDate = lastAutomationEpochDay?.let { LocalDate.ofEpochDay(it) }
        val lastAt = lastAutomationAtMillis?.let { Instant.ofEpochMilli(it) }
        return PetState(
            level = level,
            xp = xp,
            streakDays = streakDays,
            longestStreak = longestStreak,
            totalAutomations = totalAutomations,
            lastAutomationDate = lastDate,
            lastAutomationAt = lastAt,
            createdAt = Instant.ofEpochMilli(createdAtMillis),
        )
    }

    companion object {
        fun fromDomain(pet: PetState): PetEntity = PetEntity(
            id = 1,
            level = pet.level,
            xp = pet.xp,
            streakDays = pet.streakDays,
            longestStreak = pet.longestStreak,
            totalAutomations = pet.totalAutomations,
            lastAutomationEpochDay = pet.lastAutomationDate?.toEpochDay(),
            lastAutomationAtMillis = pet.lastAutomationAt?.toEpochMilli(),
            createdAtMillis = pet.createdAt.toEpochMilli(),
        )
    }
}
