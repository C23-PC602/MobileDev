package com.example.customview.dCoffee.main

import androidx.lifecycle.*
import com.example.customview.dCoffee.api.ApiConfig
import com.example.customview.dCoffee.model.UserModel
import com.example.customview.dCoffee.preferences.UserPreferences
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreferences) : ViewModel() {
    private var job: Job? = null
    val loading = MutableLiveData<Boolean>()

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun getToken(): LiveData<UserModel> {
        return pref.getToken().asLiveData()
    }

    fun setData(token: String) {
        loading.value = true
    }

    fun logout() {
        viewModelScope.launch {
            pref.userLogout()
        }
    }
}