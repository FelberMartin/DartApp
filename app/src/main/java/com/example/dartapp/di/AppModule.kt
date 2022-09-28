package com.example.dartapp.di

import android.content.Context
import androidx.room.Room
import com.example.dartapp.data.persistent.database.FakeLegDatabaseDao
import com.example.dartapp.data.persistent.database.LegDatabase
import com.example.dartapp.data.persistent.database.LegDatabaseDao
import com.example.dartapp.data.persistent.keyvalue.IKeyValueStorage
import com.example.dartapp.data.persistent.keyvalue.KeyValueStorage
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideLegDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, LegDatabase::class.java, DATABASE_NAME).build()


//    @Singleton
//    @Provides
//    fun provideLegDatabaseDao(
//        database: LegDatabase
//    ) = database.legDatabaseDao()

    @Singleton
    @Provides
//    @Named("fake_leg_dao")
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