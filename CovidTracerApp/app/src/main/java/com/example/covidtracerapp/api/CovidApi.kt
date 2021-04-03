package com.example.covidtracerapp.api

import com.example.covidtracerapp.presentation.model.HotSpotCoordinate
import com.example.covidtracerapp.presentation.model.User
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

import retrofit2.http.POST
import retrofit2.http.Query


interface CovidApi {

    @POST("api/database-check")
    suspend fun login(@Header("Authorization") token: String,
                      @Body id: Map<String, String>): User

    @POST("data-api/set-to-be-infected")
    suspend fun selfReveal(@Header("Authorization") token: String,
                           @Body id: Map<String, String>)

    @GET("data-api/get-all-positive")
    fun getPositive() : Observable<List<String>>

    @GET("data-api/hotspotsV2")
    suspend fun getHotspotsByLocation(@Header("Authorization") token: String,
                                      @Query("city") city: String,
                                      @Query("country") country: String,
                                      @Query("limit") limit: Int = 15) : List<HotSpotCoordinate>

    @POST("data-api/get-all-positive-by-location")
    fun getPositiveByLocation(@Header("Authorization") token: String,
                              @Body location: Map<String, String>) : Observable<List<User>>

    @POST("data-api/check-positive-with-my-list")
    suspend fun sendContactedIds(@Header("Authorization") token: String,
                                 @Query("city") city: String,
                                 @Query("country") country: String,
                                 @Body ids: List<String>) : List<User>

    @POST("data-api/new-case")
    suspend fun sendLocationOfHotspot(@Body body: Map<String, Double>)

}