package com.example.pokedex

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pokedex.data.Pokemon
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.navigation.Route
import com.example.pokedex.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        var team by remember { mutableStateOf(listOf<Pokemon>()) }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            when {
                                currentDestination?.hasRoute<Route.Home>() == true -> "Home"
                                currentDestination?.hasRoute<Route.PokedexList>() == true -> "Pokédex"
                                currentDestination?.hasRoute<Route.PokemonDetails>() == true -> "Detalhes"
                                currentDestination?.hasRoute<Route.TeamBuilder>() == true -> "Meu Time"
                                else -> "Pokédex"
                            }
                        )
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Route.Home>() } == true,
                        onClick = { 
                            navController.navigate(Route.Home) {
                                popUpTo(Route.Home) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Route.PokedexList>() } == true,
                        onClick = { 
                            navController.navigate(Route.PokedexList) {
                                popUpTo(Route.Home)
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.List, contentDescription = "Pokédex") },
                        label = { Text("Pokédex") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Route.TeamBuilder>() } == true,
                        onClick = { 
                            navController.navigate(Route.TeamBuilder) {
                                popUpTo(Route.Home)
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Time") },
                        label = { Text("Time") }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<Route.Home> {
                    HomeScreen(
                        onNavigateToPokedex = { navController.navigate(Route.PokedexList) },
                        onNavigateToTeam = { navController.navigate(Route.TeamBuilder) }
                    )
                }
                composable<Route.PokedexList> {
                    PokedexListScreen(
                        onPokemonClick = { id ->
                            navController.navigate(Route.PokemonDetails(id))
                        }
                    )
                }
                composable<Route.PokemonDetails> { backStackEntry ->
                    val details: Route.PokemonDetails = backStackEntry.toRoute()
                    PokemonDetailsScreen(
                        pokemonId = details.id,
                        onAddToTeam = { id ->
                            val pokemon = PokemonRepository.getPokemonById(id)
                            if (pokemon != null && team.none { it.id == id }) {
                                team = team + pokemon
                            }
                        }
                    )
                }
                composable<Route.TeamBuilder> {
                    TeamScreen(
                        team = team,
                        onRemoveFromTeam = { id ->
                            team = team.filter { it.id != id }
                        }
                    )
                }
            }
        }
    }
}
