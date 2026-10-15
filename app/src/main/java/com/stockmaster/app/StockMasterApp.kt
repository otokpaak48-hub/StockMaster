package com.stockmaster.app

import android.app.Application
import com.stockmaster.app.data.StockMasterDatabase
import com.stockmaster.app.data.StockRepository

class StockMasterApp : Application() {

    // by lazy = built only when first needed, then shared forever
    val database: StockMasterDatabase by lazy {
        StockMasterDatabase.getDatabase(this)
    }

    val repository: StockRepository by lazy {
        StockRepository(database)
    }
}
