package com.example.pokedex.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pokedex.data.PokemonType
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pokedex.composeapp.generated.resources.Res
import pokedex.composeapp.generated.resources.bulbasaur
import pokedex.composeapp.generated.resources.charizard
import pokedex.composeapp.generated.resources.charmander
import pokedex.composeapp.generated.resources.dragonite
import pokedex.composeapp.generated.resources.eevee
import pokedex.composeapp.generated.resources.gengar
import pokedex.composeapp.generated.resources.mewtwo
import pokedex.composeapp.generated.resources.pikachu
import pokedex.composeapp.generated.resources.squirtle
import pokedex.composeapp.generated.resources.venusaur

@Composable
fun PokemonArtwork(
    pokemonId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val imageRes = pokemonDrawableResource(pokemonId)
    if (imageRes != null) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CatchingPokemon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(42.dp)
            )
        }
    }
}

@Composable
fun PokemonTypeTag(
    type: PokemonType,
    modifier: Modifier = Modifier
) {
    Surface(
        color = typeColor(type).copy(alpha = 0.16f),
        contentColor = typeColor(type),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.defaultMinSize(minHeight = 26.dp)
    ) {
        Text(
            text = type.displayName(),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun PokemonTypeRow(
    types: List<PokemonType>,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        types.forEach { type ->
            PokemonTypeTag(type = type)
        }
    }
}

fun pokemonBrush(types: List<PokemonType>): Brush {
    val first = typeColor(types.first())
    val second = typeColor(types.getOrElse(1) { types.first() }).copy(alpha = 0.72f)
    return Brush.linearGradient(listOf(first, second))
}

fun typeColor(type: PokemonType): Color = when (type) {
    PokemonType.FIRE -> Color(0xFFFF8A3D)
    PokemonType.WATER -> Color(0xFF4B8EF7)
    PokemonType.GRASS -> Color(0xFF5DBB63)
    PokemonType.ELECTRIC -> Color(0xFFF3C84B)
    PokemonType.PSYCHIC -> Color(0xFFF26CA7)
    PokemonType.ICE -> Color(0xFF6FD6FF)
    PokemonType.DRAGON -> Color(0xFF6775FF)
    PokemonType.DARK -> Color(0xFF5B5366)
    PokemonType.FAIRY -> Color(0xFFF5A7D8)
    PokemonType.NORMAL -> Color(0xFFA8A77A)
    PokemonType.FIGHTING -> Color(0xFFC96B4F)
    PokemonType.FLYING -> Color(0xFF8CA8FF)
    PokemonType.POISON -> Color(0xFFAF6ACB)
    PokemonType.GROUND -> Color(0xFFD2AF5A)
    PokemonType.ROCK -> Color(0xFFBAA55A)
    PokemonType.BUG -> Color(0xFF94BC4A)
    PokemonType.GHOST -> Color(0xFF6E6AAF)
    PokemonType.STEEL -> Color(0xFF7D9AAE)
}

fun PokemonType.displayName(): String = when (this) {
    PokemonType.FIRE -> "Fogo"
    PokemonType.WATER -> "Água"
    PokemonType.GRASS -> "Grama"
    PokemonType.ELECTRIC -> "Elétrico"
    PokemonType.PSYCHIC -> "Psíquico"
    PokemonType.ICE -> "Gelo"
    PokemonType.DRAGON -> "Dragão"
    PokemonType.DARK -> "Sombrio"
    PokemonType.FAIRY -> "Fada"
    PokemonType.NORMAL -> "Normal"
    PokemonType.FIGHTING -> "Lutador"
    PokemonType.FLYING -> "Voador"
    PokemonType.POISON -> "Venenoso"
    PokemonType.GROUND -> "Terra"
    PokemonType.ROCK -> "Pedra"
    PokemonType.BUG -> "Inseto"
    PokemonType.GHOST -> "Fantasma"
    PokemonType.STEEL -> "Aço"
}

fun formatPokemonNumber(id: Int): String = "#${id.toString().padStart(3, '0')}"

private fun pokemonDrawableResource(id: Int): DrawableResource? = when (id) {
    1 -> Res.drawable.bulbasaur
    3 -> Res.drawable.venusaur
    4 -> Res.drawable.charmander
    6 -> Res.drawable.charizard
    7 -> Res.drawable.squirtle
    25 -> Res.drawable.pikachu
    94 -> Res.drawable.gengar
    133 -> Res.drawable.eevee
    149 -> Res.drawable.dragonite
    150 -> Res.drawable.mewtwo
    else -> null
}
