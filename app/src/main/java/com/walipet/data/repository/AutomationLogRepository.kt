package com.walipet.data.repository

import com.walipet.data.local.AutomationLogDao
import com.walipet.data.local.entity.AutomationLogEntity
import com.walipet.engine.model.AutomationLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Repositorio del historial de automatizaciones (fuente del feed y stats). */
class AutomationLogRepository(
    private val dao: AutomationLogDao,
) {

    fun observeRecent(limit: Int = 50): Flow<List<AutomationLog>> =
        dao.observeRecent(limit).map { list -> list.map { it.toDomain() } }

    fun observeAll(): Flow<List<AutomationLog>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun getAll(): List<AutomationLog> = dao.getAll().map { it.toDomain() }

    suspend fun add(log: AutomationLog): Long = dao.insert(AutomationLogEntity.fromDomain(log))

    suspend fun count(): Long = dao.count()
}
