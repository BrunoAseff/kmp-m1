package com.example.pokedex.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_cache")
data class PokemonCacheEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val primaryType: String,
    val secondaryType: String?,
    val artworkUrl: String?
)

@Entity(tableName = "team_pokemon")
data class TeamPokemonEntity(
    @PrimaryKey val pokemonId: Int,
    val name: String,
    val primaryType: String,
    val secondaryType: String?,
    val artworkUrl: String?,
    val capturedLocation: String,
    val latitude: Double?,
    val longitude: Double?,
    @ColumnInfo(name = "photo_path")
    val photoPath: String?,
    val capturedAtEpochMillis: Long
)
