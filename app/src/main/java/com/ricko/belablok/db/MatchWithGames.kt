package com.ricko.belablok.db

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import java.text.SimpleDateFormat
import java.util.*

@Keep
data class MatchWithGames(
    @Embedded val match: Match,
    @Relation(
        parentColumn = "id",
        entityColumn = "matchId"
    )
    val games: List<Game>
) {
    fun getPlayer1Score(): String {
        val score = games.map { it.player1Score }.sum() + games.map { it.player1Callings }.sum()
        return score.toString()
    }

    fun getPlayer2Score(): String {
        val score = games.map { it.player2Score }.sum() + games.map { it.player2Callings }.sum()
        return score.toString()
    }

    fun getDatePlayed(): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy\nHH:mm")
        val date = Date(match.creationTime)
        return sdf.format(date)
    }
}
