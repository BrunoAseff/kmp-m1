package com.example.pokedex.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamPokemonDao {
    @Query("SELECT * FROM team_pokemon ORDER BY capturedAtEpochMillis DESC")
    fun observeTeam(): Flow<List<TeamPokemonEntity>>

    @Query("SELECT COUNT(*) FROM team_pokemon")
    fun observeTeamCount(): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM team_pokemon WHERE pokemonId = :pokemonId)")
    fun observeIsInTeam(pokemonId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: TeamPokemonEntity)

    @Query("DELETE FROM team_pokemon WHERE pokemonId = :pokemonId")
    suspend fun removeByPokemonId(pokemonId: Int)
}
