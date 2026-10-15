package com.stockmaster.app.data.dao

import androidx.room.*
import com.stockmaster.app.data.entity.SaleItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleItemDao {

    @Insert
    suspend fun insertSaleItem(saleItem: SaleItem): Long

    @Insert
    suspend fun insertSaleItems(saleItems: List<SaleItem>)

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    fun getItemsForSale(saleId: Long): Flow<List<SaleItem>>

    @Query("SELECT * FROM sale_items WHERE productId = :productId")
    fun getItemsByProduct(productId: Long): Flow<List<SaleItem>>
}
