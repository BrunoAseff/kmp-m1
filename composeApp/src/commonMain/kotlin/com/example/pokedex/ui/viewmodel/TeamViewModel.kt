package com.example.pokedex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.TeamPokemon
import com.example.pokedex.data.TeamRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface TeamUiState {
    data object Loading : TeamUiState
    data class Success(val team: List<TeamPokemon>) : TeamUiState
    data class Error(val message: String) : TeamUiState
}

class TeamViewModel(
    private val teamRepository: TeamRepository
) : ViewModel() {
    val uiState: StateFlow<TeamUiState> = teamRepository.team
        .map { team -> TeamUiState.Success(team) as TeamUiState }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TeamUiState.Loading
        )

    fun removeFromTeam(pokemonId: Int) {
        viewModelScope.launch {
            teamRepository.removeFromTeam(pokemonId)
        }
    }
}
