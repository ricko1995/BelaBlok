package com.ricko.belablok.ui.gameentry

import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textview.MaterialTextView
import com.ricko.belablok.R

class GameEntryViewModel : ViewModel() {

    private var selectedPlayer1: Boolean = true
    private var selectedPlayer2: Boolean = false

    val player1Score = MutableLiveData("0")
    val player2Score = MutableLiveData("0")

    val player1Callings = MutableLiveData("")
    val player2Callings = MutableLiveData("")

    fun View.onScoreClick(v: View) {
        v.isSelected = false
        selectedPlayer1 = false
        selectedPlayer2 = false
        isSelected = !isSelected

        if (isSelected && id == R.id.player1Score) selectedPlayer1 = true
        if (isSelected && id == R.id.player2Score) selectedPlayer2 = true
    }

    fun onScoreBtnClick(number: Int) {
        val scoreToCalculate = if (selectedPlayer1) player1Score else if (selectedPlayer2) player2Score else return
        val otherPlayerScore = if (selectedPlayer2) player1Score else if (selectedPlayer1) player2Score else return
        val callingsToCalculate = if (selectedPlayer1) player1Callings else if (selectedPlayer2) player2Callings else return
        val otherPlayerCallings = if (selectedPlayer2) player1Callings else if (selectedPlayer1) player2Callings else return
        if (number > 15 || number < -15) {
            addCallings(number, callingsToCalculate, otherPlayerCallings)
            return
        }

        if (scoreToCalculate.value?.length!! > 2) return
        val temp = StringBuilder(scoreToCalculate.value!!).append(number).toString()
        if (temp.toInt() > 162) return
        if (scoreToCalculate.value == "0") scoreToCalculate.value = number.toString()
        else scoreToCalculate.value = temp
        calculateScoreForOtherPlayer(scoreToCalculate, otherPlayerScore)
    }

    private fun calculateScoreForOtherPlayer(scoreToCalculate: MutableLiveData<String>, otherPlayerScore: MutableLiveData<String>) {
        val p1 = scoreToCalculate.value!!.toInt()
        val p2 = 162 - p1
        otherPlayerScore.value = p2.toString()
    }

    fun onBackspaceClick() {
        val scoreToCalculate = if (selectedPlayer1) player1Score else if (selectedPlayer2) player2Score else return
        val otherPlayerScore = if (selectedPlayer2) player1Score else if (selectedPlayer1) player2Score else return
        if (scoreToCalculate.value?.length!! < 2) scoreToCalculate.value = "0"
        else scoreToCalculate.value = scoreToCalculate.value?.dropLast(1)
        calculateScoreForOtherPlayer(scoreToCalculate, otherPlayerScore)
    }

    fun onCancelClick() {
        player1Score.value = "0"
        player2Score.value = "0"
        player1Callings.value = ""
        player2Callings.value = ""
    }

    private fun addCallings(number: Int, callingsToCalculate: MutableLiveData<String>, otherPlayerCallings: MutableLiveData<String>) {
        when (number) {
            in 0..100 -> {
                callingsToCalculate.value = StringBuilder(callingsToCalculate.value!!)
                    .append(if (callingsToCalculate.value.isNullOrEmpty()) "" else "+")
                    .append(number).toString()
            }
            in 150..200 -> {
                if (callingsToCalculate.value!!.contains(number.toString()) || otherPlayerCallings.value!!.contains(number.toString())) return
                callingsToCalculate.value = StringBuilder(callingsToCalculate.value!!)
                    .append(if (callingsToCalculate.value.isNullOrEmpty()) "" else "+")
                    .append(number).toString()
            }
            in -200..0 -> {
                if (callingsToCalculate.value.isNullOrEmpty()) return
                val callings = callingsToCalculate.value?.split("+")?.map { it.toInt() } as MutableList<Int>
                callings.remove(number * -1)
                val newCallings = StringBuilder("")
                callings.forEach {
                    newCallings.append("+").append(it)
                }
                callingsToCalculate.value = newCallings.toString()
                if (!callingsToCalculate.value.isNullOrEmpty() && callingsToCalculate.value?.first() == '+')
                    callingsToCalculate.value = callingsToCalculate.value?.drop(1)
            }
        }
    }

    fun View.onToggleBtnClick() {
        if (id == R.id.player1FullWin) {
            player1Score.value = "162"
            player2Score.value = "0"
        } else if (id == R.id.player2FullWin) {
            player2Score.value = "162"
            player1Score.value = "0"
        }
    }


    fun View.onConfirmEntryClick(toggleGroup: MaterialButtonToggleGroup) {
        if (player1Score.value == "0" && player2Score.value == "0") {
            Toast.makeText(context, "Krivi Unos!", Toast.LENGTH_SHORT).show()
            return
        }
        if (player1Score.value == "0") player1Callings.value = ""
        if (player2Score.value == "0") player2Callings.value = ""
        var player1TotalScore = player1Score.value!!.toInt()
        var player2TotalScore = player2Score.value!!.toInt()
        player1Callings.value?.let {
            if (it.isNotEmpty()) {
                val list = it.split("+").map { i -> i.toInt() }
                list.forEach { i -> player1TotalScore += i }
            }
        }
        player2Callings.value?.let {
            if (it.isNotEmpty()) {
                val list = it.split("+").map { i -> i.toInt() }
                list.forEach { i -> player2TotalScore += i }
            }
        }
        if (toggleGroup.checkedButtonId == R.id.player1FullWin) {
            player1TotalScore += 90
            player2TotalScore = 0
        }
        if (toggleGroup.checkedButtonId == R.id.player2FullWin) {
            player2TotalScore += 90
            player1TotalScore = 0
        }

        onCancelClick()

        println("----------------------------")
        println(player1TotalScore)
        println(player2TotalScore)
        println("----------------------------")
    }
}