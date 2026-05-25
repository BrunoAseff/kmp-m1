package com.example.pokedex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.Pokemon
import com.example.pokedex.data.TeamRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface TeamUiState {
    data object Loading : TeamUiState
    data class Success(val team: List<Pokemon>) : TeamUiState
    data class Error(val message: String) : TeamUiState
}

class TeamViewModel : ViewModel() {
    val uiState: StateFlow<TeamUiState> = TeamRepository.team
        .map { TeamUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TeamUiState.Loading
        )

    fun removeFromTeam(pokemonId: Int) {
        TeamRepository.removeFromTeam(pokemonId)
    }
}
