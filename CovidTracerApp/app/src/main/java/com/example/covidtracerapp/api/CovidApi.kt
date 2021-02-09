package com.example.covidtracerapp.api

import com.example.covidtracerapp.presentation.model.Location
import com.example.covidtracerapp.presentation.model.User
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET

import retrofit2.http.POST
import retrofit2.http.Query


interface CovidApi {


    @POST("api/database-check")
    suspend fun login(@Body id: Map<String, String>): Map<String, Object>

    @POST("data-api/set-to-be-infected")
    suspend fun selfReveal(@Body id: Map<String, String>)

    @GET("data-api/get-all-positive")
    fun getPositive() : Observable<List<String>>

    @GET("data-api/get-locations")
    suspend fun getLocationsByCity(@Query("city") cityName: String) : List<Location>

    @POST("data-api/get-all-positive-by-location")
    fun getPositiveByLocation(@Body location: Map<String, String>) : Observable<List<User>>

    @POST("data-api/check-positive-with-my-list")
    suspend fun sendContactedIds(@Query("city") city: String,
                                 @Query("country") country: String,
                                 @Body ids: List<String>) : List<User>
}