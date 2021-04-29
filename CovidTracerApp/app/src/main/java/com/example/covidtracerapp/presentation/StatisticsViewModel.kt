package com.example.covidtracerapp.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.covidtracerapp.Repository
import com.example.covidtracerapp.presentation.model.CovidCases
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val repository: Repository
) : ViewModel() {

    val stats = MutableLiveData<CovidCases>()

    fun getCovidCasesByLocation(city: String, country: String){
        viewModelScope.launch {
            val covidCases = repository.getCovidCasesByLocation(city, country)
            Log.v(StatisticsViewModel::class.java.simpleName, "Covid Cases: ${covidCases.vaccinatedMessage}, ${covidCases.vaccinatedNum}, ${covidCases.doubleVaccinatedNum}" +
                    ", ${covidCases.message}")
            stats.value = covidCases
        }
    }
}