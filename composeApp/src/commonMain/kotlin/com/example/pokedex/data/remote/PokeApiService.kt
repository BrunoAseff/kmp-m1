package com.example.pokedex.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class PokeApiService(
    private val client: HttpClient
) {
    suspend fun fetchPokemonIndex(limit: Int): List<PokemonIndexEntry> {
        val response: PokemonListResponse = client.get("$BASE_URL/pokemon?limit=$limit").body()
        return response.results.mapNotNull { item ->
            val id = item.url.trimEnd('/').substringAfterLast('/').toIntOrNull()
            id?.let { PokemonIndexEntry(id = it, name = item.name) }
        }
    }

    suspend fun fetchPokemonDetails(idOrName: String): PokemonResponse =
        client.get("$BASE_URL/pokemon/$idOrName").body()

    private companion object {
        const val BASE_URL = "https://pokeapi.co/api/v2"
    }
}

data class PokemonIndexEntry(
    val id: Int,
    val name: String
)

@Serializable
data class PokemonListResponse(
    val results: List<NamedApiResource>
)

@Serializable
data class NamedApiResource(
    val name: String,
    val url: String
)

@Serializable
data class PokemonResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val abilities: List<PokemonAbilitySlotDto>,
    val sprites: PokemonSpritesDto,
    val stats: List<PokemonStatDto>,
    val types: List<PokemonTypeSlotDto>
)

@Serializable
data class PokemonTypeSlotDto(
    val slot: Int,
    val type: NamedApiResource
)

@Serializable
data class PokemonAbilitySlotDto(
    val ability: NamedApiResource
)

@Serializable
data class PokemonStatDto(
    @SerialName("base_stat") val baseStat: Int,
    val stat: NamedApiResource
)

@Serializable
data class PokemonSpritesDto(
    val other: PokemonSpritesOtherDto? = null
)

@Serializable
data class PokemonSpritesOtherDto(
    @SerialName("official-artwork") val officialArtwork: PokemonOfficialArtworkDto? = null
)

@Serializable
data class PokemonOfficialArtworkDto(
    @SerialName("front_default") val frontDefault: String? = null
)
