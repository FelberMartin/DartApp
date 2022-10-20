package com.development_felber.dartapp.di

import android.content.Context
import androidx.room.Room
import com.development_felber.dartapp.data.persistent.database.FakeLegDatabaseDao
import com.development_felber.dartapp.data.persistent.database.LegDatabase
import com.development_felber.dartapp.data.persistent.database.LegDatabaseDao
import com.development_felber.dartapp.data.persistent.database.TestLegData
import com.development_felber.dartapp.data.persistent.keyvalue.IKeyValueStorage
import com.development_felber.dartapp.data.persistent.keyvalue.KeyValueStorage
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Named
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
        inMemory = true,
        exampleData = true
    )

    private fun buildRoomDatabase(context: Context, inMemory: Boolean, exampleData: Boolean): LegDatabase {
        val database = if (!inMemory) {
            Room.databaseBuilder(context, LegDatabase::class.java, DATABASE_NAME).build()
        } else {
            Room.inMemoryDatabaseBuilder(context, LegDatabase::class.java).build()
        }

        if (exampleData) {
            GlobalScope.launch {
                database.legDatabaseDao().clear()
                val testData = TestLegData.createExampleLegs()
                for (leg in testData) {
                    database.legDatabaseDao().insert(leg)
                }
            }
        }
        return database
    }

    @Singleton
    @Provides
    fun provideLegDatabaseDao(
        database: LegDatabase
    ) = database.legDatabaseDao()

    @Singleton
    @Provides
    @Named("fake_leg_dao")
    fun provideExampleDataDatabase(): LegDatabaseDao = FakeLegDatabaseDao(fillWithTestData = false)

    @Singleton
    @Provides
    fun provideKeyValueStorage(
        @ApplicationContext context: Context
    ): IKeyValueStorage = KeyValueStorage(context)

    @Singleton
    @Provides
    fun providesNavigationManager() = NavigationManager()
}