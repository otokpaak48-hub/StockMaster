package com.stockmaster.app.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sales",
    indices = [Index(value = ["staffId"]), Index(value = ["customerId"])]
)
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long = 0,
    val staffId: Long = 0,
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val amountPaid: Double = 0.0,
    val changeDue: Double = 0.0,
    val paymentMethod: String = "CASH",
    val status: String = "COMPLETED",
    val createdAt: Long = System.currentTimeMillis()
)
