package com.example.dartapp.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Leg::class], version = 4, exportSchema = false)
abstract class LegDatabase: RoomDatabase() {

    abstract val legDatabaseDao: LegDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: LegDatabase? = null

        fun getInstance(context: Context): LegDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        LegDatabase::class.java,
                        "leg_history_database")
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}