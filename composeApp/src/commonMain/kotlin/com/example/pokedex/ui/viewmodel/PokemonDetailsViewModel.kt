package com.example.pokedex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.Pokemon
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.data.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface PokemonDetailsUiState {
    data object Loading : PokemonDetailsUiState
    data class Success(
        val pokemon: Pokemon,
        val isInTeam: Boolean
    ) : PokemonDetailsUiState
    data class Error(val message: String) : PokemonDetailsUiState
}

class PokemonDetailsViewModel(private val pokemonId: Int) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PokemonDetailsUiState> = combine(
        TeamRepository.team.map { team -> team.any { it.id == pokemonId } },
        _isLoading,
        _errorMessage
    ) { isInTeam, isLoading, errorMessage ->
        if (errorMessage != null) {
            PokemonDetailsUiState.Error(errorMessage)
        } else if (isLoading) {
            PokemonDetailsUiState.Loading
        } else {
            val pokemon = PokemonRepository.getPokemonById(pokemonId)
            if (pokemon != null) {
                PokemonDetailsUiState.Success(pokemon, isInTeam)
            } else {
                PokemonDetailsUiState.Error("Pokémon não encontrado")
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PokemonDetailsUiState.Loading
    )

    init {
        loadPokemon()
    }

    private fun loadPokemon() {
        viewModelScope.launch {
            _isLoading.value = true
            kotlinx.coroutines.delay(300) // Simulação
            _isLoading.value = false
        }
    }

    fun addToTeam() {
        val pokemon = PokemonRepository.getPokemonById(pokemonId)
        if (pokemon != null) {
            TeamRepository.addToTeam(pokemon)
        }
    }
}
