package com.example.covidtracerapp.presentation.model

import java.io.Serializable

data class HotSpotCoordinate(
    var latitude: Double,
    var longitude: Double,
    var radius: Int,
    var cases: Int
) : Serializable