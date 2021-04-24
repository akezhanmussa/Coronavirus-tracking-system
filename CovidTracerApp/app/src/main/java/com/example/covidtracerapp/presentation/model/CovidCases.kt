package com.example.covidtracerapp.presentation.model

data class CovidCases(
    val gotSickNumPCRplus: Int,
    val recoveredNumPCRplus: Int,
    val diedNumPCRplus: Int,
    val gotSickNumPCRminus: Int,
    val recoverdNumPCRminus: Int,
    val diedNumPCRminus: Int,
    val message: String
)