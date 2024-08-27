package rma.lv1.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import rma.lv1.viewmodel.CocktailViewModel
import rma.lv1.model.Cocktail

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: CocktailViewModel = viewModel(),
    onPersonSelected: (Cocktail) -> Unit,
    onFavoriteSelected: (Cocktail) -> Unit
) {
    val cocktails by viewModel.cocktails.collectAsState()
    var cocktailName by remember { mutableStateOf(TextFieldValue("Bloody Mary")) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicTextField(
                value = cocktailName,
                onValueChange = { cocktailName = it },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = { viewModel.loadCocktails(cocktailName.text) }) {
                Text("Search")
            }

            LazyColumn {
                items(cocktails) { cocktail ->
                    CocktailItem(
                        cocktail = cocktail,
                        onPersonSelected = {
                            onPersonSelected(cocktail)
                            // Dodaj koktel u "person" u Firebase
                            viewModel.savePerson(listOf(cocktail))
                        },
                        onFavoriteSelected = {
                            onFavoriteSelected(cocktail)
                            // Dodaj koktel u "favorites" u Firebase
                            viewModel.saveFavorites(listOf(cocktail))
                        }
                    )
                }
            }
        }
    }
}