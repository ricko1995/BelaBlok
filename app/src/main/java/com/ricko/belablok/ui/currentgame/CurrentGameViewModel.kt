package com.ricko.belablok.ui.currentgame

import android.app.AlertDialog
import android.view.View
import android.widget.EditText
import androidx.core.view.setPadding
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.ricko.belablok.db.Game
import com.ricko.belablok.db.Match
import com.ricko.belablok.repository.MainRepository
import com.ricko.belablok.ui.masterfragment.MasterFragmentDirections
import kotlinx.coroutines.*
import java.util.*

class CurrentGameViewModel @ViewModelInject constructor(private val repository: MainRepository) : ViewModel() {

    val player1Name = MutableLiveData("MI")
    val player2Name = MutableLiveData("VI")

    val player1Sum = MutableLiveData(0)
    val player2Sum = MutableLiveData(0)
    val lastMatch = repository.getLatestMatchWithGames()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            withTimeout(1000) {
                while (lastMatch.value == null) Unit
                withContext(Dispatchers.Main) {
                    player1Name.value = lastMatch.value!!.match.player1Name
                    player2Name.value = lastMatch.value!!.match.player2Name
                }
            }
        }

    }

    fun View.onPlayerNameClick(currentName: String) {
        val et = EditText(context).apply {
            setPadding(50)
            setText(currentName)
        }
        AlertDialog.Builder(context).apply {
            setPositiveButton("OK") { _, _ ->
                when (currentName) {
                    player1Name.value -> {
                        player1Name.value = et.text.toString()
                        if (lastMatch.value == null) viewModelScope.launch(Dispatchers.IO) { createNewMatch() }
                        else updateMatch(et.text.toString(), true)
                    }
                    player2Name.value -> {
                        player2Name.value = et.text.toString()
                        if (lastMatch.value == null) viewModelScope.launch(Dispatchers.IO) { createNewMatch() }
                        else updateMatch(et.text.toString(), false)
                    }
                }
            }
            setNegativeButton("Cancel") { _, _ -> }
            setTitle("Rename")
        }.create().apply {
            setView(et)
            show()
        }
    }

    suspend fun createNewMatch() {
        val matchId = UUID.randomUUID().toString()
        val match = Match(player1Name.value.toString(), player2Name.value.toString(), id = matchId)
        repository.insertMatch(match)
    }

    private fun updateMatch(newName: String, updateP1: Boolean) {
        viewModelScope.launch {
            if (updateP1) repository.insertMatch(lastMatch.value!!.match.copy(player1Name = newName))
            else repository.insertMatch(lastMatch.value!!.match.copy(player2Name = newName))
        }
    }

    fun View.onNewGameClick() {
        viewModelScope.launch {
            if (lastMatch.value == null || player1Sum.value!! > 1000 || player2Sum.value!! > 1000 || player1Sum.value!! != player1Sum.value!!) {
                createNewMatch()
                return@launch
            }
            if (player1Sum.value!! < 1000 && player2Sum.value!! < 1000 && player1Sum.value!! > 0 && player2Sum.value!! > 0) {
                AlertDialog.Builder(context)
                    .setTitle("Izbriši partiju u tijeku?")
                    .setNegativeButton("Izbriši") { _, _ ->
                        for (game in lastMatch.value!!.games) viewModelScope.launch(Dispatchers.IO) {
                            repository.deleteGame(game.id)
                            repository.insertMatch(lastMatch.value!!.match.copy(creationTime = System.currentTimeMillis()))
                        }
                    }
                    .setNeutralButton("Spremi partiju") { _, _ -> viewModelScope.launch(Dispatchers.IO) { createNewMatch() } }
                    .setPositiveButton("Odustani") { _, _ -> }
                    .create()
                    .show()
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.insertMatch(lastMatch.value!!.match.copy(creationTime = System.currentTimeMillis()))
                }
            }
        }
    }

    fun onLongNewGameClick(): Boolean {
        viewModelScope.launch(Dispatchers.IO) {
//            repository.deleteAll()//TODO REMOVE THIS!!!!!!!!!!!!!!!!!!!!!!
        }
        return true
    }

    fun View.onHistoryClick() {
//        findNavController().navigate(CurrentGameFragmentDirections.actionCurrentGameFragmentToAllGamesFragment())
        findNavController().navigate(MasterFragmentDirections.actionMasterFragmentToAllGamesFragment())
    }

    suspend fun insertGame(game: Game) {
        repository.insertGame(game)
    }

    suspend fun deleteGame(game: Game) {
        repository.deleteGame(game.id)
    }
}