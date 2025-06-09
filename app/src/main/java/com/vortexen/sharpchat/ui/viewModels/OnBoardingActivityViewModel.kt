package com.vortexen.sharpchat.ui.viewModels

import androidx.lifecycle.MutableLiveData
import com.vortexen.sharpchat.data.DataState
import com.vortexen.sharpchat.repository.AuthRepository
import com.vortexen.sharpchat.utils.BaseViewModel
import com.vortexen.sharpchat.utils.Event
import com.vortexen.sharpchat.utils.extensions.emitFlowResultsToEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.user.UserSession
import javax.inject.Inject

@HiltViewModel
class OnBoardingActivityViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _currentSession = MutableLiveData<Event<DataState<UserSession>>>()
    val currentSession: MutableLiveData<Event<DataState<UserSession>>> = _currentSession

    fun loadSession() = emitFlowResultsToEvent(_currentSession) {
        authRepository.loadCurrentSession()
    }
}