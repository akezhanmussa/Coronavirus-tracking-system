package com.example.covidtracerapp

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ShowBeaconsViewModel(
    private val repository: Repository
) : ViewModel(){

    private val disposable = CompositeDisposable()
    val listOfPositive : MutableLiveData<List<String>> = MutableLiveData()

    fun startTracing(){
        disposable.add(
            repository.getPositive()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .repeatWhen { completed -> completed.delay(10, TimeUnit.SECONDS) }
                .subscribe({
                    listOfPositive.value = it
                }, {
                    Log.d("TAG", "List of positive: ERROR")
                })

        )
    }
}