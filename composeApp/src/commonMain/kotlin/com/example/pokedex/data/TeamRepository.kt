package com.example.pokedex.data

import com.example.pokedex.data.local.TeamPokemonDao
import com.example.pokedex.data.local.TeamPokemonEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class TeamRepository(
    private val teamPokemonDao: TeamPokemonDao
) {
    val team: Flow<List<TeamPokemon>> = teamPokemonDao.observeTeam().map { entities ->
        entities.map { entity ->
            TeamPokemon(
                id = entity.pokemonId,
                name = entity.name,
                types = listOfNotNull(
                    PokemonType.fromApiName(entity.primaryType),
                    PokemonType.fromApiName(entity.secondaryType)
                ),
                artworkUrl = entity.artworkUrl,
                capturedLocation = entity.capturedLocation,
                latitude = entity.latitude,
                longitude = entity.longitude,
                photoPath = entity.photoPath
            )
        }
    }

    val teamCount: Flow<Int> = teamPokemonDao.observeTeamCount()

    fun observeIsInTeam(pokemonId: Int): Flow<Boolean> = teamPokemonDao.observeIsInTeam(pokemonId)

    suspend fun addToTeam(
        pokemon: PokemonDetails,
        capturedLocation: String,
        latitude: Double,
        longitude: Double,
        photoPath: String
    ) {
        val types = pokemon.types
        teamPokemonDao.upsert(
            TeamPokemonEntity(
                pokemonId = pokemon.id,
                name = pokemon.name,
                primaryType = types.first().apiName,
                secondaryType = types.getOrNull(1)?.apiName,
                artworkUrl = pokemon.artworkUrl,
                capturedLocation = capturedLocation,
                latitude = latitude,
                longitude = longitude,
                photoPath = photoPath,
                capturedAtEpochMillis = Clock.System.now().toEpochMilliseconds()
            )
        )
    }

    suspend fun removeFromTeam(pokemonId: Int) {
        teamPokemonDao.removeByPokemonId(pokemonId)
    }
}
