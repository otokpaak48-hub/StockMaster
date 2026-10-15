package com.stockmaster.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stockmaster.app.data.StockRepository
import com.stockmaster.app.data.TopProduct
import kotlinx.coroutines.flow.*
import java.util.Calendar

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = StockRepository.get(app)

    private fun startOfDay(offsetDays: Int = 0): Long = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, offsetDays)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private val todayStart = startOfDay()
    private val weekStart = startOfDay(-6)

    val todayRevenue: StateFlow<Double> =
        repo.sales.revenueBetween(todayStart, Long.MAX_VALUE)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val todaySalesCount: StateFlow<Int> =
        repo.sales.countBetween(todayStart, Long.MAX_VALUE)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val stockValue: StateFlow<Double> =
        repo.products.stockValue()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val productCount: StateFlow<Int> =
        repo.products.count()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val lowStock = repo.products.getLowStock()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val topProducts = repo.sales.topProducts(weekStart, Long.MAX_VALUE, 5)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList<TopProduct>())
}
