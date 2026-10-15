package com.stockmaster.app.data

import com.stockmaster.app.data.entity.Staff
import com.stockmaster.app.util.PasswordHasher

object AdminSeeder {

    /**
     * Runs on every launch, but only acts once:
     * if the staff table is empty, forges the master Admin account
     * with a salted, hashed PIN.
     */
    suspend fun seedIfEmpty(database: StockMasterDatabase) {
        val staffDao = database.staffDao()

        // Staff already exist? Door is staffed. Do nothing.
        if (staffDao.countStaff() > 0) return

        val salt = PasswordHasher.generateSalt()
        val admin = Staff(
            name = "Admin",
            role = "Admin",
            salt = salt,
            pin = PasswordHasher.hash("1234", salt)
        )
        staffDao.insertStaff(admin)
    }
}
