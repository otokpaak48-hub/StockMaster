package com.stockmaster.app.data.dao

import androidx.room.*
import com.stockmaster.app.data.entity.AuditLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditLogDao {

    @Insert
    suspend fun insertLog(log: AuditLog): Long

    @Query("SELECT * FROM audit_logs ORDER BY createdAt DESC")
    fun getAllLogs(): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_logs WHERE staffId = :staffId ORDER BY createdAt DESC")
    fun getLogsByStaff(staffId: Long): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_logs WHERE action = :action ORDER BY createdAt DESC")
    fun getLogsByAction(action: String): Flow<List<AuditLog>>
}
