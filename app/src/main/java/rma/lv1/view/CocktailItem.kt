package rma.lv1.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import rma.lv1.model.Cocktail

@Composable
fun CocktailItem(
    cocktail: Cocktail,
    onPersonSelected: (Cocktail) -> Unit,
    onFavoriteSelected: (Cocktail) -> Unit
) {
    var isPersonSelected by remember { mutableStateOf(false) }
    var isFavoriteSelected by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isPersonSelected,
            onCheckedChange = {
                isPersonSelected = it
                onPersonSelected(cocktail)
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = cocktail.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = cocktail.ingredients.joinToString(", "),
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        Icon(
            imageVector = if (isFavoriteSelected) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = null,
            tint = if (isFavoriteSelected) Color.Red else Color.Gray,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    isFavoriteSelected = !isFavoriteSelected
                    onFavoriteSelected(cocktail)
                }
        )
    }
}
