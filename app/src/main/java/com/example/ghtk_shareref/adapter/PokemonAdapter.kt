package com.example.ghtk_shareref.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ghtk_shareref.R
import com.example.ghtk_shareref.databinding.LayoutItemPokemonBinding
import com.example.ghtk_shareref.model.Pokemon
import java.io.File
import java.lang.ref.WeakReference

class PokemonAdapter(
    private val context: Context,
    private var pokemon: Pokemon,
    private var names: MutableList<String>,
    private var stateWifi: String
) :
    RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {

    private val contextRef = WeakReference(context)

    private val colors: List<Int> = listOf(
        R.color.blue,
        R.color.pink,
        R.color.yellow,
    )
    private val urlImg =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"

    class ViewHolder(var binding: LayoutItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = contextRef.get()
        val view = LayoutInflater.from(context)
        val binding = LayoutItemPokemonBinding.inflate(view, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = pokemon.results.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvName.text = pokemon.results[position].name
        val url = pokemon.results[position].url.split("/")
        val numberItem = url[url.size - 2]
        holder.binding.tvNumber.text = numberItem
        val randomColor = colors.random()
        holder.binding.layoutItemPokemon.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, randomColor))

        if (stateWifi == "true") {
            Glide
                .with(holder.itemView.context)
                .load("$urlImg$numberItem.png")
                .into(holder.binding.imgPokemon)
        } else {
            names.forEach {
                val file = File(context.filesDir, it)
                if (file.exists()) {
                    Glide.with(context).load(file).into(holder.binding.imgPokemon)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPokemon(pokemon: Pokemon) {
        this.pokemon = Pokemon()
        this.pokemon = pokemon
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setName(listName: MutableList<String>) {
        this.names = mutableListOf()
        this.names = listName
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setState(state: String) {
        this.stateWifi = ""
        this.stateWifi = state
        notifyDataSetChanged()
    }

}