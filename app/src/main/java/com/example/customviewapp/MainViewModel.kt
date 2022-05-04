package com.example.customviewapp

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val isLoading = ObservableBoolean(true)

    init {
        viewModelScope.launch {
            delay(3000)
            isLoading.set(false)
        }
    }
}