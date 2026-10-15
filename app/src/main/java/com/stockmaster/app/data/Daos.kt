package com.stockmaster.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(p: Product): Long

    @Update
    suspend fun update(p: Product)

    @Delete
    suspend fun delete(p: Product)

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAll(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Long): Product?

    @Query("SELECT * FROM products WHERE name LIKE '%' || :q || '%' OR sku LIKE '%' || :q || '%' OR category LIKE '%' || :q || '%' ORDER BY name ASC")
    fun search(q: String): Flow<List<Product>>

    @Query("SELECT DISTINCT category FROM products ORDER BY category ASC")
    fun getCategories(): Flow<List<String>>

    @Query("SELECT * FROM products WHERE quantity <= reorderLevel ORDER BY quantity ASC")
    fun getLowStock(): Flow<List<Product>>

    @Query("SELECT COALESCE(SUM(quantity * costPrice), 0) FROM products")
    fun stockValue(): Flow<Double>

    @Query("SELECT COUNT(*) FROM products")
    fun count(): Flow<Int>

    @Query("UPDATE products SET quantity = :newQty WHERE id = :id")
    suspend fun setQuantity(id: Long, newQty: Double)
}

@Dao
interface CustomerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(c: Customer): Long

    @Update
    suspend fun update(c: Customer)

    @Delete
    suspend fun delete(c: Customer)

    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAll(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :q || '%' OR phone LIKE '%' || :q || '%' ORDER BY name ASC")
    fun search(q: String): Flow<List<Customer>>

    @Query("UPDATE customers SET balance = balance + :amount WHERE id = :id")
    suspend fun adjustBalance(id: Long, amount: Double)
}

@Dao
interface StaffDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(s: Staff): Long

    @Update
    suspend fun update(s: Staff)

    @Query("SELECT * FROM staff ORDER BY name ASC")
    fun getAll(): Flow<List<Staff>>

    @Query("SELECT * FROM staff WHERE pin = :pin AND active = 1 LIMIT 1")
    suspend fun login(pin: String): Staff?

    @Query("UPDATE staff SET active = :active WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean)
}

@Dao
interface SaleDao {
    @Insert
    suspend fun insert(s: Sale): Long

    @Query("SELECT COALESCE(SUM(total - discount), 0) FROM sales WHERE createdAt BETWEEN :from AND :to AND status = 'COMPLETED'")
    fun revenueBetween(from: Long, to: Long): Flow<Double>

    @Query("SELECT COUNT(*) FROM sales WHERE createdAt BETWEEN :from AND :to AND status = 'COMPLETED'")
    fun countBetween(from: Long, to: Long): Flow<Int>

    @Query("SELECT * FROM sales WHERE createdAt BETWEEN :from AND :to ORDER BY createdAt DESC")
    fun between(from: Long, to: Long): Flow<List<Sale>>

    @Query("SELECT * FROM sales ORDER BY createdAt DESC LIMIT 20")
    fun recent(): Flow<List<Sale>>

    @Query("SELECT MAX(si.productName) AS name, SUM(si.quantity) AS qty, SUM(si.lineTotal) AS revenue FROM sale_items si INNER JOIN sales s ON si.saleId = s.id WHERE s.createdAt BETWEEN :from AND :to AND s.status = 'COMPLETED' GROUP BY si.productId ORDER BY revenue DESC LIMIT :limit")
    fun topProducts(from: Long, to: Long, limit: Int): Flow<List<TopProduct>>
}

@Dao
interface SaleItemDao {
    @Insert
    suspend fun insertAll(items: List<SaleItem>)
}

@Dao
interface StockMovementDao {
    @Insert
    suspend fun insert(m: StockMovement): Long

    @Query("SELECT * FROM stock_movements ORDER BY createdAt DESC LIMIT 50")
    fun recent(): Flow<List<StockMovement>>
}

@Dao
interface AuditLogDao {
    @Insert
    suspend fun insert(l: AuditLog): Long

    @Query("SELECT * FROM audit_logs ORDER BY createdAt DESC LIMIT 100")
    fun recent(): Flow<List<AuditLog>>
}
