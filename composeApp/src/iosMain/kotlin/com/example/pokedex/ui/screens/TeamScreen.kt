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
    Column(modifier = Modifier.fillMaxSize().padding(top = 32.dp)) {
        Text(
            text = "My Team",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        if (team.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No Pokemon in your team yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp)
            ) {
                items(team) { pokemon ->
                    ListItem(
                        headlineContent = { Text(pokemon.name) },
                        supportingContent = { Text("#${pokemon.id}") },
                        trailingContent = {
                            TextButton(onClick = { onRemoveFromTeam(pokemon.id) }) {
                                Text("Remove", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}
