package com.example.covidtracerapp.presentation.model

import java.io.Serializable

data class Location(
    val city: String,
    val country: String
) : Serializable