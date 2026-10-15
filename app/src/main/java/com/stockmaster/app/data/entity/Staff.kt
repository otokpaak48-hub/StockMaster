package com.stockmaster.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "staff")
data class Staff(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val role: String = "CASHIER",
    val phone: String = "",
    val salt: String = "",
    val pin: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
