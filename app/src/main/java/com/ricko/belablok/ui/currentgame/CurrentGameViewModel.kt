package com.ricko.belablok.ui.currentgame

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.EditText
import androidx.core.view.setPadding
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.ricko.belablok.R
import com.ricko.belablok.db.Game
import com.ricko.belablok.db.Match
import com.ricko.belablok.repository.MainRepository
import com.ricko.belablok.ui.masterfragment.MasterFragmentDirections
import com.ricko.belablok.util.Constants.MAX_SCORE_KEY
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import java.util.*

class CurrentGameViewModel @ViewModelInject constructor(
    private val repository: MainRepository,
    @ActivityContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val player1Name = MutableLiveData(context.getString(R.string.team1_name))
    val player2Name = MutableLiveData(context.getString(R.string.team2_name))

    val player1Sum = MutableLiveData(0)
    val player2Sum = MutableLiveData(0)

    val player1Diff = MutableLiveData("")
    val player2Diff = MutableLiveData("")

    var isGameOver = true
    val lastMatch = repository.getLatestMatchWithGames()

//    var maxScore = MutableStateFlow(1000)

    val isP1ScoreBoxSelected = MutableLiveData(false)
    val isP2ScoreBoxSelected = MutableLiveData(false)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.data.map { it[MAX_SCORE_KEY] ?: 1000 }.collect { mScore ->
                withTimeout(2000) {
                    while (lastMatch.value == null) delay(100)
                    if (lastMatch.value!!.games.sumOf { it.player1Score + it.player1Callings } < mScore &&
                        lastMatch.value!!.games.sumOf { it.player2Score + it.player2Callings } < mScore ||
                        (lastMatch.value!!.games.sumOf { it.player1Score + it.player1Callings } == lastMatch.value!!.games.sumOf { it.player2Score + it.player2Callings }))
                        isGameOver = false
                    withContext(Dispatchers.Main) {
                        player1Name.value = lastMatch.value!!.match.player1Name
                        player2Name.value = lastMatch.value!!.match.player2Name
                    }
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
            setPositiveButton(context.getString(R.string.ok_text)) { _, _ ->
                println(lastMatch.value == null)
                println(currentName)
                when (currentName) {
                    player1Name.value -> {
                        player1Name.value = et.text.toString().trim()
                        if (lastMatch.value == null) viewModelScope.launch(Dispatchers.IO) { createNewMatch() }
                        else CoroutineScope(Dispatchers.IO).launch { updateMatch(et.text.toString(), true) }
                    }
                    player2Name.value -> {
                        player2Name.value = et.text.toString().trim()
                        if (lastMatch.value == null) viewModelScope.launch(Dispatchers.IO) { createNewMatch() }
                        else viewModelScope.launch(Dispatchers.IO) { updateMatch(et.text.toString(), false) }
                    }
                }
            }
            setNegativeButton(context.getString(R.string.cancel_text)) { _, _ -> }
            setTitle(context.getString(R.string.rename_text))
        }.create().apply {
            setView(et)
            show()
        }
    }

    suspend fun createNewMatch() {
        val matchId = UUID.randomUUID().toString()
        val match = Match(player1Name.value.toString(), player2Name.value.toString(), id = matchId)
        repository.insertMatch(match)
        isGameOver = false
    }

    private suspend fun updateMatch(newName: String, updateP1: Boolean) {
//        println("/////////////////////////////////////")
//        viewModelScope.launch {
        println("**********************************")
        if (updateP1) repository.insertMatch(lastMatch.value!!.match.copy(player1Name = newName))
        else repository.insertMatch(lastMatch.value!!.match.copy(player2Name = newName))
//        }
    }

    fun View.onNewGameClick() {
        viewModelScope.launch(Dispatchers.Main) {
            dataStore.data.map { it[MAX_SCORE_KEY] ?: 1000 }.collect { maxScore ->
                if (lastMatch.value == null || player1Sum.value!! > maxScore || player2Sum.value!! > maxScore) {
                    createNewMatch()
                    return@collect
                }
                if (player1Sum.value!! < maxScore && player2Sum.value!! < maxScore && player1Sum.value!! > 0 && player2Sum.value!! > 0) {
                    AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.delete_game_in_progress_text))
                        .setNegativeButton(context.getString(R.string.delete_text)) { _, _ ->
                            for (game in lastMatch.value!!.games) viewModelScope.launch(Dispatchers.IO) {
                                repository.deleteGame(game.id)
                                repository.insertMatch(lastMatch.value!!.match.copy(creationTime = System.currentTimeMillis()))
                            }
                        }
                        .setNeutralButton(context.getString(R.string.save_game_text)) { _, _ -> viewModelScope.launch(Dispatchers.IO) { createNewMatch() } }
                        .setPositiveButton(context.getString(R.string.cancel_text)) { _, _ -> }
                        .create()
                        .show()
                } else {
                    withContext(Dispatchers.IO) {
                        repository.insertMatch(lastMatch.value!!.match.copy(creationTime = System.currentTimeMillis()))
                    }
                }
            }
        }
    }

    fun calcDiff() {
        val p1Diff = player1Sum.value!! - player2Sum.value!!
        val p2Diff = player2Sum.value!! - player1Sum.value!!
        player1Diff.value = when {
            p1Diff > 0 -> "+$p1Diff"
            else -> ""
        }
        player2Diff.value = when {
            p2Diff > 0 -> "+$p2Diff"
            else -> ""
        }
    }

    fun View.onHistoryClick() {
        findNavController().navigate(MasterFragmentDirections.actionMasterFragmentToAllGamesFragment())
    }

    suspend fun insertGame(game: Game) {
        repository.insertGame(game)
    }

    suspend fun deleteGame(game: Game) {
        repository.deleteGame(game.id)
    }
}