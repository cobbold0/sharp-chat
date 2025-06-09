package com.vortexen.sharpchat.utils.extensions

import androidx.lifecycle.MutableLiveData
import com.vortexen.sharpchat.data.DataState
import com.vortexen.sharpchat.data.PaginatedDataState
import com.vortexen.sharpchat.utils.BaseViewModel
import com.vortexen.sharpchat.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

fun <T> BaseViewModel.emitFlowResults(
    liveDataObject: MutableLiveData<DataState<T>>,
    networkRequest: () -> Flow<DataState<T>>
) {
    coroutineScope.launch(Dispatchers.IO) {
        networkRequest()
            .onStart { liveDataObject.postValue(DataState.Loading) }
            .onEach {
                liveDataObject.postValue(it)
            }
            .catch {
                liveDataObject.postValue(DataState.Error(Exception(it.localizedMessage)))
            }
            .launchIn(this)

    }
}


fun <T> BaseViewModel.emitFlowResultsToEvent(
    liveDataObject: MutableLiveData<Event<DataState<T>>>,
    networkRequest: () -> Flow<DataState<T>>
) {
    coroutineScope.launch(Dispatchers.IO) {
        networkRequest()
            .onStart { liveDataObject.postValue(Event(DataState.Loading)) }
            .onEach {
                liveDataObject.postValue(Event(it))
            }
            .catch { throwable ->
                // Post the error with a meaningful message
                val errorMessage = throwable.localizedMessage ?: "Something went wrong"
                liveDataObject.postValue(Event(DataState.Error(Exception(errorMessage))))
            }
            .launchIn(this)
    }
}