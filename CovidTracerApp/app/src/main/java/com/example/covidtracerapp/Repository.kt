package com.example.covidtracerapp

import com.example.covidtracerapp.api.TokenEntity
import com.example.covidtracerapp.database.ContactedEntity
import com.example.covidtracerapp.presentation.model.CovidCases
import com.example.covidtracerapp.presentation.model.HotSpotCoordinate
import com.example.covidtracerapp.presentation.model.Location
import com.example.covidtracerapp.presentation.model.User
import io.reactivex.Observable


interface Repository {
    suspend fun saveTokenToSharedPrefs(tokenEntity: TokenEntity)
    suspend fun getToken(id: String, password: String) : TokenEntity
    suspend fun login(id: String) : User
    suspend fun selfReveal(id: String)
    fun getPositive() : Observable<List<String>>
    fun getPositiveByLocation(country: String, city: String) : Observable<List<User>>
    suspend fun getAllContacted() : List<ContactedEntity>
    suspend fun getAllContactedIds() : List<String>
    suspend fun getContactedPerson(id: String): ContactedEntity
    suspend fun getHotspotsByLocation(userLocation: Location) : List<HotSpotCoordinate>
    suspend fun sendLocationOfHotspot(lat: Double, lon: Double)
    suspend fun insertContacted(contactedEntity: ContactedEntity)
    suspend fun sendContacted(city: String, country: String, contactedIds: List<String>) : List<User>
    suspend fun deleteContacted(contactedEntity: ContactedEntity)
    suspend fun getCovidCasesByLocation(city: String, country: String) : CovidCases
}