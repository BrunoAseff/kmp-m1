package com.example.pokedex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokedex.data.PokemonDetails
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.data.TeamRepository
import com.example.pokedex.hardware.CaptureResult
import com.example.pokedex.hardware.CapturedPhotoPreview
import com.example.pokedex.hardware.rememberCaptureHardwareController
import com.example.pokedex.ui.components.PokemonArtwork
import com.example.pokedex.ui.components.PokemonTypeRow
import com.example.pokedex.ui.components.formatPokemonNumber
import com.example.pokedex.ui.components.pokemonBrush
import com.example.pokedex.ui.viewmodel.PokemonDetailsUiState
import com.example.pokedex.ui.viewmodel.PokemonDetailsViewModel

@Composable
fun PokemonDetailsScreen(
    pokemonId: Int,
    pokemonRepository: PokemonRepository,
    teamRepository: TeamRepository,
    viewModel: PokemonDetailsViewModel = viewModel {
        PokemonDetailsViewModel(
            pokemonId = pokemonId,
            pokemonRepository = pokemonRepository,
            teamRepository = teamRepository
        )
    }
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is PokemonDetailsUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is PokemonDetailsUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }

        is PokemonDetailsUiState.Success -> {
            PokemonDetailsContent(
                pokemon = state.pokemon,
                isInTeam = state.isInTeam,
                captureResult = state.captureResult,
                captureError = state.captureError,
                isSaving = state.isSaving,
                onCaptureCompleted = viewModel::onCaptureCompleted,
                onCaptureError = viewModel::onCaptureError,
                onAddToTeam = viewModel::addToTeam
            )
        }
    }
}

@Composable
private fun PokemonDetailsContent(
    pokemon: PokemonDetails,
    isInTeam: Boolean,
    captureResult: CaptureResult?,
    captureError: String?,
    isSaving: Boolean,
    onCaptureCompleted: (CaptureResult) -> Unit,
    onCaptureError: (String) -> Unit,
    onAddToTeam: () -> Unit
) {
    val captureController = rememberCaptureHardwareController(
        onCaptureCompleted = onCaptureCompleted,
        onPermissionDenied = onCaptureError,
        onCaptureFailed = onCaptureError
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(pokemonBrush(pokemon.types))
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(170.dp)
                    .background(Color.White.copy(alpha = 0.12f), CircleShape)
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = formatPokemonNumber(pokemon.id),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.88f)
                )
                Text(
                    text = pokemon.name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                PokemonTypeRow(types = pokemon.types)
            }
            PokemonArtwork(
                pokemonId = pokemon.id,
                imageUrl = pokemon.artworkUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.BottomCenter)
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Detalhes em tempo real",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = pokemon.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Habilidades: ${pokemon.abilities.joinToString { it.name }}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider()
                    StatRow(label = "HP", value = pokemon.hp, max = 255)
                    StatRow(label = "Ataque", value = pokemon.attack, max = 190)
                    StatRow(label = "Defesa", value = pokemon.defense, max = 230)
                    StatRow(label = "Velocidade", value = pokemon.speed, max = 180)
                }
            }

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Salvar no time",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isInTeam) {
                            "Esse Pokémon já foi persistido no seu time com foto e coordenadas."
                        } else {
                            "Capture uma foto do local e registre automaticamente as coordenadas via GPS."
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    CapturedPhotoPreview(
                        photoPath = captureResult?.photoPath,
                        contentDescription = "Foto de captura de ${pokemon.name}",
                        modifier = Modifier.fillMaxWidth()
                    )

                    CaptureLocationCard(captureResult = captureResult)

                    if (captureError != null) {
                        Text(
                            text = captureError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    OutlinedButton(
                        onClick = captureController::capture,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isInTeam && !isSaving && !captureController.isCaptureInProgress
                    ) {
                        if (captureController.isCaptureInProgress) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(if (captureResult == null) "Capturar foto e GPS" else "Capturar novamente")
                    }

                    if (isInTeam) {
                        OutlinedButton(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Já está salvo")
                        }
                    } else {
                        Button(
                            onClick = onAddToTeam,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving && captureResult != null
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Salvar no Time")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CaptureLocationCard(captureResult: CaptureResult?) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.52f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Localização da captura",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (captureResult == null) {
                        "Aguardando coordenadas do GPS."
                    } else {
                        "Lat ${captureResult.location.latitude} • Long ${captureResult.location.longitude}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: Int, max: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = { value.toFloat() / max },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
    }
}
