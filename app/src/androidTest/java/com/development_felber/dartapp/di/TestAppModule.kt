package com.development_felber.dartapp.di

import android.content.Context
import androidx.room.Room
import com.development_felber.dartapp.data.persistent.database.LegDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Named("in_memory_db")
    fun provideInMemoryDb(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(context, LegDatabase::class.java)
            .allowMainThreadQueries()
            .build()
}