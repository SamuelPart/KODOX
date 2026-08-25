package com.walipet.data.repository

import com.walipet.data.local.RuleDao
import com.walipet.data.local.entity.RuleEntity
import com.walipet.engine.model.Rule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Repositorio de reglas lógicas (CRUD + estado activo). */
class RuleRepository(
    private val dao: RuleDao,
) {

    fun observeAll(): Flow<List<Rule>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun getAll(): List<Rule> = dao.getAll().map { it.toDomain() }

    suspend fun getActive(): List<Rule> = getAll().filter { it.isActive }

    /** Inserta si es nueva (id == 0) o actualiza si ya existe. Devuelve el id final. */
    suspend fun save(rule: Rule): Long {
        return if (rule.id == 0L) {
            dao.insert(RuleEntity.fromDomain(rule))
        } else {
            dao.update(RuleEntity.fromDomain(rule))
            rule.id
        }
    }

    suspend fun delete(rule: Rule) = dao.delete(RuleEntity.fromDomain(rule))

    suspend fun setActive(id: Long, active: Boolean) = dao.setActive(id, active)

    suspend fun recordTriggered(id: Long, nowMillis: Long) = dao.recordTrigger(id, nowMillis)

    /** Siembra reglas de ejemplo en el primer arranque. */
    suspend fun ensureSeeded() {
        if (dao.getAll().isEmpty()) {
            DefaultRules.all().forEach { dao.insert(RuleEntity.fromDomain(it)) }
        }
    }
}

/** Reglas de ejemplo creadas en el primer uso (la usuaria puede editarlas/borrarlas). */
object DefaultRules {
    fun all(): List<Rule> = listOf(
        Rule(
            name = "Saludo",
            trigger = "hola",
            response = "¡Hola! 👋 Gracias por escribir. Te responderé en un momento.",
            matchMode = com.walipet.engine.model.MatchMode.CONTAINS,
        ),
        Rule(
            name = "Precio",
            trigger = "precio",
            response = "Hola, te enviaremos la información del precio en un momento.",
            matchMode = com.walipet.engine.model.MatchMode.CONTAINS,
        ),
        Rule(
            name = "Catálogo",
            trigger = "catálogo",
            response = "Te paso el catálogo en un momento. 📦",
            matchMode = com.walipet.engine.model.MatchMode.CONTAINS,
        ),
    )
}
