package com.example.pokedex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.TeamRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val teamCount: Int) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    teamRepository: TeamRepository
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = teamRepository.teamCount
        .map { count: Int -> HomeUiState.Success(teamCount = count) as HomeUiState }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading
        )
}
