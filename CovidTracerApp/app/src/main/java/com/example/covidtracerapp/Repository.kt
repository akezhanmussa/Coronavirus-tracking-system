package com.example.covidtracerapp

import io.reactivex.Observable


interface Repository {
    suspend fun login(id: String) : User
    fun getPositive() : Observable<List<String>>
}