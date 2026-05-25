package com.example.pokedex.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object TeamRepository {
    private val _team = MutableStateFlow<List<Pokemon>>(emptyList())
    val team: StateFlow<List<Pokemon>> = _team.asStateFlow()

    fun addToTeam(pokemon: Pokemon) {
        _team.update { currentTeam ->
            if (currentTeam.none { it.id == pokemon.id }) {
                currentTeam + pokemon
            } else {
                currentTeam
            }
        }
    }

    fun removeFromTeam(pokemonId: Int) {
        _team.update { currentTeam ->
            currentTeam.filter { it.id != pokemonId }
        }
    }
}
