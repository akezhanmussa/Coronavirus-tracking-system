package com.example.covidtracerapp.api

import com.google.gson.annotations.SerializedName

data class TokenEntity(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("scope")
    val scope: String
)