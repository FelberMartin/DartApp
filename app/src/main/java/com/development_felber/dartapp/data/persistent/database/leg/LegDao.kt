package com.development_felber.dartapp.data.persistent.database.leg

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LegDao {

    @Insert
    suspend fun insert(leg: Leg)

    @Update
    fun update(leg: Leg)

    @Query("SELECT * FROM legs_table WHERE id = :id")
    suspend fun get(id: Long): Leg?

    @Query("DELETE FROM legs_table")
    fun clear()

    @Query("SELECT * FROM legs_table ORDER BY end_time")
    fun getAllLegs(): LiveData<List<Leg>>

    @Query("SELECT * FROM legs_table ORDER BY end_time DESC LIMIT 1")
    suspend fun getLatestLeg(): Leg?

}
