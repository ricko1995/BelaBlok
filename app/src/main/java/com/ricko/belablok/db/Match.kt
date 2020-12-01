package com.ricko.belablok.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Match(
    var player1Name: String,
    var player2Name: String,
    val creationTime: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString()
)
