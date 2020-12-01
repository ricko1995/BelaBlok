package com.ricko.belablok.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Game(
    val player1Score: Int,
    val player2Score: Int,
    val player1Callings: Int,
    val player2Callings: Int,
    val matchId: String,
    val creationTime: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString()
)