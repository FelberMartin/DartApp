package com.example.dartapp.data.persistent.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LegDatabaseDao {

    @Insert
    fun insert(leg: Leg)

    @Update
    fun update(leg: Leg)

    @Query("SELECT * FROM legs_table WHERE id = :id")
    fun get(id: Long): Leg?

    @Query("DELETE FROM legs_table")
    fun clear()

    @Query("SELECT * FROM legs_table ORDER BY end_time DESC")
    fun getAllLegs(): List<Leg>

    @Query("SELECT * FROM legs_table ORDER BY end_time DESC LIMIT 1")
    fun getLatestLeg(): Leg?

}
