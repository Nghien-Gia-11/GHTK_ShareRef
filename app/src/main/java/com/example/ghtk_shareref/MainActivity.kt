package com.example.ghtk_shareref

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.ghtk_shareref.adapter.PokemonAdapter
import com.example.ghtk_shareref.databinding.ActivityMainBinding
import com.example.ghtk_shareref.model.Pokemon
import com.example.ghtk_shareref.model.Result
import com.example.ghtk_shareref.viewmodel.PokemonViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PokemonViewModel

    private lateinit var pokemonAdapter: PokemonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[PokemonViewModel::class.java]

        initPokemonAdapter()

        initData()

        checkStateWifi()

        viewModel.pokemonLocal.observe(this){
            pokemonAdapter.setName(it)
        }
    }

    private fun checkStateWifi() {
        lifecycleScope.launch {
            viewModel.stateNetwork.collect{
                when (it){
                    false -> {
                        showToast("không kết nối mạng")
                        pokemonAdapter.setState("false")
                    }
                    else -> {
                        showToast("có kết nối mạng")
                        pokemonAdapter.setState("true")
                    }
                }
            }
        }
    }

    private fun initData() {
        viewModel.pokemon.observe(this) {
            pokemonAdapter.setPokemon(it)
        }
    }

    private fun initPokemonAdapter() {
        pokemonAdapter = PokemonAdapter(this, Pokemon(), mutableListOf<String>(), "")
        binding.rvPokemon.apply {
            adapter = pokemonAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun showToast(title: String) {
        Toast.makeText(this@MainActivity, title, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}