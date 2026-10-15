package com.stockmaster.app.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audit_logs",
    indices = [Index(value = ["staffId"]), Index(value = ["createdAt"])]
)
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val staffId: Long = 0,
    val action: String,
    val details: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
