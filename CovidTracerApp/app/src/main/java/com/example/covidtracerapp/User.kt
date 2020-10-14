package com.example.covidtracerapp

import java.io.Serializable

data class User(
    val datePositive: String,
    val id: String,
    val phone: String,
    val positive: Boolean
) : Serializable