package com.example.covidtracerapp.api

import com.example.covidtracerapp.presentation.model.User
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET

import retrofit2.http.POST


interface CovidApi {
//    url = google.com/api/v1/
//
//    @GET("/qod") //google.com/qod
//    @GET("qod")//google.com/api/v1/qod

//    https://api.forismatic.com/api/1.0/?method=getQuote&lang=en&format=json
//    https://google.com/api/search?q=123123

    @POST("api/database-check")
    suspend fun login(@Body id: Map<String, String>): Map<String, Object>

    @GET("data-api/get-all-positive")
    fun getPositive() : Observable<List<String>>

    @POST("data-api/get-all-positive-by-location")
    fun getPositiveByLocation(@Body location: Map<String, String>) : Observable<List<User>>

//    @GET("history")
//    suspend fun getDetails(@Query("country") country: String): DetailsRemoteDTOX
}