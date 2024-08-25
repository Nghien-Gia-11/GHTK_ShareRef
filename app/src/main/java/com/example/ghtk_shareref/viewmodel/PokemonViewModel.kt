package com.example.ghtk_shareref.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ghtk_shareref.instance.RetrofitInstance
import com.example.ghtk_shareref.model.Pokemon
import com.example.ghtk_shareref.model.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class PokemonViewModel(application: Application) : AndroidViewModel(application) {

    private var _pokemon = MutableLiveData<Pokemon>()
    val pokemon: LiveData<Pokemon> get() = _pokemon

    private var _pokemonLocal = MutableLiveData<MutableList<String>>()
    val pokemonLocal: LiveData<MutableList<String>> get() = _pokemonLocal

    private var _stateNetwork = MutableStateFlow(false)
    val stateNetwork: SharedFlow<Boolean> get() = _stateNetwork

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private lateinit var connectivityManager: ConnectivityManager

    init {
        startNetworkMonitoring()
    }

    private fun fetchPokemon() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getPokemon(0)
                _pokemon.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Exception", e.toString())
            }
        }
    }


    private fun getDataFromSharedPreferences() : MutableList<String>{
        val sharePreference = getApplication<Application>().getSharedPreferences("SaveData", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharePreference.getString("names", null)
        val type = object : TypeToken<MutableList<Result>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }


    private fun saveDataToSharedPreferences(items: List<Result>) {
        val sharePreference = getApplication<Application>().getSharedPreferences("SaveData", Context.MODE_PRIVATE)
        val editor = sharePreference.edit()
        val gson = Gson()
        val nameItems = mutableListOf<String>()
        items.forEach {
            saveImageToLocal(getApplication(), it.url, it.name)
            nameItems.add(it.name)
        }
        val json = gson.toJson(nameItems)
        editor.putString("names", json)
        editor.apply()
    }

    private fun saveImageToLocal(context: Context, imageUrl: String, imageName: String) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val file = File(context.filesDir, imageName)
                    FileOutputStream(file).use { out ->
                        resource.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle cleanup if needed
                }
            })
    }


    private fun startNetworkMonitoring() {
        connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest =
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
                            NetworkCapabilities.TRANSPORT_CELLULAR
                        )
                    ) {
                        _stateNetwork.value = true
                        fetchPokemon()
                        _pokemon.value?.results?.let {  saveDataToSharedPreferences(it) }
                    }
                } else {
                    _stateNetwork.value = false
                    _pokemonLocal.value = getDataFromSharedPreferences()
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                _stateNetwork.value = false
                _pokemonLocal.value = getDataFromSharedPreferences()
            }
        }
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
    }
}
