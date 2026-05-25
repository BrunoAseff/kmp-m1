package com.example.pokedex.ui.screens

import androidx.compose.runtime.Composable
import com.example.pokedex.data.TeamRepository

@Composable
expect fun TeamScreen(
    teamRepository: TeamRepository
)
