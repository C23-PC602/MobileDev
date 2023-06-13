package com.example.customview.dcoffee.model

import androidx.lifecycle.*
import com.example.customview.dcoffee.api.ApiConfig
import com.example.customview.dcoffee.preferences.UserPreferences
import com.example.customview.dcoffee.response.LoginResponse
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreferences) : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    private var job: Job? = null
    val loading = MutableLiveData<Boolean>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun loginUser(email: String, password: String) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val service = ApiConfig.getApiService().login(email, password)
            withContext(Dispatchers.Main) {
                service.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        val responseBody = response.body()
                        if (response.isSuccessful ) {
                            if (responseBody != null && !responseBody.error) {
                                loading.value = false
                                saveToken(responseBody.loginResult.token)
                                onError(responseBody.message)
                            }
                        } else {
                            onError("Error : ${response.message()} ")
                        }
                    }
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        onError("onFailure")
                    }
                })
            }
        }
    }

    fun getToken(): LiveData<UserModel> {
        return pref.getToken().asLiveData()
    }

    fun saveToken(token: String){
        viewModelScope.launch {
            pref.userLogin(token)
        }
    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}