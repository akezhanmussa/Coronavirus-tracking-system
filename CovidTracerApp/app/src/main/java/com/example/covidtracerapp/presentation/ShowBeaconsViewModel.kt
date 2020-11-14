package com.example.covidtracerapp.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.covidtracerapp.Repository
import com.example.covidtracerapp.presentation.model.User
import com.example.covidtracerapp.database.ContactedEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ShowBeaconsViewModel(
    private val repository: Repository
) : ViewModel(){

    private val disposable = CompositeDisposable()
    val listOfPositive : MutableLiveData<List<User>> = MutableLiveData()
    val dbState : MutableLiveData<Resource<List<ContactedEntity>>> =
        MutableLiveData()

    fun startTracing(city: String, country: String){
//        disposable.add(
//            repository.getAllContactedIds()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .repeatWhen { completed -> completed.delay(10, TimeUnit.SECONDS) }
//                .subscribe({
//                    viewModelScope.launch {
//                        var res = repository.sendContacted(city, country, it)
//                        Log.d("TAG", "startTracing: " + res)
//                    }
//                }, {
//                    Log.d("TAG", "List of positive: ERROR")
//                })
//        )

        viewModelScope.launch(Dispatchers.IO) {

            var list  : List<String> = repository.getAllContactedIds()
            var res = repository.sendContacted(city, country, list)
            Log.d("TAG", "startTracing: " + res[0].id)

        }
    }

    fun insertContacted(contactedEntity: ContactedEntity){
        viewModelScope.launch(Dispatchers.IO) {
            dbState.postValue(Resource.Loading)
            try {
                repository.insertContacted(contactedEntity)
                Log.d(ShowBeaconsViewModel::class.java.simpleName, "Inserted")
                dbState.postValue(Resource.Success(listOf()))
            } catch (throwable: Throwable) {
                dbState.postValue(
                    Resource.Error(
                        "Error adding to db"
                    )
                )
            }
        }
    }

    fun getAllContacted() {
        viewModelScope.launch(Dispatchers.IO) {
            dbState.postValue(Resource.Loading)
            try {
                val contactedUsers = repository.getAllContacted()
                dbState.postValue(Resource.Success(contactedUsers))
            } catch (throwable: Throwable) {
                dbState.postValue(
                    Resource.Error(
                        "Error adding to db"
                    )
                )
            }
        }
    }
}