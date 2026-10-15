package com.stockmaster.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val sku: String,
    val category: String,
    val costPrice: Double,
    val sellingPrice: Double,
    val quantityInStock: Int,
    val reorderLevel: Int = 5,
    val createdAt: Long = System.currentTimeMillis()
)
