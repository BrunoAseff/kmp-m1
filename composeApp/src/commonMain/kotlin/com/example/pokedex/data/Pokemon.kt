package com.example.pokedex.data

enum class PokemonType(
    val apiName: String,
    val displayName: String
) {
    BUG("bug", "Inseto"),
    DARK("dark", "Sombrio"),
    DRAGON("dragon", "Dragão"),
    ELECTRIC("electric", "Elétrico"),
    FAIRY("fairy", "Fada"),
    FIGHTING("fighting", "Lutador"),
    FIRE("fire", "Fogo"),
    FLYING("flying", "Voador"),
    GHOST("ghost", "Fantasma"),
    GRASS("grass", "Grama"),
    GROUND("ground", "Terra"),
    ICE("ice", "Gelo"),
    NORMAL("normal", "Normal"),
    POISON("poison", "Venenoso"),
    PSYCHIC("psychic", "Psíquico"),
    ROCK("rock", "Pedra"),
    STEEL("steel", "Aço"),
    WATER("water", "Água");

    companion object {
        fun fromApiName(value: String?): PokemonType? = entries.firstOrNull { it.apiName == value }
    }
}

data class PokemonListItem(
    val id: Int,
    val name: String,
    val types: List<PokemonType>,
    val artworkUrl: String?
)

data class PokemonAbility(
    val name: String
)

data class PokemonDetails(
    val id: Int,
    val name: String,
    val description: String,
    val heightMeters: Double,
    val weightKg: Double,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val abilities: List<PokemonAbility>,
    val types: List<PokemonType>,
    val artworkUrl: String?
)

data class TeamPokemon(
    val id: Int,
    val name: String,
    val types: List<PokemonType>,
    val artworkUrl: String?,
    val capturedLocation: String
)
