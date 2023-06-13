package com.example.customview.dcoffee.detect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.customview.dcoffee.api.ApiConfig
import com.example.customview.dcoffee.model.UserModel
import com.example.customview.dcoffee.preferences.UserPreferences
import com.example.customview.dcoffee.response.FileUploadResponse
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetectedCoffeeViewModel(private val pref: UserPreferences) : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    private var job: Job? = null
    val loading = MutableLiveData<Boolean>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun getToken(): LiveData<UserModel> {
        return pref.getToken().asLiveData()
    }

    fun uploadImage(file: MultipartBody.Part, description: RequestBody, token: String) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val service = ApiConfig.getApiService().addStories(file, description,"Bearer $token")
            withContext(Dispatchers.Main) {
                service.enqueue(object : Callback<FileUploadResponse> {
                    override fun onResponse(
                        call: Call<FileUploadResponse>,
                        response: Response<FileUploadResponse>
                    ) {
                        val responseBody = response.body()
                        if (response.isSuccessful ) {
                            if (responseBody != null && !responseBody.error) {
                                loading.value = false
                                onError(responseBody.message)
                            }
                        } else {
                            onError("Error : ${response.message()} ")
                        }
                    }
                    override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                        onError("onFailure")
                    }
                })
            }
        }
    }
}