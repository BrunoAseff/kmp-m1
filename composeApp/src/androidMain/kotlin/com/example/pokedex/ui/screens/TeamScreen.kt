package com.example.pokedex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokedex.data.Pokemon

@Composable
actual fun TeamScreen(
    team: List<Pokemon>,
    onRemoveFromTeam: (Int) -> Unit
) {
    if (team.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Seu time está vazio!", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(team) { pokemon ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = pokemon.name, style = MaterialTheme.typography.titleLarge)
                            Text(text = "ID: #${pokemon.id}", style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = { onRemoveFromTeam(pokemon.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remover")
                        }
                    }
                }
            }
        }
    }
}
