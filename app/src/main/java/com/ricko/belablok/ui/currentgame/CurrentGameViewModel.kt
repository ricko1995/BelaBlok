package com.ricko.belablok.ui.currentgame

import android.app.AlertDialog
import android.view.View
import android.widget.EditText
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricko.belablok.db.Game
import com.ricko.belablok.db.MatchWithGames
import com.ricko.belablok.repository.MainRepository
import com.ricko.belablok.ui.gameentry.GameEntrySheet
import kotlinx.coroutines.launch

class CurrentGameViewModel @ViewModelInject constructor(private val repository: MainRepository) : ViewModel() {

    val player1Name = MutableLiveData("MI")
    val player2Name = MutableLiveData("VI")

    val player1Sum = MutableLiveData(0)
    val player2Sum = MutableLiveData(0)
    val lastMatch = repository.getLatestMatchWithGames()

    fun View.onMiViClick(currentName: String) {
        val et = EditText(context).apply {
            setPadding(50)
            setText(currentName)
        }
        AlertDialog.Builder(context).apply {
            setPositiveButton("OK") { _, _ ->
                when (currentName) {
                    player1Name.value -> player1Name.value = et.text.toString()
                    player2Name.value -> player2Name.value = et.text.toString()
                }
            }
            setNegativeButton("Cancel") { _, _ -> }
            setTitle("Rename")
        }.create().apply {
            setView(et)
            show()
        }
    }

    fun onNewGameClick() {
        viewModelScope.launch { //TODO REMOVE THIS!!!!!!!!!!!!!!!!!!!!!!
            repository.deleteAll()
        }
    }
}