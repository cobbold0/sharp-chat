package com.vortexen.sharpchat.data

import java.lang.Exception

sealed class PaginatedDataState<out R> {
    class Success<T>(val data: T?)  : PaginatedDataState<T>()
    class Error(val exception: Exception) : PaginatedDataState<Nothing>()
    data object Empty : PaginatedDataState<Nothing>()
    data object LoadingMore : PaginatedDataState<Nothing>()
    data object Loading : PaginatedDataState<Nothing>()
}