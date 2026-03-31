package com.example.pokedex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokedex.data.PokemonRepository

@Composable
fun PokemonDetailsScreen(
    pokemonId: Int,
    onAddToTeam: (Int) -> Unit
) {
    val pokemon = PokemonRepository.getPokemonById(pokemonId) ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = pokemon.name, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Types: ${pokemon.types.joinToString { it.name }}")
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(text = pokemon.description, style = MaterialTheme.typography.bodyLarge)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        StatRow(label = "HP", value = pokemon.hp, max = 255)
        StatRow(label = "Attack", value = pokemon.attack, max = 190)
        StatRow(label = "Defense", value = pokemon.defense, max = 230)
        StatRow(label = "Speed", value = pokemon.speed, max = 180)
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { onAddToTeam(pokemon.id) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Adicionar ao Time")
        }
    }
}

@Composable
fun StatRow(label: String, value: Int, max: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.width(80.dp))
        LinearProgressIndicator(
            progress = { value.toFloat() / max },
            modifier = Modifier.weight(1f).height(8.dp),
        )
        Text(text = value.toString(), modifier = Modifier.padding(start = 8.dp))
    }
}
