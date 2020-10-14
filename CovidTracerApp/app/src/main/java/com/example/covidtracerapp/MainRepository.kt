package com.example.covidtracerapp

import android.util.Log

class MainRepository(
    private val covidApi: CovidApi
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
                positive = response["positive"] as Boolean
            )
        }
    }
}