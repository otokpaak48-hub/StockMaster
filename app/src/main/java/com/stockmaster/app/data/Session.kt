package com.stockmaster.app.data

import com.stockmaster.app.data.entity.Staff

/** Holds the signed-in staff member for the current app session */
object Session {
    var currentStaff: Staff? = null

    val staffId: Long get() = currentStaff?.id ?: 0L
    val staffName: String get() = currentStaff?.name ?: ""
    val role: String get() = currentStaff?.role ?: "Cashier"

    val isAdmin: Boolean
        get() = role.equals("Admin", ignoreCase = true)

    fun clear() { currentStaff = null }
}
