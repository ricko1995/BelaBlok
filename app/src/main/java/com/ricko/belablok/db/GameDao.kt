package com.ricko.belablok.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: Match)

    @Transaction
    @Query("SELECT * FROM `match` WHERE id = :matchId")
    suspend fun getMatchWithGames(matchId: String): List<MatchWithGames>

    @Transaction
    @Query("SELECT * FROM `match` ORDER BY creationTime")
    suspend fun getAllMatchesWithGames(): List<MatchWithGames>

    @Transaction
    @Query("SELECT * FROM `match` ORDER BY creationTime DESC LIMIT 1")
    fun getLatestMatchWithGames(): LiveData<MatchWithGames>
//
//    @Transaction
//    @Query("SELECT * FROM `match` ORDER BY creationTime DESC LIMIT 1")
//    suspend fun getLatestMatchWith(): LiveData<MatchWithGames>

    @Query("DELETE FROM game")
    suspend fun deleteAllGames()

    @Query("DELETE FROM `Match`")
    suspend fun deleteAllMatches()

}