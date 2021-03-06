package com.ricko.belablok.db

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
@Keep
data class Match(
    var player1Name: String,
    var player2Name: String,
    val creationTime: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString()
)
