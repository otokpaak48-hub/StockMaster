package com.stockmaster.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StockRepository(private val db: AppDatabase) {

    val products = db.productDao()
    val customers = db.customerDao()
    val staff = db.staffDao()
    val sales = db.saleDao()
    private val saleItems = db.saleItemDao()
    val movements = db.stockMovementDao()
    val audit = db.auditLogDao()

    suspend fun log(action: String, detail: String, staffId: Long) {
        audit.insert(AuditLog(action = action, detail = detail, staffId = staffId))
    }

    suspend fun restock(productId: Long, qty: Double, note: String, staffId: Long) =
        withContext(Dispatchers.IO) {
            val p = products.getById(productId) ?: return@withContext
            products.setQuantity(p.id, p.quantity + qty)
            movements.insert(StockMovement(productId = p.id, productName = p.name,
                type = "RESTOCK", quantityChange = qty, note = note, staffId = staffId))
        }

    suspend fun auditAdjust(productId: Long, counted: Double, staffId: Long) =
        withContext(Dispatchers.IO) {
            val p = products.getById(productId) ?: return@withContext
            val diff = counted - p.quantity
            products.setQuantity(p.id, counted)
            movements.insert(StockMovement(productId = p.id, productName = p.name,
                type = "AUDIT", quantityChange = diff, note = "Physical count adjustment",
                staffId = staffId))
        }

    suspend fun recordSale(sale: Sale, items: List<SaleItem>) = withContext(Dispatchers.IO) {
        val saleId = sales.insert(sale)
        saleItems.insertAll(items.map { it.copy(saleId = saleId) })

        items.forEach { item ->
            val p = products.getById(item.productId) ?: return@forEach
            products.setQuantity(p.id, (p.quantity - item.quantity).coerceAtLeast(0.0))
            movements.insert(StockMovement(productId = p.id, productName = p.name,
                type = "SALE", quantityChange = -item.quantity,
                note = "Receipt ${sale.receiptNo}", staffId = sale.staffId))
        }

        val net = sale.total - sale.discount
        val due = net - sale.amountPaid
        if (sale.paymentMethod == "Credit" && sale.customerId != null && due > 0) {
            customers.adjustBalance(sale.customerId!!, due)
        }
    }

    companion object {
        @Volatile private var INSTANCE: StockRepository? = null

        fun get(context: Context): StockRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: StockRepository(AppDatabase.get(context)).also { INSTANCE = it }
            }
    }
}
