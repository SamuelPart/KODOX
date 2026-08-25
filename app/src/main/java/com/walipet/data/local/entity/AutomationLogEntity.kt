package com.walipet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.walipet.engine.model.AutomationLog
import com.walipet.engine.model.ChatType
import java.time.Instant

/** Entrada del historial de automatizaciones exitosas. */
@Entity(tableName = "automation_logs")
data class AutomationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val ruleId: Long,
    val ruleName: String,
    val sender: String,
    val chatType: String = ChatType.PRIVATE.name,
    val messageSnippet: String,
    val responseText: String,
    val xpGained: Int,
    val occurredAtMillis: Long = System.currentTimeMillis(),
) {
    fun toDomain(): AutomationLog = AutomationLog(
        id = id,
        ruleId = ruleId,
        ruleName = ruleName,
        sender = sender,
        chatType = runCatching { ChatType.valueOf(chatType) }.getOrDefault(ChatType.PRIVATE),
        messageSnippet = messageSnippet,
        responseText = responseText,
        xpGained = xpGained,
        occurredAt = Instant.ofEpochMilli(occurredAtMillis),
    )

    companion object {
        fun fromDomain(log: AutomationLog): AutomationLogEntity = AutomationLogEntity(
            id = log.id,
            ruleId = log.ruleId,
            ruleName = log.ruleName,
            sender = log.sender,
            chatType = log.chatType.name,
            messageSnippet = log.messageSnippet,
            responseText = log.responseText,
            xpGained = log.xpGained,
            occurredAtMillis = log.occurredAt.toEpochMilli(),
        )
    }
}
