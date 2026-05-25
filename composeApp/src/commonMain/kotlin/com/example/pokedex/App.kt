package com.example.pokedex

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
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
import androidx.room.RoomDatabase
import com.example.pokedex.data.AppContainer
import com.example.pokedex.data.local.AppDatabase
import com.example.pokedex.navigation.Route
import com.example.pokedex.ui.screens.*
import com.example.pokedex.ui.theme.PokedexTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    databaseBuilderFactory: () -> RoomDatabase.Builder<AppDatabase>
) {
    PokedexTheme {
        val appContainer = remember { AppContainer(databaseBuilderFactory()) }
        val navController = rememberNavController()
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
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().route ?: return@navigate) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        AppScaffold(
            topBarTitle = topBarTitle,
            isDetailsRoute = isDetailsRoute,
            isHomeSelected = currentDestination?.hierarchy?.any { it.hasRoute<Route.Home>() } == true,
            isPokedexSelected = currentDestination?.hierarchy?.any { it.hasRoute<Route.PokedexList>() } == true,
            isTeamSelected = currentDestination?.hierarchy?.any { it.hasRoute<Route.TeamBuilder>() } == true,
            onBack = { navController.popBackStack() },
            onNavigateHome = { navigateTopLevel(Route.Home) },
            onNavigatePokedex = { navigateTopLevel(Route.PokedexList) },
            onNavigateTeam = { navigateTopLevel(Route.TeamBuilder) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<Route.Home> {
                    HomeScreen(
                        teamRepository = appContainer.teamRepository,
                        onNavigateToPokedex = { navigateTopLevel(Route.PokedexList) },
                        onNavigateToTeam = { navigateTopLevel(Route.TeamBuilder) }
                    )
                }
                composable<Route.PokedexList> {
                    PokedexListScreen(
                        pokemonRepository = appContainer.pokemonRepository,
                        teamRepository = appContainer.teamRepository,
                        onPokemonClick = { id ->
                            navController.navigate(Route.PokemonDetails(id))
                        }
                    )
                }
                composable<Route.PokemonDetails> { backStackEntry ->
                    val details: Route.PokemonDetails = backStackEntry.toRoute()
                    PokemonDetailsScreen(
                        pokemonId = details.id,
                        pokemonRepository = appContainer.pokemonRepository,
                        teamRepository = appContainer.teamRepository
                    )
                }
                composable<Route.TeamBuilder> {
                    TeamScreen(teamRepository = appContainer.teamRepository)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppScaffold(
    topBarTitle: String,
    isDetailsRoute: Boolean,
    isHomeSelected: Boolean,
    isPokedexSelected: Boolean,
    isTeamSelected: Boolean,
    onBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigatePokedex: () -> Unit,
    onNavigateTeam: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(topBarTitle) },
                navigationIcon = {
                    if (isDetailsRoute) {
                        IconButton(onClick = onBack) {
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
                    selected = isHomeSelected,
                    onClick = onNavigateHome,
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Início") }
                )
                NavigationBarItem(
                    selected = isPokedexSelected,
                    onClick = onNavigatePokedex,
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Pokédex") },
                    label = { Text("Pokédex") }
                )
                NavigationBarItem(
                    selected = isTeamSelected,
                    onClick = onNavigateTeam,
                    icon = { Icon(Icons.Default.Person, contentDescription = "Time") },
                    label = { Text("Time") }
                )
            }
        },
        content = content
    )
}
