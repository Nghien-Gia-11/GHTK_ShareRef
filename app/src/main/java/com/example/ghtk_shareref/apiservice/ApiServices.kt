package com.example.ghtk_shareref.apiservice

import com.example.ghtk_shareref.model.Pokemon
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("pokemon?limit=20")
    suspend fun getPokemon(
        @Query("offset") offset : Int
    ) : Pokemon

}