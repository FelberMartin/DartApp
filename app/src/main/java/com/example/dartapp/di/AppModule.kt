package com.example.dartapp.di

import android.content.Context
import androidx.room.Room
import com.example.dartapp.data.persistent.keyvalue.IKeyValueStorage
import com.example.dartapp.data.persistent.keyvalue.KeyValueStorage
import com.example.dartapp.data.persistent.database.ExampleLegDatabase
import com.example.dartapp.data.persistent.database.LegDatabase
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideShoppingItemDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, LegDatabase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    @Named("example_data_db")
    fun provideExampleDataDatabase(
        @ApplicationContext context: Context
    ) = ExampleLegDatabase.generate(context)

    @Singleton
    @Provides
    fun provideShoppingDao(
        database: LegDatabase
    ) = database.legDatabaseDao()

    @Singleton
    @Provides
    fun provideKeyValueStorage(
        @ApplicationContext context: Context
    ): IKeyValueStorage = KeyValueStorage(context)

    @Singleton
    @Provides
    fun providesNavigationManager() = NavigationManager()
}