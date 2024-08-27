
package rma.lv1.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rma.lv1.model.Cocktail

@Composable
fun PersonScreen(cocktails: List<Cocktail>) {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Column {
            Text("Person Screen", fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))
            LazyColumn {
                items(cocktails) { cocktail ->
                    Text(cocktail.name)
                    Text(cocktail.ingredients.joinToString(", "))
                }
            }
        }
    }
}
