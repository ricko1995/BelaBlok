package com.ricko.belablok.ui.allgames

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricko.belablok.db.MatchWithGames
import com.ricko.belablok.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllMatchesViewModel @ViewModelInject constructor(private val repository: MainRepository) : ViewModel() {

    private val matchesWithGames: MutableLiveData<List<MatchWithGames>> = MutableLiveData()
    val latestGame = repository.getLatestMatchWithGames()

    suspend fun matchesWithGames(): List<MatchWithGames> {
        return repository.getAllMatchesWithGames()
    }
    init {
        viewModelScope.launch(Dispatchers.Main) {
            matchesWithGames.value = matchesWithGames()
        }
    }
}