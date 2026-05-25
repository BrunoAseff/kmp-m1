package com.example.pokedex.ui.screens

import androidx.compose.runtime.Composable
import com.example.pokedex.ui.viewmodel.TeamViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
expect fun TeamScreen(
    viewModel: TeamViewModel = viewModel { TeamViewModel() }
)
