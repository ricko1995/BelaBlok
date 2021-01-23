package com.ricko.belablok.ui.gameentry

import android.view.View
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.button.MaterialButtonToggleGroup
import com.ricko.belablok.R
import com.ricko.belablok.db.Game
import com.ricko.belablok.repository.MainRepository
import kotlinx.coroutines.launch

class GameEntryViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

    val selectedPlayer1 = MutableLiveData(true)
    val selectedPlayer2 = MutableLiveData(false)

    val player1Score = MutableLiveData("0")
    val player2Score = MutableLiveData("0")

    val player1Callings = MutableLiveData("")
    val player2Callings = MutableLiveData("")
    val lastMatch = repository.getLatestMatchWithGames()

    fun View.onScoreClick() {
        if (id == R.id.player1Score) {
            selectedPlayer1.value = !selectedPlayer1.value!!
            selectedPlayer2.value = false
        }
        if (id == R.id.player2Score) {
            selectedPlayer2.value = !selectedPlayer2.value!!
            selectedPlayer1.value = false
        }
    }

    fun onScoreBtnClick(number: Int) {
        val scoreToCalculate = if (selectedPlayer1.value!!) player1Score else if (selectedPlayer2.value!!) player2Score else return
        val otherPlayerScore = if (selectedPlayer2.value!!) player1Score else if (selectedPlayer1.value!!) player2Score else return
        if (number > 15 || number < -15) {
            calculateCallings(number)
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
        val scoreToCalculate = if (selectedPlayer1.value!!) player1Score else if (selectedPlayer2.value!!) player2Score else return
        val otherPlayerScore = if (selectedPlayer2.value!!) player1Score else if (selectedPlayer1.value!!) player2Score else return
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

    private fun calculateCallings(number: Int) {
        if (selectedPlayer1.value!! && number != 20 && number > 0) player2Callings.value = ""
        if (selectedPlayer2.value!! && number != 20 && number > 0) player1Callings.value = ""

        val listOfCallingsP1 = if (player1Callings.value.isNullOrEmpty()) emptyList() else player1Callings.value!!.split("+").map { it.toInt() }
        val listOfCallingsP2 = if (player2Callings.value.isNullOrEmpty()) emptyList() else player2Callings.value!!.split("+").map { it.toInt() }

        if ((listOfCallingsP1.size + listOfCallingsP2.size) > 4 && number > 0) return
        if (selectedPlayer1.value!! && listOfCallingsP1.filter { it == number }.size > if (number == 20) 4 else 3) return
        if (selectedPlayer2.value!! && listOfCallingsP2.filter { it == number }.size > if (number == 20) 4 else 3) return

        if (selectedPlayer1.value!!) addCallings(number, player1Callings, player2Callings)
        else addCallings(number, player2Callings, player1Callings)
    }

    private fun addCallings(number: Int, callingsToCalculate: MutableLiveData<String?>, otherPlayerCallings: MutableLiveData<String?>) {
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

    fun View.onToggleBtnClick(toggleGroup: MaterialButtonToggleGroup) {
        if (toggleGroup.checkedButtonIds.isNullOrEmpty()) return
        if (id == R.id.player1FullWin) {
            player1Score.value = "162"
            player2Score.value = "0"
            player2Callings.value = ""
        } else if (id == R.id.player2FullWin) {
            player2Score.value = "162"
            player1Score.value = "0"
            player1Callings.value = ""
        }
    }


    fun View.onConfirmEntryClick(toggleGroup: MaterialButtonToggleGroup) {
        if (player1Score.value == "0" && player2Score.value == "0" || player1Score.value == "81") {
            Toast.makeText(context, context.getString(R.string.wrong_entry_text), Toast.LENGTH_SHORT).show()
            return
        }
        if (player1Score.value == "0") player1Callings.value = ""
        if (player2Score.value == "0") player2Callings.value = ""
        var player1TotalCallings = 0
        var player2TotalCallings = 0
        player1Callings.value?.let {
            if (it.isNotEmpty()) {
                val list = it.split("+").map { i -> i.toInt() }
                list.forEach { i -> player1TotalCallings += i }
            }
        }
        player2Callings.value?.let {
            if (it.isNotEmpty()) {
                val list = it.split("+").map { i -> i.toInt() }
                list.forEach { i -> player2TotalCallings += i }
            }
        }
        if (toggleGroup.checkedButtonId == R.id.player1FullWin) {
            player1TotalCallings += 90
            player2TotalCallings = 0
        }
        if (toggleGroup.checkedButtonId == R.id.player2FullWin) {
            player2TotalCallings += 90
            player1TotalCallings = 0
        }

        viewModelScope.launch {
            insertGameInDb(player1TotalCallings, player2TotalCallings)
            toggleGroup.clearChecked()
            onCancelClick()
        }
    }

    private suspend fun insertGameInDb(player1TotalCallings: Int, player2TotalCallings: Int) {
        if (lastMatch.value!!.games.isNullOrEmpty()) repository.insertMatch(lastMatch.value!!.match.copy(creationTime = System.currentTimeMillis()))
        val matchId = lastMatch.value!!.match.id
        val p1s = player1Score.value!!.toInt()
        val p2s = player2Score.value!!.toInt()
        val game = Game(p1s, p2s, player1TotalCallings, player2TotalCallings, matchId)
        repository.insertGame(game)
    }
}