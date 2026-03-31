package com.example.pokedex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToPokedex: () -> Unit,
    onNavigateToTeam: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pokédex Multiplatform", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNavigateToPokedex) {
            Text("Ver Pokédex")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToTeam) {
            Text("Meu Time")
        }
    }
}
