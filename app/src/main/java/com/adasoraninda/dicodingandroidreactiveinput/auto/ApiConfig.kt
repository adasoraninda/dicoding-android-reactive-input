package com.adasoraninda.dicodingandroidreactiveinput.auto

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    const val ACCESS_TOKEN =
        ""

    fun provideApiServiceFlow(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.mapbox.com/geocoding/v5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }

    fun provideApiServiceRx(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.mapbox.com/geocoding/v5/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }
}