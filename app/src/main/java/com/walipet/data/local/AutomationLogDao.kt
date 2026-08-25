package com.walipet.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.walipet.data.local.entity.AutomationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AutomationLogDao {

    @Query("SELECT * FROM automation_logs ORDER BY occurredAtMillis DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<AutomationLogEntity>>

    @Query("SELECT * FROM automation_logs ORDER BY occurredAtMillis DESC")
    fun observeAll(): Flow<List<AutomationLogEntity>>

    @Query("SELECT * FROM automation_logs ORDER BY occurredAtMillis DESC")
    suspend fun getAll(): List<AutomationLogEntity>

    @Insert
    suspend fun insert(log: AutomationLogEntity): Long

    @Query("SELECT COUNT(*) FROM automation_logs")
    suspend fun count(): Long
}
