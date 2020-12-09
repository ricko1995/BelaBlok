package com.ricko.belablok.db

import android.view.View
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
){
    fun getPlayer1ScorePlusCallings():String{
        if (player1Callings == 0) return ""
        return "$player1Score + $player1Callings"
    }
    fun getPlayer2ScorePlusCallings():String{
        if (player2Callings == 0) return ""
        return "$player2Score + $player2Callings"
    }

    fun getPlayer1TextViewVisibility(): Int{
        if (player1Callings == 0) return View.GONE
        return View.VISIBLE
    }
    fun getPlayer2TextViewVisibility(): Int{
        if (player2Callings == 0) return View.GONE
        return View.VISIBLE
    }
}