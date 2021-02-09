package com.example.covidtracerapp

import com.example.covidtracerapp.database.ContactedEntity
import com.example.covidtracerapp.presentation.model.Location
import com.example.covidtracerapp.presentation.model.User
import io.reactivex.Observable


interface Repository {
    suspend fun login(id: String) : User
    suspend fun selfReveal(id: String)
    fun getPositive() : Observable<List<String>>
    fun getPositiveByLocation(country: String, city: String) : Observable<List<User>>
    suspend fun getAllContacted() : List<ContactedEntity>
    suspend fun getAllContactedIds() : List<String>
    suspend fun getLocationsByCity(cityName: String) : List<Location>
    suspend fun insertContacted(contactedEntity: ContactedEntity)
    suspend fun sendContacted(city: String, country: String, contactedIds: List<String>) : List<User>
    suspend fun deleteContacted(contactedEntity: ContactedEntity)
}