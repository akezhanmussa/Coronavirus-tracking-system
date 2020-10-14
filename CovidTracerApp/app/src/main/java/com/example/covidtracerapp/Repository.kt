package com.example.covidtracerapp

interface Repository {
    suspend fun login(id: String) : User
}