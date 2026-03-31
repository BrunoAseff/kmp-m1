package com.example.pokedex.ui.screens

import androidx.compose.runtime.Composable
import com.example.pokedex.data.Pokemon

@Composable
expect fun TeamScreen(
    team: List<Pokemon>,
    onRemoveFromTeam: (Int) -> Unit
)
