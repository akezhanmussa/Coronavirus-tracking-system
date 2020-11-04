package com.example.covidtracerapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.covidtracerapp.Repository
import com.example.covidtracerapp.presentation.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: Repository
) : ViewModel(){



    val loginState : MutableLiveData<Resource<User>> =
        MutableLiveData()

    fun onLoginClicked(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            loginState.postValue(Resource.Loading)
            try {
                val user = repository.login(id)
                if (user.id!=null){
                    loginState.postValue(
                        Resource.Success(
                            user
                        )
                    )
                }
            } catch (throwable: Throwable) {
                loginState.postValue(
                    Resource.Error(
                        "User with this ID does not exist"
                    )
                )
            }
        }
    }
}

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val message: String) : Resource<T>()
}