package com.development_felber.dartapp.data.persistent.database.finished_leg

import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.development_felber.dartapp.data.PlayerOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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


    fun getAllSoloLegs(): Flow<List<FinishedLeg>> {
        return getAllLegs().asFlow().map { legs ->
            legs.filter { leg ->
                leg.playerOption == PlayerOption.Solo
            }
        }
    }

}
