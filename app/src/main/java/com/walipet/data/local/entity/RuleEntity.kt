package com.walipet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.walipet.engine.model.ChatScope
import com.walipet.engine.model.MatchMode
import com.walipet.engine.model.Rule
import java.time.Instant

/** Regla lógica persistida (se mapea al modelo puro del motor). */
@Entity(tableName = "rules")
data class RuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val trigger: String,
    val response: String,
    val matchMode: String = MatchMode.CONTAINS.name,
    val chatScope: String = ChatScope.ALL.name,
    val caseSensitive: Boolean = false,
    val isActive: Boolean = true,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val timesTriggered: Int = 0,
    val lastTriggeredAtMillis: Long? = null,
) {
    fun toDomain(): Rule = Rule(
        id = id,
        name = name,
        trigger = trigger,
        response = response,
        matchMode = matchModeOf(matchMode),
        chatScope = chatScopeOf(chatScope),
        caseSensitive = caseSensitive,
        isActive = isActive,
        createdAt = Instant.ofEpochMilli(createdAtMillis),
        timesTriggered = timesTriggered,
        lastTriggeredAt = lastTriggeredAtMillis?.let(Instant::ofEpochMilli),
    )

    companion object {
        fun fromDomain(rule: Rule): RuleEntity = RuleEntity(
            id = rule.id,
            name = rule.name,
            trigger = rule.trigger,
            response = rule.response,
            matchMode = rule.matchMode.name,
            chatScope = rule.chatScope.name,
            caseSensitive = rule.caseSensitive,
            isActive = rule.isActive,
            createdAtMillis = rule.createdAt.toEpochMilli(),
            timesTriggered = rule.timesTriggered,
            lastTriggeredAtMillis = rule.lastTriggeredAt?.toEpochMilli(),
        )

        fun matchModeOf(name: String): MatchMode = runCatching { MatchMode.valueOf(name) }.getOrDefault(MatchMode.CONTAINS)

        fun chatScopeOf(name: String): ChatScope = runCatching { ChatScope.valueOf(name) }.getOrDefault(ChatScope.ALL)
    }
}
