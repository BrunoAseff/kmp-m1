package com.example.pokedex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.PokemonListItem
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.data.PokemonType
import com.example.pokedex.data.TeamRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PokedexUiState {
    data object Loading : PokedexUiState
    data class Success(
        val pokemons: List<PokemonListItem>,
        val query: String,
        val selectedType: PokemonType?,
        val availableTypes: List<PokemonType>,
        val teamIds: Set<Int>,
        val isLoadingMore: Boolean,
        val canLoadMore: Boolean
    ) : PokedexUiState

    data class Error(val message: String) : PokedexUiState
}

class PokedexViewModel(
    private val pokemonRepository: PokemonRepository,
    teamRepository: TeamRepository
) : ViewModel() {
    private val _contentState = MutableStateFlow(PokedexContentState())
    private var refreshJob: Job? = null

    val uiState: StateFlow<PokedexUiState> = combine(
        _contentState,
        teamRepository.team
    ) { content, team ->
        when {
            content.errorMessage != null -> PokedexUiState.Error(content.errorMessage)
            content.isInitialLoading && content.items.isEmpty() -> PokedexUiState.Loading
            else -> PokedexUiState.Success(
                pokemons = content.items,
                query = content.query,
                selectedType = content.selectedType,
                availableTypes = content.availableTypes,
                teamIds = team.map { it.id }.toSet(),
                isLoadingMore = content.isLoadingMore,
                canLoadMore = content.canLoadMore
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PokedexUiState.Loading
    )

    init {
        reload()
    }

    fun updateQuery(newQuery: String) {
        _contentState.update { it.copy(query = newQuery) }
        reload()
    }

    fun updateSelectedType(type: PokemonType?) {
        _contentState.update { it.copy(selectedType = type) }
        reload()
    }

    fun loadNextPage() {
        val snapshot = _contentState.value
        if (snapshot.isInitialLoading || snapshot.isLoadingMore || !snapshot.canLoadMore) return

        viewModelScope.launch {
            _contentState.update { it.copy(isLoadingMore = true, errorMessage = null) }
            try {
                val nextPage = snapshot.currentPage + 1
                val nextItems = pokemonRepository.getPokemonPage(
                    query = snapshot.query,
                    selectedType = snapshot.selectedType,
                    page = nextPage,
                    pageSize = PAGE_SIZE
                )
                _contentState.update { current ->
                    current.copy(
                        items = current.items + nextItems,
                        currentPage = nextPage,
                        canLoadMore = nextItems.size == PAGE_SIZE,
                        isLoadingMore = false
                    )
                }
            } catch (error: Throwable) {
                _contentState.update {
                    it.copy(
                        isLoadingMore = false,
                        errorMessage = "Não foi possível carregar mais Pokémon."
                    )
                }
            }
        }
    }

    private fun reload() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            val query = _contentState.value.query
            val selectedType = _contentState.value.selectedType

            _contentState.update {
                it.copy(
                    isInitialLoading = true,
                    isLoadingMore = false,
                    items = emptyList(),
                    currentPage = 0,
                    canLoadMore = true,
                    errorMessage = null
                )
            }

            try {
                pokemonRepository.ensureInitialSync()
                val availableTypes = pokemonRepository.getAvailableTypes()
                val firstPage = pokemonRepository.getPokemonPage(
                    query = query,
                    selectedType = selectedType,
                    page = 0,
                    pageSize = PAGE_SIZE
                )
                _contentState.update {
                    it.copy(
                        availableTypes = availableTypes,
                        items = firstPage,
                        currentPage = 0,
                        canLoadMore = firstPage.size == PAGE_SIZE,
                        isInitialLoading = false
                    )
                }
            } catch (error: Throwable) {
                _contentState.update {
                    it.copy(
                        isInitialLoading = false,
                        errorMessage = "Erro ao sincronizar a Pokédex. Verifique a conexão e tente novamente."
                    )
                }
            }
        }
    }

    private data class PokedexContentState(
        val query: String = "",
        val selectedType: PokemonType? = null,
        val availableTypes: List<PokemonType> = emptyList(),
        val items: List<PokemonListItem> = emptyList(),
        val currentPage: Int = 0,
        val canLoadMore: Boolean = true,
        val isInitialLoading: Boolean = true,
        val isLoadingMore: Boolean = false,
        val errorMessage: String? = null
    )

    private companion object {
        const val PAGE_SIZE = 24
    }
}
