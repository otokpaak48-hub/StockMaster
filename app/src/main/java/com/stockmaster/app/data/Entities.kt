package com.stockmaster.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sku: String,
    val category: String = "General",
    val unit: String = "pcs",
    val costPrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val quantity: Double = 0.0,
    val reorderLevel: Double = 5.0,
    val supplier: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val address: String = "",
    val creditLimit: Double = 0.0,
    val balance: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "staff")
data class Staff(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val role: String = "Cashier",
    val phone: String = "",
    val pin: String,
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val receiptNo: String,
    val customerId: Long? = null,
    val staffId: Long,
    val total: Double,
    val discount: Double = 0.0,
    val paymentMethod: String = "Cash",
    val amountPaid: Double = 0.0,
    val status: String = "COMPLETED",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sale_items")
data class SaleItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var saleId: Long = 0,
    val productId: Long,
    val productName: String,
    val unitPrice: Double,
    val quantity: Double,
    val lineTotal: Double
)

@Entity(tableName = "stock_movements")
data class StockMovement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productName: String,
    val type: String,
    val quantityChange: Double,
    val note: String = "",
    val staffId: Long,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val detail: String = "",
    val staffId: Long,
    val createdAt: Long = System.currentTimeMillis()
)

data class TopProduct(val name: String, val qty: Double, val revenue: Double)
