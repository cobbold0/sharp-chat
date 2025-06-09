package com.vortexen.sharpchat.utils

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus

open class BaseViewModel: ViewModel() {
    companion object {
        private const val TAG = "BaseViewModel"
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d(TAG, "CoroutineExceptionHandler : ", throwable)
    }
    private val job = SupervisorJob()
    private val context = Dispatchers.Main + job + exceptionHandler
    val coroutineScope = (viewModelScope + exceptionHandler)
}