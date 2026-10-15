package com.stockmaster.app.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_movements",
    indices = [Index(value = ["productId"])]
)
data class StockMovement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val type: String,
    val quantity: Int,
    val reason: String = "",
    val staffId: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)
