package com.stockmaster.app.data

import com.stockmaster.app.data.entity.AuditLog
import com.stockmaster.app.data.entity.Sale
import com.stockmaster.app.data.entity.SaleItem
import com.stockmaster.app.data.entity.StockMovement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** One item inside the shopping cart */
data class CartItem(
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double
)

class StockRepository(private val db: StockMasterDatabase) {

    val products = db.productDao()
    val customers = db.customerDao()
    val staff = db.staffDao()
    val sales = db.saleDao()
    private val saleItems = db.saleItemDao()
    val movements = db.stockMovementDao()
    val audit = db.auditLogDao()

    // ---------- AUDIT ----------

    suspend fun log(action: String, detail: String) {
        audit.insertLog(
            AuditLog(
                staffId = Session.staffId,
                action = action,
                detail = detail
            )
        )
    }

    // ---------- STOCK ----------

    suspend fun restock(productId: Long, qty: Int, reason: String = "Restock") =
        withContext(Dispatchers.IO) {
            val product = products.getProductById(productId) ?: return@withContext
            products.adjustStock(productId, qty)
            movements.insertMovement(
                StockMovement(
                    productId = productId,
                    staffId = Session.staffId,
                    movementType = "RESTOCK",
                    quantity = qty,
                    reason = reason
                )
            )
            log("RESTOCK", "${product.name}: +$qty")
        }

    // ---------- SALES ----------

    suspend fun processSale(
        customerId: Long = 0,
        cart: List<CartItem>,
        paymentMethod: String,
        amountPaid: Double
    ): Long = withContext(Dispatchers.IO) {
        val total = cart.sumOf { it.quantity * it.unitPrice }

        // Step 1: Create the sale record
        val saleId = sales.insertSale(
            Sale(
                customerId = customerId,
                staffId = Session.staffId,
                totalAmount = total,
                paymentMethod = paymentMethod,
                amountPaid = amountPaid
            )
        )

        // Step 2: Save items, reduce stock, record movements
        cart.forEach { item ->
            saleItems.insertSaleItem(
                SaleItem(
                    saleId = saleId,
                    productId = item.productId,
                    quantity = item.quantity,
                    unitPrice = item.unitPrice,
                    totalPrice = item.quantity * item.unitPrice
                )
            )
            products.adjustStock(item.productId, -item.quantity)
            movements.insertMovement(
                StockMovement(
                    productId = item.productId,
                    staffId = Session.staffId,
                    movementType = "SALE",
                    quantity = -item.quantity,
                    reason = "Sale #$saleId"
                )
            )
        }

        // Step 3: Audit trail
        log("SALE", "Sale #$saleId | items: ${cart.size} | total: $total")

        saleId
    }
}
