package com.example.pokedex.data

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.pokedex.data.local.AppDatabase
import com.example.pokedex.data.remote.PokeApiService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

class AppContainer(
    databaseBuilder: RoomDatabase.Builder<AppDatabase>
) {
    private val database = databaseBuilder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
    }

    private val pokeApiService = PokeApiService(httpClient)

    val pokemonRepository = PokemonRepository(
        pokeApiService = pokeApiService,
        pokemonCacheDao = database.pokemonCacheDao()
    )

    val teamRepository = TeamRepository(
        teamPokemonDao = database.teamPokemonDao()
    )
}
