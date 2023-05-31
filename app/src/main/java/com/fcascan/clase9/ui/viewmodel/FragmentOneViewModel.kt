package com.fcascan.clase9.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fcascan.clase9.domain.Pokemon
import com.fcascan.clase9.ui.fragments.LoadingState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FragmentOneViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    var pokemon = MutableLiveData<Pokemon?>()
    val screenState = MutableLiveData<LoadingState>()

    fun getIdFromFirebase(idNumber: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            screenState.value = LoadingState.LOADING
            val poke: Pokemon? = getPokemon(idNumber)
            if (poke != null) {
                poke.imageURL = getPokeImg(idNumber)
                pokemon.postValue(poke)
                screenState.value = LoadingState.SUCCESS
            } else screenState.value = LoadingState.FAILURE
        }
    }

    //Coroutines:
    private suspend fun getPokemon(idNumber: Int): Pokemon? {
        var data: Pokemon? = null
        try {
            data = db.collection("pokemons")
                .whereEqualTo("id", idNumber)
                .get()
                .await()
                .toObjects(Pokemon::class.java)[0]
        } catch (e: Exception) {
            Log.d("FragmentOne", "ERROR: $e.toString()")
        }
        return data
    }

    private suspend fun getPokeImg(idNumber: Int): String? {
        var url: String? = null
        try {
            val imgRef = storage.reference.child("pokemons/${idNumber.toString().padStart(3, '0')}.png")
            url = imgRef.downloadUrl.await().toString()
            Log.d("FragmentOne", "URL: $url")
        } catch (e: Exception) {
            Log.d("FragmentOne", "ERROR: $e.toString()")
        }
        return url
    }
}