package com.example.customview.dcoffee.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.customview.dcoffee.preferences.UserPreferences

class ViewModelFactory(private val pref: UserPreferences) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel() as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(DetectedCoffeeViewModel::class.java) -> {
                DetectedCoffeeViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown Viewmodel Class: " + modelClass.name)
        }
    }
}