package com.ricko.belablok.db

import java.util.*

data class Game(
    val mi: Int,
    val vi: Int,
    val miZvanja: Int,
    val viZvanja: Int,
    val matchId: String,
    val id: String = UUID.randomUUID().toString()
) {
}