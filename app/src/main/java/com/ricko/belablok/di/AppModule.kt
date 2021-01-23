package com.ricko.belablok.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.room.Room
import com.ricko.belablok.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.createDataStore("currentMatchIdStorage")

    @Singleton
    @Provides
    fun provideAppDatabase(app: Application) =
        Room.databaseBuilder(app, AppDatabase::class.java, "DATABASE_NAME")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideGameDao(db: AppDatabase) = db.getGameDao()
}