package com.example.covidtracerapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.covidtracerapp.MainRepository
import com.example.covidtracerapp.Repository
import com.example.covidtracerapp.database.ContactedEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseNotificationViewModel(
    val repository: Repository
) : ViewModel() {

    val localListState = MutableLiveData<Resource<ContactedEntity>?>()

    fun checkLocalList(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            val isPresent = repository.getAllContactedIds().contains(id)
            if (isPresent){
                val contacted = repository.getContactedPerson(id)
                localListState.postValue(Resource.Success(contacted))
            }else{
                localListState.postValue(null)
            }
        }
    }

}