package com.development_felber.dartapp.di

import android.content.Context
import androidx.room.Room
import com.development_felber.dartapp.data.persistent.database.AppDatabase
import com.development_felber.dartapp.data.persistent.database.TestLegData
import com.development_felber.dartapp.data.persistent.database.player.PlayerDao
import com.development_felber.dartapp.data.persistent.keyvalue.IKeyValueStorage
import com.development_felber.dartapp.data.persistent.keyvalue.KeyValueStorage
import com.development_felber.dartapp.data.repository.PlayerRepository
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideLegDatabase(
        @ApplicationContext context: Context
    ) = buildRoomDatabase(
        context = context,
        inMemory = false,
        exampleData = false
    )

    private fun buildRoomDatabase(context: Context, inMemory: Boolean, exampleData: Boolean): AppDatabase {
        val database = if (!inMemory) {
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
        } else {
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        }

        if (exampleData) {
            GlobalScope.launch {
                database.getLegDao().clear()
                val testData = TestLegData.createExampleLegs()
                for (leg in testData) {
                    database.getLegDao().insert(leg)
                }
            }
        }
        return database
    }

    @Singleton
    @Provides
    fun provideLegDatabaseDao(
        database: AppDatabase
    ) = database.getLegDao()

    @Singleton
    @Provides
    fun provideKeyValueStorage(
        @ApplicationContext context: Context
    ): IKeyValueStorage = KeyValueStorage(context)

    @Singleton
    @Provides
    fun providesNavigationManager() = NavigationManager()

    @Singleton
    @Provides
    fun providesPlayerDao(
        database: AppDatabase
    ) = database.getPlayerDao()

    @Singleton
    @Provides
    fun providesMultiplayerGameDao(
        database: AppDatabase
    ) = database.getMultiplayerGameDao()

    @Singleton
    @Provides
    fun providesDartSetDao(
        database: AppDatabase
    ) = database.getDartSetDao()

    @Singleton
    @Provides
    fun providesPlayerRepository(
        playerDao: PlayerDao,
        keyValueStorage: IKeyValueStorage
    ) = PlayerRepository(keyValueStorage, playerDao)

}