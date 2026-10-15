package com.stockmaster.app.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [Index(value = ["barcode"])]
)
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val barcode: String = "",
    val category: String = "",
    val unit: String = "pcs",
    val costPrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val quantityInStock: Int = 0,
    val reorderLevel: Int = 5,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
