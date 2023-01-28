package com.development_felber.dartapp.data.persistent.database.dart_set

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DartSetDao {

    @Insert()
    suspend fun insert(dartSet: DartSet)

    @Query("SELECT * FROM dart_sets_table WHERE id = :id")
    suspend fun get(id: Long): DartSet?

    @Query("SELECT * FROM dart_sets_table ORDER BY id")
    suspend fun getAll(): List<DartSet>
}