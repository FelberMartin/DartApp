package com.example.dartapp.database

import androidx.lifecycle.LiveData
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

    @Query("SELECT * FROM legs_table WHERE id = :key")
    fun get(key: Long): Leg?

    @Query("DELETE FROM legs_table")
    fun clear()

    @Query("SELECT * FROM legs_table ORDER BY end_time_milli DESC")
    fun getAllLegs(): LiveData<List<Leg>>

    @Query("SELECT * FROM legs_table ORDER BY end_time_milli DESC LIMIT 1")
    fun getLatestLeg(): Leg?
}
