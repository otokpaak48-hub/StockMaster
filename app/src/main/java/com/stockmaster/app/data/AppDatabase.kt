package com.stockmaster.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Product::class, Customer::class, Staff::class, Sale::class,
        SaleItem::class, StockMovement::class, AuditLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun staffDao(): StaffDao
    abstract fun saleDao(): SaleDao
    abstract fun saleItemDao(): SaleItemDao
    abstract fun stockMovementDao(): StockMovementDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stockmaster.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("INSERT INTO staff (name, role, phone, pin, active, createdAt) VALUES ('Admin', 'Admin', '', '1234', 1, 0)")
                            db.execSQL("INSERT INTO staff (name, role, phone, pin, active, createdAt) VALUES ('Manager', 'Manager', '', '2222', 1, 0)")
                            db.execSQL("INSERT INTO staff (name, role, phone, pin, active, createdAt) VALUES ('Cashier', 'Cashier', '', '3333', 1, 0)")
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
