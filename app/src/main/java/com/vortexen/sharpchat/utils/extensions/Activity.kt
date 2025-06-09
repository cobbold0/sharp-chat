package com.vortexen.sharpchat.utils.extensions

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.vortexen.sharpchat.R
import com.vortexen.sharpchat.data.DataState
import com.vortexen.sharpchat.data.PaginatedDataState
import com.vortexen.sharpchat.utils.Event

fun Activity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.showToast(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun Activity.notify(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}

fun Activity.notify(view: View, resId: Int) {
    Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show()
}

fun Activity.findNavController(): NavController {
    return findNavController(R.id.nav_host_fragment)
}

fun <T> AppCompatActivity.observeLiveDataState(
    liveData: LiveData<DataState<T>>,
    enableProgressBar: Boolean = false,
    onFailure: ((String) -> Unit)? = null,
    onSuccess: (T) -> Unit
) {
    liveData.observe(this) { result ->
        when (result) {
            is DataState.Loading -> {
                // TODO: Show loading dialog
            }

            is DataState.Success -> {
                result.data?.let { onSuccess(it) }

            }

            is DataState.Error -> {
                result.exception.message?.let { errorMsg ->
                    onFailure?.invoke(errorMsg)
                }

            }
        }
    }
}

fun <T> AppCompatActivity.observeEventLiveData(
    liveData: LiveData<Event<DataState<T>>>,
    enableProgressBar: Boolean = true,
    onFailure: ((String) -> Unit)? = null,
    onSuccess: (T) -> Unit
) {
    liveData.observe(this) { event ->
        event.getContentIfNotHandled()?.let { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                }

                is DataState.Success -> {
                    dataState.data?.let { onSuccess(it) }
                }

                is DataState.Error -> {
                    dataState.exception.message?.let { errorMsg ->
                        onFailure?.invoke(errorMsg)  // Pass errorMsg to the onError lambda
                    }

                }
            }
        }
    }
}

fun <T> AppCompatActivity.observeLivePaginatedDataState(
    liveData: LiveData<PaginatedDataState<T>>,
    onFailure: ((String) -> Unit)? = null,
    onLoading: () -> Unit,
    onLoadingMore: () -> Unit,
    onEmpty: () -> Unit,
    onSuccess: (T) -> Unit
) {
    liveData.observe(this) { result ->
        when (result) {
            is PaginatedDataState.Loading -> {
                onLoading()
            }

            is PaginatedDataState.LoadingMore -> {
                onLoadingMore()
            }

            is PaginatedDataState.Empty -> {
                onEmpty()
            }

            is PaginatedDataState.Success -> {
                result.data?.let { onSuccess(it) }

            }

            is PaginatedDataState.Error -> {
                result.exception.message?.let { errorMsg ->
                    onFailure?.invoke(errorMsg)
                }
            }
        }
    }
}

