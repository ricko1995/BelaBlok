package com.ricko.belablok.db

import androidx.lifecycle.LiveData
import androidx.room.Embedded
import androidx.room.Relation

data class MatchWithGames(
    @Embedded val match: Match,
    @Relation(
        parentColumn = "id",
        entityColumn = "matchId"
    )
    val games: List<Game>
)
