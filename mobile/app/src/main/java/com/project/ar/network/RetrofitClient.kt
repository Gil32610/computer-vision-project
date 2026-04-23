package com.project.ar.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Replace with your actual machine IP from the hotspot!
    private const val BASE_URL = "http://172.17.31.119:8000/" 

    val instance: ArApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ArApiService::class.java)
    }
}