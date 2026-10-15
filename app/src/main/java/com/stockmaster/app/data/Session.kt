package com.stockmaster.app.data

/** Holds the signed-in staff member for the current session. */
object Session {
    var currentStaff: Staff? = null

    val staffId: Long get() = currentStaff?.id ?: 0L
    val staffName: String get() = currentStaff?.name ?: "Unknown"
    val role: String get() = currentStaff?.role ?: ""

    val isAdmin: Boolean
        get() = role.equals("Admin", ignoreCase = true) || role.equals("Manager", ignoreCase = true)

    fun clear() { currentStaff = null }
}
