package rma.lv1.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import rma.lv1.model.Cocktail
import rma.lv1.viewmodel.CocktailViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val personScreenCocktails = remember { mutableStateListOf<Cocktail>() }
    val favoriteScreenCocktails = remember { mutableStateListOf<Cocktail>() }
    val viewModel: CocktailViewModel = viewModel() // Dodano da bi se koristio ViewModel

    // Učitaj omiljene koktele iz Firebase-a kada se MainScreen pokrene
    LaunchedEffect(Unit) {
        viewModel.loadFavorites { loadedFavorites ->
            favoriteScreenCocktails.clear()
            favoriteScreenCocktails.addAll(loadedFavorites)
        }
    }

    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Person,
        Screen.Favorites
    )

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController, items = items) }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Home.route, Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Search.route) {
                SearchScreen(
                    navController = navController,
                    onPersonSelected = { cocktail ->
                        if (!personScreenCocktails.contains(cocktail)) {
                            personScreenCocktails.add(cocktail)
                        }
                    },
                    onFavoriteSelected = { cocktail ->
                        if (!favoriteScreenCocktails.contains(cocktail)) {
                            favoriteScreenCocktails.add(cocktail)
                            viewModel.saveFavorites(favoriteScreenCocktails) // Spremi u Firebase
                        }
                    }
                )
            }
            composable(Screen.Person.route) { PersonScreen(cocktails = personScreenCocktails) }
            composable(Screen.Favorites.route) {
                // Prosljeđivanje ViewModel-a FavoritesScreen-u
                FavoritesScreen(
                    viewModel = viewModel,
                    onCocktailChecked = { cocktail ->
                        if (!personScreenCocktails.contains(cocktail)) {
                            personScreenCocktails.add(cocktail)
                            viewModel.savePerson(personScreenCocktails) // Spremi u Firebase
                        }
                    },
                    onCocktailDeleted = { cocktail ->
                        favoriteScreenCocktails.remove(cocktail)
                        viewModel.saveFavorites(favoriteScreenCocktails) // Ažuriraj Firebase
                    }
                )
            }
        }
    }
}

sealed class Screen(val route: String, val icon: ImageVector) {
    object Home : Screen("home", Icons.Filled.Home)
    object Search : Screen("search", Icons.Filled.Search)
    object Person : Screen("person", Icons.Filled.Person)
    object Favorites : Screen("favorites", Icons.Filled.Favorite)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
