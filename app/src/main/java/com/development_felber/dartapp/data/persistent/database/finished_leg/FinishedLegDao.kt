package com.development_felber.dartapp.data.persistent.database.finished_leg

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FinishedLegDao {

    @Insert
    suspend fun insert(leg: FinishedLeg)

    @Insert
    suspend fun insert(legs: List<FinishedLeg>)

    @Update
    fun update(leg: FinishedLeg)

    @Query("SELECT * FROM legs_table WHERE id = :id")
    suspend fun get(id: Long): FinishedLeg?

    @Query("DELETE FROM legs_table")
    fun clear()

    @Query("SELECT * FROM legs_table ORDER BY end_time")
    fun getAllLegs(): LiveData<List<FinishedLeg>>

    @Query("SELECT * FROM legs_table ORDER BY end_time DESC LIMIT 1")
    suspend fun getLatestLeg(): FinishedLeg?

}
