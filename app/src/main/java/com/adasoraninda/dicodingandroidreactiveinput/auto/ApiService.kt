package com.adasoraninda.dicodingandroidreactiveinput.auto

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("mapbox.places/{query}.json")
    suspend fun getCountryFlow(
        @Path("query") query: String,
        @Query("access_token") accessToken: String,
        @Query("autocomplete") autoComplete: Boolean = true
    ): PlaceResponse

    @GET("mapbox.places/{query}.json")
    fun getCountryRx(
        @Path("query") query: String,
        @Query("access_token") accessToken: String,
        @Query("autocomplete") autoComplete: Boolean = true
    ): Single<PlaceResponse>

}