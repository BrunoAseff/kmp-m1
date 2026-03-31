package com.example.pokedex.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokedex.data.Pokemon
import com.example.pokedex.data.PokemonRepository

@Composable
fun PokedexListScreen(
    onPokemonClick: (Int) -> Unit
) {
    val pokemons = PokemonRepository.getPokemonList()
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(pokemons) { pokemon ->
            PokemonCard(pokemon = pokemon, onClick = { onPokemonClick(pokemon.id) })
        }
    }
}

@Composable
fun PokemonCard(pokemon: Pokemon, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "#${pokemon.id.toString().padStart(3, '0')}", style = MaterialTheme.typography.labelSmall)
            Text(text = pokemon.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                pokemon.types.forEach { type ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(type.name, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
    }
}
