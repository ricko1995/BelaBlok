package com.ricko.belablok.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 2, entities = [Game::class, Match::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun getGameDao(): GameDao
}