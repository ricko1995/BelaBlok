package com.ricko.belablok.ui.allgames

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.ricko.belablok.db.MatchWithGames
import com.ricko.belablok.repository.MainRepository

class AllMatchesViewModel @ViewModelInject constructor(private val repository: MainRepository) : ViewModel() {
    suspend fun matchesWithGames(): List<MatchWithGames> {
        return repository.getAllMatchesWithGames()
    }
}