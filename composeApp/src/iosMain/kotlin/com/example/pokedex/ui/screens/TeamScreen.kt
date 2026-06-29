package com.example.pokedex.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokedex.data.TeamRepository
import com.example.pokedex.hardware.CapturedPhotoPreview
import com.example.pokedex.ui.components.PokemonArtwork
import com.example.pokedex.ui.components.PokemonTypeRow
import com.example.pokedex.ui.components.formatPokemonNumber
import com.example.pokedex.ui.components.pokemonBrush
import com.example.pokedex.ui.viewmodel.TeamUiState
import com.example.pokedex.ui.viewmodel.TeamViewModel

@Composable
actual fun TeamScreen(
    teamRepository: TeamRepository
) {
    val viewModel: TeamViewModel = viewModel { TeamViewModel(teamRepository) }
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is TeamUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is TeamUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is TeamUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF4F5F9))
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(top = 20.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Meu Time",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (state.team.isEmpty()) {
                            "Monte sua equipe a partir da Pokédex."
                        } else {
                            "${state.team.size} Pokémon prontos para a batalha."
                        },
                        color = Color(0xFF6E7684),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                if (state.team.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        shape = RoundedCornerShape(32.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(Color(0xFFE8ECF8), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color(0xFF7083C8)
                                )
                            }
                            Spacer(modifier = Modifier.size(16.dp))
                            Text(
                                "Nenhum Pokémon no seu time ainda.",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                "Adicione Pokémon pela tela de detalhes para montar sua equipe.",
                                color = Color(0xFF6E7684)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = Color(0xFFE8ECF8)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 18.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(Color.White.copy(alpha = 0.8f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = state.team.size.toString(),
                                            color = Color(0xFF30407A),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.size(12.dp))
                                    Text(
                                        text = "Sua seleção atual",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        items(state.team, key = { it.id }) { pokemon ->
                            Surface(
                                shape = RoundedCornerShape(30.dp),
                                color = Color.White,
                                shadowElevation = 2.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(88.dp)
                                                .background(
                                                    brush = pokemonBrush(pokemon.types),
                                                    shape = RoundedCornerShape(26.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            PokemonArtwork(
                                                pokemonId = pokemon.id,
                                                imageUrl = pokemon.artworkUrl,
                                                contentDescription = pokemon.name,
                                                modifier = Modifier.size(70.dp)
                                            )
                                        }
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = pokemon.name,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = formatPokemonNumber(pokemon.id),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            PokemonTypeRow(types = pokemon.types)
                                            Text(
                                                text = "Capturado em ${pokemon.capturedLocation}",
                                                color = Color(0xFF6E7684),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        TextButton(
                                            onClick = { viewModel.removeFromTeam(pokemon.id) },
                                            modifier = Modifier
                                                .background(
                                                    color = Color(0xFFFCE3E8),
                                                    shape = RoundedCornerShape(18.dp)
                                                )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = Color(0xFFC83C5A)
                                            )
                                            Spacer(modifier = Modifier.size(6.dp))
                                            Text("Remover", color = Color(0xFFC83C5A))
                                        }
                                    }

                                    if (pokemon.photoPath != null) {
                                        CapturedPhotoPreview(
                                            photoPath = pokemon.photoPath,
                                            contentDescription = "Foto de captura de ${pokemon.name}",
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                    if (pokemon.latitude != null && pokemon.longitude != null) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = Color(0xFF30407A),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = "Lat ${pokemon.latitude} • Long ${pokemon.longitude}",
                                                color = Color(0xFF6E7684),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
