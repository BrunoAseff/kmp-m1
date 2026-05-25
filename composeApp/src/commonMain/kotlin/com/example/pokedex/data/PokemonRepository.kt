package com.example.pokedex.data

import com.example.pokedex.data.local.PokemonCacheDao
import com.example.pokedex.data.local.PokemonCacheEntity
import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.data.remote.PokemonResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit

class PokemonRepository(
    private val pokeApiService: PokeApiService,
    private val pokemonCacheDao: PokemonCacheDao
) {
    suspend fun ensureInitialSync() {
        if (pokemonCacheDao.count() > 0) return

        syncMutex.withLock {
            if (pokemonCacheDao.count() > 0) return

            val index = pokeApiService.fetchPokemonIndex(INITIAL_SYNC_LIMIT)
            val semaphore = Semaphore(MAX_CONCURRENT_REQUESTS)

            val cachedPokemons = coroutineScope {
                index.map { entry ->
                    async {
                        semaphore.withPermit {
                            pokeApiService.fetchPokemonDetails(entry.id.toString()).toCacheEntity()
                        }
                    }
                }.awaitAll()
            }.sortedBy { it.id }

            pokemonCacheDao.upsertAll(cachedPokemons)
        }
    }

    suspend fun getAvailableTypes(): List<PokemonType> =
        pokemonCacheDao.getAvailableTypes().mapNotNull(PokemonType::fromApiName)

    suspend fun getPokemonPage(
        query: String,
        selectedType: PokemonType?,
        page: Int,
        pageSize: Int
    ): List<PokemonListItem> =
        pokemonCacheDao.getPage(
            query = query.trim(),
            selectedType = selectedType?.apiName,
            limit = pageSize,
            offset = page * pageSize
        ).map { entity ->
            PokemonListItem(
                id = entity.id,
                name = entity.name.replaceFirstChar { it.uppercase() },
                types = listOfNotNull(
                    PokemonType.fromApiName(entity.primaryType),
                    PokemonType.fromApiName(entity.secondaryType)
                ),
                artworkUrl = entity.artworkUrl
            )
        }

    suspend fun getPokemonDetails(pokemonId: Int): PokemonDetails =
        pokeApiService.fetchPokemonDetails(pokemonId.toString()).toPokemonDetails()

    private fun PokemonResponse.toCacheEntity(): PokemonCacheEntity {
        val sortedTypes = types.sortedBy { it.slot }.mapNotNull { PokemonType.fromApiName(it.type.name) }
        val primaryType = sortedTypes.firstOrNull() ?: PokemonType.NORMAL
        return PokemonCacheEntity(
            id = id,
            name = name.replaceFirstChar { it.uppercase() },
            primaryType = primaryType.apiName,
            secondaryType = sortedTypes.getOrNull(1)?.apiName,
            artworkUrl = sprites.other?.officialArtwork?.frontDefault
        )
    }

    private fun PokemonResponse.toPokemonDetails(): PokemonDetails {
        val statsByName = stats.associateBy { it.stat.name }
        val sortedTypes = types.sortedBy { it.slot }.mapNotNull { PokemonType.fromApiName(it.type.name) }

        return PokemonDetails(
            id = id,
            name = name.replaceFirstChar { it.uppercase() },
            description = "Altura: ${height / 10.0} m • Peso: ${weight / 10.0} kg",
            heightMeters = height / 10.0,
            weightKg = weight / 10.0,
            hp = statsByName["hp"]?.baseStat ?: 0,
            attack = statsByName["attack"]?.baseStat ?: 0,
            defense = statsByName["defense"]?.baseStat ?: 0,
            speed = statsByName["speed"]?.baseStat ?: 0,
            abilities = abilities.map { PokemonAbility(it.ability.name.replace('-', ' ').replaceFirstChar { c -> c.uppercase() }) },
            types = sortedTypes,
            artworkUrl = sprites.other?.officialArtwork?.frontDefault
        )
    }

    private companion object {
        const val INITIAL_SYNC_LIMIT = 151
        const val MAX_CONCURRENT_REQUESTS = 8
        val syncMutex = Mutex()
    }
}
