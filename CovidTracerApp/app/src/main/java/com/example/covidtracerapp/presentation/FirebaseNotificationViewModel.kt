package com.example.covidtracerapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.covidtracerapp.MainRepository
import com.example.covidtracerapp.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseNotificationViewModel(
    val repository: Repository
) : ViewModel() {

    val localListState = MutableLiveData<Resource<Boolean>>()

    fun checkLocalList(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getAllContactedIds().contains(id)
            localListState.postValue(Resource.Success(result))
        }
    }

}