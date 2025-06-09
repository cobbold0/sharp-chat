package com.vortexen.sharpchat.utils.extensions

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.vortexen.sharpchat.data.DataState
import com.vortexen.sharpchat.data.PaginatedDataState
import com.vortexen.sharpchat.utils.Event
import com.vortexen.sharpchat.utils.LoadingDialog

fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.showToast(resId: Int) {
    Toast.makeText(requireContext(), resId, Toast.LENGTH_SHORT).show()
}

fun Fragment.notify(message: String) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
}

fun Fragment.notify(resId: Int) {
    Snackbar.make(requireView(), resId, Snackbar.LENGTH_SHORT).show()
}

fun Fragment.navigateBack() {
    findNavController().popBackStack()
}

fun <T> Fragment.observeLiveDataState(
    liveData: LiveData<DataState<T>>,
    enableProgressBar: Boolean = false,
    onFailure: ((String) -> Unit)? = null,
    onSuccess: (T) -> Unit
) {
    val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireActivity()) }
    liveData.observe(this) { result ->
        when (result) {
            is DataState.Loading -> {
                if (enableProgressBar) {
                    loadingDialog.setLoading(true)
                }
            }

            is DataState.Success -> {
                result.data?.let { onSuccess(it) }
                loadingDialog.setLoading(false)
            }

            is DataState.Error -> {
                result.exception.message?.let { errorMsg ->
                    onFailure?.invoke(errorMsg)
                }
                loadingDialog.setLoading(false)
            }
        }
    }
}

fun <T> Fragment.observeEventLiveData(
    liveData: LiveData<Event<DataState<T>>>,
    enableProgressBar: Boolean = true,
    onFailure: ((String) -> Unit)? = null,
    onSuccess: (T) -> Unit
) {
    val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireActivity()) }
    liveData.observe(this) { event ->
        event.getContentIfNotHandled()?.let { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                    if (enableProgressBar) {
                        loadingDialog.setLoading(true)
                    }
                }

                is DataState.Success -> {
                    dataState.data?.let { onSuccess(it) }
                    loadingDialog.setLoading(false)
                }

                is DataState.Error -> {
                    dataState.exception.message?.let { errorMsg ->
                        onFailure?.invoke(errorMsg)  // Pass errorMsg to the onError lambda
                    }
                    loadingDialog.setLoading(false)
                }
            }
        }
    }
}

fun <T> Fragment.observeLivePaginatedDataState(
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




