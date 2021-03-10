package com.example.covidtracerapp.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("oauth/token")
    suspend fun getToken(@Query("grant_type") grantType: String,
                         @Query("username") username: String,
                         @Query("password") password: String): TokenEntity

//    suspend fun getToken(@Body data: Map<String, String>): TokenEntity
}