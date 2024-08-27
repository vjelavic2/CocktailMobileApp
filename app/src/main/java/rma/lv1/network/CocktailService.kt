package rma.lv1.network

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import rma.lv1.model.Cocktail

interface CocktailService {
    @Headers("X-Api-Key: F2q2KNqhbdqYCOZFkRDopg==AJrFp108BQkXBHZA")  // Replace YOUR_API_KEY with your actual API key
    @GET("cocktail")
    suspend fun getCocktail(@Query("name") name: String): List<Cocktail>
}

