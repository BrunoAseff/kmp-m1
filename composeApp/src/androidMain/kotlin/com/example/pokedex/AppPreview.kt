package com.example.pokedex

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pokedex.ui.screens.HomeScreenContent
import com.example.pokedex.ui.theme.PokedexTheme

private enum class PreviewRoute {
    HOME,
    POKEDEX,
    TEAM,
    DETAILS
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AppHomePreview() {
    var route by remember { mutableStateOf(PreviewRoute.HOME) }

    PokedexTheme {
        AppScaffold(
            topBarTitle = when (route) {
                PreviewRoute.HOME -> "Pokédex"
                PreviewRoute.POKEDEX -> "Explorar"
                PreviewRoute.TEAM -> "Meu Time"
                PreviewRoute.DETAILS -> "Detalhes"
            },
            isDetailsRoute = route == PreviewRoute.DETAILS,
            isHomeSelected = route == PreviewRoute.HOME,
            isPokedexSelected = route == PreviewRoute.POKEDEX,
            isTeamSelected = route == PreviewRoute.TEAM,
            onBack = { route = PreviewRoute.POKEDEX },
            onNavigateHome = { route = PreviewRoute.HOME },
            onNavigatePokedex = { route = PreviewRoute.POKEDEX },
            onNavigateTeam = { route = PreviewRoute.TEAM }
        ) { innerPadding ->
            when (route) {
                PreviewRoute.HOME -> HomeScreenContent(
                    teamCount = 3,
                    onNavigateToPokedex = { route = PreviewRoute.POKEDEX },
                    onNavigateToTeam = { route = PreviewRoute.TEAM },
                    modifier = Modifier.padding(innerPadding)
                )

                PreviewRoute.POKEDEX -> PreviewPlaceholderScreen(
                    title = "Preview da Pokédex",
                    subtitle = "Clique em um item para simular a tela de detalhes.",
                    actionLabel = "Abrir detalhes",
                    onAction = { route = PreviewRoute.DETAILS },
                    modifier = Modifier.padding(innerPadding)
                )

                PreviewRoute.TEAM -> PreviewPlaceholderScreen(
                    title = "Preview do Time",
                    subtitle = "Use a navbar para voltar para outras áreas da aplicação.",
                    actionLabel = "Ir para Pokédex",
                    onAction = { route = PreviewRoute.POKEDEX },
                    modifier = Modifier.padding(innerPadding)
                )

                PreviewRoute.DETAILS -> PreviewPlaceholderScreen(
                    title = "Preview de Detalhes",
                    subtitle = "O botão voltar do top bar também funciona neste preview.",
                    actionLabel = "Voltar para o time",
                    onAction = { route = PreviewRoute.TEAM },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun PreviewPlaceholderScreen(
    title: String,
    subtitle: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = subtitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(4) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAction),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text("$actionLabel ${index + 1}")
                }
            }
        }
    }
}
