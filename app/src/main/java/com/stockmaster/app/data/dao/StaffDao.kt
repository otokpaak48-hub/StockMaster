package com.stockmaster.app.data.dao

import androidx.room.*
import com.stockmaster.app.data.entity.Staff
import kotlinx.coroutines.flow.Flow

@Dao
interface StaffDao {

    @Insert
    suspend fun insertStaff(staff: Staff): Long

    @Update
    suspend fun updateStaff(staff: Staff)

    @Delete
    suspend fun deleteStaff(staff: Staff)

    @Query("SELECT * FROM staff ORDER BY name ASC")
    fun getAllStaff(): Flow<List<Staff>>

    @Query("SELECT * FROM staff WHERE id = :staffId")
    suspend fun getStaffById(staffId: Long): Staff?

    @Query("SELECT * FROM staff WHERE name = :name")
    suspend fun getStaffByName(name: String): Staff?

    // One-shot roster for hash-based login (no Flow needed)
    @Query("SELECT * FROM staff")
    suspend fun getAllStaffOnce(): List<Staff>

    // Seeder checks this before forging Admin
    @Query("SELECT COUNT(*) FROM staff")
    suspend fun countStaff(): Int
}
