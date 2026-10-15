package com.stockmaster.app.util

import java.text.DecimalFormat

object Money {
    private val money = DecimalFormat("#,##0")
    private val qty = DecimalFormat("#,##0.##")

    fun naira(v: Double): String = "₦${money.format(v)}"
    fun qty(v: Double): String = qty.format(v)
}
