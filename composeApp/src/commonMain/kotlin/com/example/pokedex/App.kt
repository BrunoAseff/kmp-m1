package com.example.pokedex

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pokedex.data.Pokemon
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.navigation.Route
import com.example.pokedex.ui.screens.*
import com.example.pokedex.ui.theme.PokedexTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    PokedexTheme {
        val navController = rememberNavController()
        var team by remember { mutableStateOf(listOf<Pokemon>()) }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val isDetailsRoute = currentDestination?.hasRoute<Route.PokemonDetails>() == true
        val topBarTitle = when {
            currentDestination?.hasRoute<Route.Home>() == true -> "Pokédex"
            currentDestination?.hasRoute<Route.PokedexList>() == true -> "Explorar"
            currentDestination?.hasRoute<Route.PokemonDetails>() == true -> "Detalhes"
            currentDestination?.hasRoute<Route.TeamBuilder>() == true -> "Meu Time"
            else -> "Pokédex"
        }
        fun navigateTopLevel(route: Route) {
            navController.navigate(route) navOptions@{
                popUpTo(navController.graph.findStartDestination().route ?: return@navOptions) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(topBarTitle) },
                    navigationIcon = {
                        if (isDetailsRoute) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Voltar"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Route.Home>() } == true,
                        onClick = { navigateTopLevel(Route.Home) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Início") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Route.PokedexList>() } == true,
                        onClick = { navigateTopLevel(Route.PokedexList) },
                        icon = { Icon(Icons.Default.List, contentDescription = "Pokédex") },
                        label = { Text("Pokédex") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Route.TeamBuilder>() } == true,
                        onClick = { navigateTopLevel(Route.TeamBuilder) },
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
                        teamCount = team.size,
                        onNavigateToPokedex = { navigateTopLevel(Route.PokedexList) },
                        onNavigateToTeam = { navigateTopLevel(Route.TeamBuilder) }
                    )
                }
                composable<Route.PokedexList> {
                    PokedexListScreen(
                        team = team,
                        onPokemonClick = { id ->
                            navController.navigate(Route.PokemonDetails(id))
                        }
                    )
                }
                composable<Route.PokemonDetails> { backStackEntry ->
                    val details: Route.PokemonDetails = backStackEntry.toRoute()
                    PokemonDetailsScreen(
                        pokemonId = details.id,
                        isInTeam = team.any { it.id == details.id },
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
