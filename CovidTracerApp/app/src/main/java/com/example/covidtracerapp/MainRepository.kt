package com.example.covidtracerapp

import android.content.SharedPreferences
import android.util.Log
import com.example.covidtracerapp.api.AuthApi
import com.example.covidtracerapp.api.CovidApi
import com.example.covidtracerapp.api.TokenEntity
import com.example.covidtracerapp.database.ContactedDAO
import com.example.covidtracerapp.database.ContactedEntity
import com.example.covidtracerapp.presentation.model.HotSpotCoordinate
import com.example.covidtracerapp.presentation.model.Location
import com.example.covidtracerapp.presentation.model.User
import io.reactivex.Observable
import kotlin.collections.HashMap

class MainRepository(
    private val authApi: AuthApi,
    private val covidApi: CovidApi,
    private val contactedDAO: ContactedDAO,
    private val sharedPreferences: SharedPreferences
) : Repository {

    private var token: String = ""

    override suspend fun getToken(id: String, password: String) : TokenEntity {
        val body: HashMap<String, String> = HashMap()
        body["grant_type"] = "password"
        body["username"] = id
        body["password"] = password

        val tokenEntity = authApi.getToken("password", id, password)
        saveTokenToSharedPrefs(tokenEntity)
        return tokenEntity
    }

    override suspend fun saveTokenToSharedPrefs(tokenEntity: TokenEntity){
        token = "Bearer " + tokenEntity.accessToken
        sharedPreferences.edit().apply{
            putString("USER_TOKEN", token)
        }.apply()
    }

    override suspend fun login(id: String): User {
        Log.d("TAG", "login: TOKEN IS " + token)
        val body: HashMap<String, String> = HashMap()
        body["id"] = id
        return covidApi.login(token, body)
    }

    override suspend fun selfReveal(id: String) {
        val body: HashMap<String, String> = HashMap()
        body["id"] = id
        covidApi.selfReveal(token, body)
    }

    override fun getPositive(): Observable<List<String>> {
        return covidApi.getPositive()
    }

    override fun getPositiveByLocation(city: String, country: String): Observable<List<User>> {
        val body: HashMap<String, String> = HashMap()
        body["city"] = city
        body["country"] = country
        return covidApi.getPositiveByLocation(token, body)
    }

    override suspend fun getAllContacted() : List<ContactedEntity> {
        return contactedDAO.getAllContacted()
    }

    override suspend fun getAllContactedIds(): List<String> {
        return contactedDAO.getAllContactedIds()
    }

    override suspend fun getContactedPerson(id: String): ContactedEntity {
        return contactedDAO.getContactedPerson(id)
    }

    override suspend fun getHotspotsByLocation(userLocation: Location): List<HotSpotCoordinate> {
        return covidApi.getHotspotsByLocation(token, userLocation.city, userLocation.country)
    }

    override suspend fun sendLocationOfHotspot(lat: Double, lon: Double) {
        val body = mapOf(
            "latitude" to lat,
            "longitude" to lon
        )
        covidApi.sendLocationOfHotspot(body)
    }

    override suspend fun insertContacted(contactedEntity: ContactedEntity) {
        contactedDAO.insertContacted(contactedEntity)
    }

    override suspend fun sendContacted(city: String, country: String, contactedIds: List<String>) : List<User> {
        return covidApi.sendContactedIds(token, city, country, contactedIds)
//        return covidApi.sendContactedIds("Astana", "Kazakhstan", listOf("010101000002", "010101000004"))
    }

    override suspend fun deleteContacted(contactedEntity: ContactedEntity) {
        contactedDAO.deleteContacted(contactedEntity)
    }
}