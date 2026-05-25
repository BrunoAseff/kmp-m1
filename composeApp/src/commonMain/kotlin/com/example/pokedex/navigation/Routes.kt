package com.example.pokedex.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Home : Route
    
    @Serializable
    data object PokedexList : Route
    
    @Serializable
    data class PokemonDetails(val id: Int) : Route
    
    @Serializable
    data object TeamBuilder : Route
}
