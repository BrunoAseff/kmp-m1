package com.example.pokedex.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection

@Database(
    entities = [PokemonCacheEntity::class, TeamPokemonEntity::class],
    version = 2,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pokemonCacheDao(): PokemonCacheDao
    abstract fun teamPokemonDao(): TeamPokemonDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.prepare("ALTER TABLE team_pokemon ADD COLUMN latitude REAL").use { it.step() }
        connection.prepare("ALTER TABLE team_pokemon ADD COLUMN longitude REAL").use { it.step() }
        connection.prepare("ALTER TABLE team_pokemon ADD COLUMN photo_path TEXT").use { it.step() }
    }
}
