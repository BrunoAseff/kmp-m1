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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface PokedexUiState {
    data object Loading : PokedexUiState
    data class Success(
        val pokemons: List<Pokemon>,
        val query: String = "",
        val selectedType: String = "ALL",
        val teamIds: Set<Int> = emptySet()
    ) : PokedexUiState
    data class Error(val message: String) : PokedexUiState
}

class PokedexViewModel : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _selectedType = MutableStateFlow("ALL")
    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PokedexUiState> = combine(
        _query,
        _selectedType,
        TeamRepository.team,
        _isLoading,
        _errorMessage
    ) { query, selectedType, team, isLoading, errorMessage ->
        if (errorMessage != null) {
            PokedexUiState.Error(errorMessage)
        } else if (isLoading) {
            PokedexUiState.Loading
        } else {
            val pokemons = PokemonRepository.getPokemonList()
                .filter { pokemon ->
                    val matchesQuery = query.isBlank() ||
                        pokemon.name.contains(query.trim(), ignoreCase = true) ||
                        pokemon.id.toString().contains(query.trim())
                    val matchesType = selectedType == "ALL" || pokemon.types.any { it.name == selectedType }
                    matchesQuery && matchesType
                }
                .sortedBy { it.id }
            
            PokedexUiState.Success(
                pokemons = pokemons,
                query = query,
                selectedType = selectedType,
                teamIds = team.map { it.id }.toSet()
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PokedexUiState.Loading
    )

    init {
        loadPokemons()
    }

    private fun loadPokemons() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // Simulando um delay de rede/banco
                kotlinx.coroutines.delay(500)
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar Pokémon: ${e.message}"
            }
        }
    }

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun updateSelectedType(type: String) {
        _selectedType.value = type
    }
}
