package com.example.covidtracerapp.presentation.model

import java.io.Serializable

data class User(
    val datePositive: String,
    val id: String,
    val phone: String,
    val positive: Boolean,
    val city: String,
    val country: String
) : Serializable