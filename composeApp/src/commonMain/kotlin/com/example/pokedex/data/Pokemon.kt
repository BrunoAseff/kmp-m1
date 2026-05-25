package com.example.pokedex.data

import kotlinx.serialization.Serializable

@Serializable
enum class PokemonType {
    FIRE, WATER, GRASS, ELECTRIC, PSYCHIC, ICE, DRAGON, DARK, FAIRY, NORMAL, FIGHTING, FLYING, POISON, GROUND, ROCK, BUG, GHOST, STEEL
}

@Serializable
data class Pokemon(
    val id: Int,
    val name: String,
    val types: List<PokemonType>,
    val description: String,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int
)
