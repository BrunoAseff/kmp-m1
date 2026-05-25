package com.example.pokedex.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonCacheDao {
    @Query("SELECT COUNT(*) FROM pokemon_cache")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<PokemonCacheEntity>)

    @Query(
        """
        SELECT * FROM pokemon_cache
        WHERE (
            :query = '' OR
            name LIKE '%' || :query || '%' OR
            CAST(id AS TEXT) LIKE '%' || :query || '%'
        )
        AND (
            :selectedType IS NULL OR
            primaryType = :selectedType OR
            secondaryType = :selectedType
        )
        ORDER BY id ASC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getPage(
        query: String,
        selectedType: String?,
        limit: Int,
        offset: Int
    ): List<PokemonCacheEntity>

    @Query(
        """
        SELECT DISTINCT primaryType AS typeName FROM pokemon_cache
        UNION
        SELECT DISTINCT secondaryType AS typeName FROM pokemon_cache WHERE secondaryType IS NOT NULL
        ORDER BY typeName ASC
        """
    )
    suspend fun getAvailableTypes(): List<String>
}
