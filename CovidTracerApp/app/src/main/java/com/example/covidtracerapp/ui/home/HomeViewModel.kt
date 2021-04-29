package com.example.covidtracerapp.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.covidtracerapp.presentation.model.MyBeacon
import com.example.covidtracerapp.presentation.model.User

class HomeViewModel : ViewModel() {
    val beacons = MutableLiveData<Set<MyBeacon>>()
    val user = MutableLiveData<User>()
}