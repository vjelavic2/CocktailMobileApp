
package rma.lv1.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import rma.lv1.model.Cocktail
import rma.lv1.network.RetrofitInstance

class CocktailViewModel : ViewModel() {

    private val _cocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    val cocktails: StateFlow<List<Cocktail>> = _cocktails

    private val db = FirebaseFirestore.getInstance()
    private val userId get() = FirebaseAuth.getInstance().currentUser?.uid

    fun loadCocktails(cocktailName: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getCocktail(cocktailName)
                _cocktails.value = response
                Log.d("CocktailViewModel", "Cocktails loaded: ${response}")
            } catch (e: Exception) {
                Log.e("CocktailViewModel", "Error loading cocktails", e)
            }
        }
    }

    fun saveFavorites(cocktails: List<Cocktail>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { id ->
            val userDocument = db.collection("users").document(id)

            userDocument.get().addOnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.exists()) {
                    // Kreirajte dokument s praznim favorites poljem
                    userDocument.set(mapOf("favorites" to emptyList<Cocktail>())).addOnSuccessListener {
                        // Sada dodajte koktele
                        addCocktailsToFavorites(userDocument, cocktails)
                    }.addOnFailureListener { e ->
                        Log.e("CocktailViewModel", "Error creating user document", e)
                    }
                } else {
                    addCocktailsToFavorites(userDocument, cocktails)
                }
            }.addOnFailureListener { e ->
                Log.e("CocktailViewModel", "Error fetching user document", e)
            }
        }
    }

    private fun addCocktailsToFavorites(userDocument: DocumentReference, cocktails: List<Cocktail>) {
        cocktails.forEach { cocktail ->
            userDocument.update("favorites", FieldValue.arrayUnion(cocktail.toMap()))
                .addOnSuccessListener {
                    Log.d("CocktailViewModel", "Cocktail successfully added to favorites.")
                }
                .addOnFailureListener { e ->
                    Log.w("CocktailViewModel", "Error adding cocktail to favorites", e)
                }
        }
    }

    fun savePerson(cocktails: List<Cocktail>) {
        userId?.let { id ->
            val userDocument = db.collection("users").document(id)

            userDocument.get().addOnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.exists()) {
                    // Kreirajte dokument s praznim person poljem
                    userDocument.set(mapOf("person" to emptyList<Cocktail>())).addOnSuccessListener {
                        // Sada dodajte koktele
                        addCocktailsToPerson(userDocument, cocktails)
                    }.addOnFailureListener { e ->
                        Log.e("CocktailViewModel", "Error creating user document", e)
                    }
                } else {
                    addCocktailsToPerson(userDocument, cocktails)
                }
            }.addOnFailureListener { e ->
                Log.e("CocktailViewModel", "Error fetching user document", e)
            }
        }
    }

    private fun addCocktailsToPerson(userDocument: DocumentReference, cocktails: List<Cocktail>) {
        cocktails.forEach { cocktail ->
            userDocument.update("person", FieldValue.arrayUnion(cocktail.toMap()))
                .addOnSuccessListener {
                    Log.d("CocktailViewModel", "Cocktail successfully added to person.")
                }
                .addOnFailureListener { e ->
                    Log.w("CocktailViewModel", "Error adding cocktail to person", e)
                }
        }
    }
    fun loadFavorites(onResult: (List<Cocktail>) -> Unit) {
        userId?.let { id ->
            db.collection("users").document(id).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val favorites = document.get("favorites") as? List<*>
                    val cocktailList = favorites?.mapNotNull { it as? Map<String, Any> }?.map { it.toCocktail() } ?: emptyList()
                    onResult(cocktailList)
                } else {
                    onResult(emptyList())
                }
            }
        }
    }
    fun deleteFavorite(cocktail: Cocktail) {
        userId?.let { id ->
            val userDocument = db.collection("users").document(id)

            // Uklanjanje koktela pomoću FieldValue.arrayRemove()
            userDocument.update("favorites", FieldValue.arrayRemove(cocktail.toMap()))
                .addOnSuccessListener {
                    Log.d("CocktailViewModel", "Cocktail successfully removed from favorites")
                }
                .addOnFailureListener { e ->
                    Log.e("CocktailViewModel", "Error removing cocktail from favorites", e)
                }
        }
    }

    fun loadPerson(onResult: (List<Cocktail>) -> Unit) {
        userId?.let { id ->
            db.collection("users").document(id).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val person = document.get("person") as? List<*>
                    val cocktailList = person?.mapNotNull { it as? Map<String, Any> }?.map { it.toCocktail() } ?: emptyList()
                    onResult(cocktailList)
                } else {
                    onResult(emptyList())
                }
            }
        }
    }
}

// Extension functions to convert between Cocktail and Map
private fun Cocktail.toMap(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "name" to name,
        "ingredients" to ingredients
    )
}

private fun Map<String, Any>.toCocktail(): Cocktail {
    return Cocktail(
        id = (this["id"] as Long).toInt(),
        name = this["name"] as String,
        ingredients = this["ingredients"] as List<String>
    )
}
