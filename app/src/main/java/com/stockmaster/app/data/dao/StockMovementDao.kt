package com.stockmaster.app.data.dao

import androidx.room.*
import com.stockmaster.app.data.entity.StockMovement
import kotlinx.coroutines.flow.Flow

@Dao
interface StockMovementDao {

    @Insert
    suspend fun insertMovement(movement: StockMovement): Long

    @Query("SELECT * FROM stock_movements ORDER BY createdAt DESC")
    fun getAllMovements(): Flow<List<StockMovement>>

    @Query("SELECT * FROM stock_movements WHERE productId = :productId ORDER BY createdAt DESC")
    fun getMovementsByProduct(productId: Long): Flow<List<StockMovement>>

    @Query("SELECT * FROM stock_movements WHERE staffId = :staffId ORDER BY createdAt DESC")
    fun getMovementsByStaff(staffId: Long): Flow<List<StockMovement>>
}
