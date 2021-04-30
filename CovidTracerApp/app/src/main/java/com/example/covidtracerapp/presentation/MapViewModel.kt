package com.example.covidtracerapp.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.covidtracerapp.Repository
import com.example.covidtracerapp.presentation.model.HotSpotCoordinate
import com.example.covidtracerapp.presentation.model.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.stream.Collector

class MapViewModel(
    private val repository: Repository
) : ViewModel() {

    val locationsState: MutableLiveData<Resource<List<HotSpotCoordinate>>> =
        MutableLiveData()

    fun getHotspotsByLocation(userLocation: Location){

//        val fakeLocations = listOf(
//            Location(51.1666679,71.4333344,18),
//            Location(51.0269852,71.4608765,96)
//        )
//
//        locationsState.postValue(Resource.Success(fakeLocations))
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val locations = repository.getHotspotsByLocation(userLocation).filter { c -> c.radius > 0 }
                Log.d("TAG", "getLocationsByCity: " + locations)
                if (locations.isNotEmpty()){
                    locationsState.postValue(
                        Resource.Success(
                            locations
                        )
                    )
                }
            } catch (throwable: Throwable) {
                locationsState.postValue(
                    Resource.Error(
                        "Error getting locations!"
                    )
                )
            }
        }
    }

    fun refreshHotspots(userLocation: Location){
        getHotspotsByLocation(userLocation)
    }

}