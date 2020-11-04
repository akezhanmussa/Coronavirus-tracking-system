package com.example.covidtracerapp

import android.util.Log
import com.example.covidtracerapp.api.CovidApi
import com.example.covidtracerapp.database.ContactedDAO
import com.example.covidtracerapp.database.ContactedEntity
import com.example.covidtracerapp.presentation.model.User
import io.reactivex.Observable
import kotlin.collections.HashMap

class MainRepository(
    private val covidApi: CovidApi,
    private val contactedDAO: ContactedDAO
) : Repository {
    override suspend fun login(id: String): User {
        val body: HashMap<String, String> = HashMap()
        body["id"] = id
        var response = covidApi.login(body)
        if (response["errorMessage"]!=null){
            Log.d("TAG", "login: SSSSSSSSSSSs" + response["errorMessage"])
            throw Exception(response["errorMessage"] as String)
        }else{
            return User(
                datePositive = response["datePositive"] as String,
                id = response["id"] as String,
                phone = response["phone"] as String,
                positive = response["positive"] as Boolean,
                city = response["city"] as String,
                country = response["country"] as String
            )
        }
    }

    override fun getPositive(): Observable<List<String>> {
        return covidApi.getPositive()
    }

    override fun getPositiveByLocation(city: String, country: String): Observable<List<User>> {
        val body: HashMap<String, String> = HashMap()
        body["city"] = city
        body["country"] = country
        val positiveByLocation = covidApi.getPositiveByLocation(body)
        return positiveByLocation
    }

    override suspend fun getAllContacted() : List<ContactedEntity> {
        return contactedDAO.getAllContacted()
    }

    override suspend fun insertContacted(contactedEntity: ContactedEntity) {
        contactedDAO.insertContacted(contactedEntity)
    }

    override suspend fun deleteContacted(contactedEntity: ContactedEntity) {
        contactedDAO.deleteContacted(contactedEntity)
    }
}