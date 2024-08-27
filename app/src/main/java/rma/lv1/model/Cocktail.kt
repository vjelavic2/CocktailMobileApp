package rma.lv1.model

data class Cocktail(
    val id: Int = 0,
    val name: String = "",
    val ingredients: List<String> = emptyList()
)
