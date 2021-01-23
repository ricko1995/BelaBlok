package com.ricko.belablok.repository

import com.ricko.belablok.db.Game
import com.ricko.belablok.db.GameDao
import com.ricko.belablok.db.Match
import com.ricko.belablok.db.MatchWithGames
import javax.inject.Inject

class MainRepository @Inject constructor(private val dao: GameDao) {

    suspend fun insertGame(game: Game) = dao.insertGame(game)
    suspend fun insertMatch(match: Match) = dao.insertMatch(match)

    suspend fun getMatchWithGames(matchId: String) = dao.getMatchWithGames(matchId)
    suspend fun getAllMatchesWithGames() = dao.getAllMatchesWithGames()

    //    suspend fun getLatestMatchWithGames():MatchWithGames? {
//      return dao.getAllMatchesWithGames()[0]
//    }
    fun getLatestMatchWithGames() = dao.getLatestMatchWithGames()

    suspend fun deleteAll() {
        dao.deleteAllGames()
        dao.deleteAllMatches()
    }

    suspend fun deleteGame(gameId: String) = dao.deleteGame(gameId)
    suspend fun deleteMatch(matchId: String) = dao.deleteMatch(matchId)
}