package com.example.pokedex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.PokemonDetails
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.data.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PokemonDetailsUiState {
    data object Loading : PokemonDetailsUiState
    data class Success(
        val pokemon: PokemonDetails,
        val isInTeam: Boolean,
        val capturedLocation: String,
        val locationError: String?,
        val isSaving: Boolean
    ) : PokemonDetailsUiState

    data class Error(val message: String) : PokemonDetailsUiState
}

class PokemonDetailsViewModel(
    private val pokemonId: Int,
    private val pokemonRepository: PokemonRepository,
    private val teamRepository: TeamRepository
) : ViewModel() {
    private val _detailsState = MutableStateFlow(DetailsState())

    val uiState: StateFlow<PokemonDetailsUiState> = combine(
        _detailsState,
        teamRepository.observeIsInTeam(pokemonId)
    ) { detailsState, isInTeam ->
        when {
            detailsState.errorMessage != null -> PokemonDetailsUiState.Error(detailsState.errorMessage)
            detailsState.isLoading || detailsState.pokemon == null -> PokemonDetailsUiState.Loading
            else -> PokemonDetailsUiState.Success(
                pokemon = detailsState.pokemon,
                isInTeam = isInTeam,
                capturedLocation = detailsState.capturedLocation,
                locationError = detailsState.locationError,
                isSaving = detailsState.isSaving
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PokemonDetailsUiState.Loading
    )

    init {
        loadPokemon()
    }

    fun updateCapturedLocation(value: String) {
        _detailsState.update {
            it.copy(capturedLocation = value, locationError = null)
        }
    }

    fun addToTeam() {
        val snapshot = _detailsState.value
        val pokemon = snapshot.pokemon ?: return
        val trimmedLocation = snapshot.capturedLocation.trim()

        if (trimmedLocation.isBlank()) {
            _detailsState.update { it.copy(locationError = "Informe onde o Pokémon foi capturado.") }
            return
        }

        viewModelScope.launch {
            _detailsState.update { it.copy(isSaving = true, locationError = null) }
            try {
                teamRepository.addToTeam(pokemon, trimmedLocation)
                _detailsState.update { it.copy(isSaving = false) }
            } catch (error: Throwable) {
                _detailsState.update {
                    it.copy(
                        isSaving = false,
                        locationError = "Não foi possível salvar este Pokémon no time."
                    )
                }
            }
        }
    }

    private fun loadPokemon() {
        viewModelScope.launch {
            _detailsState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val pokemon = pokemonRepository.getPokemonDetails(pokemonId)
                _detailsState.update {
                    it.copy(
                        pokemon = pokemon,
                        isLoading = false
                    )
                }
            } catch (error: Throwable) {
                _detailsState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Não foi possível carregar os detalhes em tempo real."
                    )
                }
            }
        }
    }

    private data class DetailsState(
        val pokemon: PokemonDetails? = null,
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val capturedLocation: String = "",
        val locationError: String? = null,
        val errorMessage: String? = null
    )
}
