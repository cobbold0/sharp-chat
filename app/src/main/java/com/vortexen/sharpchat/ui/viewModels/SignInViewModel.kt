package com.vortexen.sharpchat.ui.viewModels

import androidx.lifecycle.MutableLiveData
import com.vortexen.sharpchat.data.DataState
import com.vortexen.sharpchat.data.constant.VerificationType
import com.vortexen.sharpchat.repository.AuthRepository
import com.vortexen.sharpchat.utils.BaseViewModel
import com.vortexen.sharpchat.utils.Event
import com.vortexen.sharpchat.utils.extensions.emitFlowResults
import com.vortexen.sharpchat.utils.extensions.emitFlowResultsToEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val supabaseClient: SupabaseClient
) : BaseViewModel() {

    private val _currentSession = MutableLiveData<Event<DataState<UserSession>>>()
    val currentSession: MutableLiveData<Event<DataState<UserSession>>> = _currentSession

    val userSession = supabaseClient.auth.currentSessionOrNull()

    private val _signInWithPhoneResult = MutableLiveData<DataState<Boolean>>()
    val signInWithPhoneResult: MutableLiveData<DataState<Boolean>> = _signInWithPhoneResult

    private val _signInWithPasswordResult = MutableLiveData<DataState<Boolean>>()
    val signInWithPasswordResult: MutableLiveData<DataState<Boolean>> = _signInWithPasswordResult

    private val _signInWithGoogleResult = MutableLiveData<DataState<Boolean>>()
    val signInWithGoogleResult: MutableLiveData<DataState<Boolean>> = _signInWithGoogleResult

    private val _signOutResult = MutableLiveData<DataState<Boolean>>()
    val signOutResult: MutableLiveData<DataState<Boolean>> = _signOutResult

    private val _otpToken = MutableStateFlow("")
    val otpToken = _otpToken

    private val _email = MutableStateFlow("")
    val email: Flow<String> = _email

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: Flow<String> = _phoneNumber

    private val _password = MutableStateFlow("")
    val password = _password

    fun onPhoneNumberChange(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onOtpTokenChange(otpToken: String) {
        _otpToken.value = otpToken
    }

    fun signInWithPassword() = emitFlowResults(_signInWithPasswordResult) {
        authRepository.signIn(_email.value, _password.value)
    }

    fun signInWithPhone() = emitFlowResults(_signInWithPhoneResult) {
        authRepository.signInWithPhone(_phoneNumber.value)
    }

    fun onGoogleSignIn() = emitFlowResults(_signInWithGoogleResult) {
        authRepository.signInWithGoogle()
    }

    fun signOut() = emitFlowResults(_signOutResult) {
        authRepository.signOut()
    }

    fun loadSession() = emitFlowResultsToEvent(_currentSession) {
        authRepository.loadCurrentSession()
    }

}