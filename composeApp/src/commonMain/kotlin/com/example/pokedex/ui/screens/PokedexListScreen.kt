package com.example.pokedex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pokedex.data.Pokemon
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.data.PokemonType
import com.example.pokedex.ui.components.PokemonArtwork
import com.example.pokedex.ui.components.displayName
import com.example.pokedex.ui.components.PokemonTypeRow
import com.example.pokedex.ui.components.formatPokemonNumber
import com.example.pokedex.ui.components.pokemonBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokedexListScreen(
    team: List<Pokemon>,
    onPokemonClick: (Int) -> Unit
) {
    val pokemons = remember { PokemonRepository.getPokemonList().sortedBy { it.id } }
    var query by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf("ALL") }

    val availableTypes = remember(pokemons) {
        pokemons.flatMap { it.types }.distinct().sortedBy { it.name }
    }
    val filteredPokemons = remember(pokemons, query, selectedType) {
        pokemons.filter { pokemon ->
            val matchesQuery = query.isBlank() ||
                pokemon.name.contains(query.trim(), ignoreCase = true) ||
                formatPokemonNumber(pokemon.id).contains(query.trim(), ignoreCase = true)
            val matchesType = selectedType == "ALL" || pokemon.types.any { it.name == selectedType }
            matchesQuery && matchesType
        }
    }
    val teamIds = remember(team) { team.map { it.id }.toSet() }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = query,
                        onQueryChange = { query = it },
                        onSearch = {},
                        expanded = false,
                        onExpandedChange = {},
                        placeholder = { Text("Procure por nome ou número") },
                        leadingIcon = {
                            androidx.compose.material3.Icon(
                                Icons.Default.Search,
                                contentDescription = null
                            )
                        }
                    )
                },
                expanded = false,
                onExpandedChange = {},
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {}

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        label = "Todos",
                        selected = selectedType == "ALL",
                        onClick = { selectedType = "ALL" }
                    )
                }
                items(availableTypes) { type ->
                    FilterChip(
                        label = type.displayName(),
                        selected = selectedType == type.name,
                        onClick = {
                            selectedType = if (selectedType == type.name) "ALL" else type.name
                        },
                        type = type
                    )
                }
            }

            Text(
                text = "${filteredPokemons.size} Pokémon encontrados",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredPokemons, key = { it.id }) { pokemon ->
                PokemonCard(
                    pokemon = pokemon,
                    isInTeam = pokemon.id in teamIds,
                    onClick = { onPokemonClick(pokemon.id) }
                )
            }
        }
    }
}

@Composable
fun PokemonCard(
    pokemon: Pokemon,
    isInTeam: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .background(pokemonBrush(pokemon.types))
                    .padding(14.dp)
            ) {
                if (isInTeam) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.24f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Em equipe",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                PokemonArtwork(
                    pokemonId = pokemon.id,
                    contentDescription = pokemon.name,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(108.dp)
                )
            }

            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = formatPokemonNumber(pokemon.id),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = pokemon.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                PokemonTypeRow(types = pokemon.types)
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    type: PokemonType? = null
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                (type?.let { com.example.pokedex.ui.components.typeColor(it) }
                    ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.18f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (type != null) {
                Spacer(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(com.example.pokedex.ui.components.typeColor(type))
                )
            }
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
