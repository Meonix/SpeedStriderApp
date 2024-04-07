package com.example.basecomposeapplication.di

import android.content.Context
import androidx.room.Room
import com.example.basecomposeapplication.data.local.datastore.Pref
import com.example.basecomposeapplication.data.local.datastore.PreferencesDataStore
import com.example.basecomposeapplication.data.local.room.AppDatabase
import com.example.basecomposeapplication.data.local.room.note.NoteDao
import com.example.basecomposeapplication.data.remote.AppNetworkDataSource
import com.example.basecomposeapplication.data.remote.NetworkDataSource
import com.example.basecomposeapplication.data.repository.Repository
import com.example.basecomposeapplication.data.repository.RepositoryImpl
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "BaseComposeApp.db"
        ).build()
    }

    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao = database.noteDao()
}

@Module
@InstallIn(SingletonComponent::class)
object PreferencesDataStoreModule {
    @Singleton
    @Provides
    fun provideSharePref(@ApplicationContext context: Context, gson: Gson): PreferencesDataStore {
        return PreferencesDataStore(context.applicationContext, gson)
    }

    @Singleton
    @Provides
    fun providePref(sharedPref: PreferencesDataStore): Pref {
        return Pref(sharedPref)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindNetworkDataSource(dataSource: AppNetworkDataSource): NetworkDataSource
}


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun provideRepository(repositoryImpl: RepositoryImpl): Repository
}
