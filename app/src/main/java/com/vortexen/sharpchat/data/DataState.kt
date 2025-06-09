package com.vortexen.sharpchat.data

import java.lang.Exception

sealed class DataState<out R> {
    class Success<T>(val data: T?)  : DataState<T>()
    class Error(val exception: Exception) : DataState<Nothing>()
    object Loading : DataState<Nothing>()
}