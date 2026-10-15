package com.stockmaster.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.stockmaster.app.data.dao.AuditLogDao
import com.stockmaster.app.data.dao.CustomerDao
import com.stockmaster.app.data.dao.ProductDao
import com.stockmaster.app.data.dao.SaleDao
import com.stockmaster.app.data.dao.SaleItemDao
import com.stockmaster.app.data.dao.StaffDao
import com.stockmaster.app.data.dao.StockMovementDao
import com.stockmaster.app.data.entity.AuditLog
import com.stockmaster.app.data.entity.Customer
import com.stockmaster.app.data.entity.Product
import com.stockmaster.app.data.entity.Sale
import com.stockmaster.app.data.entity.SaleItem
import com.stockmaster.app.data.entity.Staff
import com.stockmaster.app.data.entity.StockMovement

@Database(
    entities = [
        Product::class,
        Customer::class,
        Sale::class,
        SaleItem::class,
        Staff::class,
        StockMovement::class,
        AuditLog::class
    ],
    version = 2,
    exportSchema = false
)
abstract class StockMasterDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun saleDao(): SaleDao
    abstract fun saleItemDao(): SaleItemDao
    abstract fun staffDao(): StaffDao
    abstract fun stockMovementDao(): StockMovementDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: StockMasterDatabase? = null

        fun getDatabase(context: Context): StockMasterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockMasterDatabase::class.java,
                    "stockmaster_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
