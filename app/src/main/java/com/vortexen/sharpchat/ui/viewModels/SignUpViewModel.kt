package com.vortexen.sharpchat.ui.viewModels

import androidx.lifecycle.MutableLiveData
import com.vortexen.sharpchat.data.DataState
import com.vortexen.sharpchat.data.constant.VerificationType
import com.vortexen.sharpchat.data.remote.SignUpMetaData
import com.vortexen.sharpchat.repository.AuthRepository
import com.vortexen.sharpchat.utils.BaseViewModel
import com.vortexen.sharpchat.utils.extensions.emitFlowResults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _signUpWithPasswordResult = MutableLiveData<DataState<Boolean>>()
    val signUpWithPasswordResult: MutableLiveData<DataState<Boolean>> = _signUpWithPasswordResult

    private val _signInWithGoogleResult = MutableLiveData<DataState<Boolean>>()
    val signInWithGoogleResult: MutableLiveData<DataState<Boolean>> = _signInWithGoogleResult

    private val _verifyPhoneResult = MutableLiveData<DataState<Boolean>>()
    val verifyPhoneResult: MutableLiveData<DataState<Boolean>> = _verifyPhoneResult

    private val _verifyEmailResult = MutableLiveData<DataState<Boolean>>()
    val verifyEmailResult: MutableLiveData<DataState<Boolean>> = _verifyEmailResult

    private val _name = MutableStateFlow("")
    val name: Flow<String> = _name

    fun onNameChange(name: String) {
        _name.value = name
    }

    private val _email = MutableStateFlow("")
    val email: Flow<String> = _email

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: Flow<String> = _phoneNumber

    private val _password = MutableStateFlow("")
    val password = _password

    private val _otpToken = MutableStateFlow("")
    val otpToken = _otpToken

    fun onPhoneNumberChange(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }

    fun onOtpTokenChange(otpToken: String) {
        _otpToken.value = otpToken
    }

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onSignUp() = emitFlowResults(_signUpWithPasswordResult) {
        authRepository.signUp(
            email = _email.value,
            password = _password.value,
            data = SignUpMetaData(phone = _phoneNumber.value, full_name = _name.value)
        )
    }

    fun onGoogleSignIn() = emitFlowResults(_signInWithGoogleResult) {
        authRepository.signInWithGoogle()
    }


    private fun verifyPhone() = emitFlowResults(_verifyPhoneResult) {
        authRepository.verifyPhone(_phoneNumber.value, _otpToken.value)
    }

    private fun verifyEmail() = emitFlowResults(_verifyEmailResult) {
        authRepository.verifyEmail(_email.value, _otpToken.value)
    }

    fun verifyCode(email: VerificationType) {
        when (email) {
            VerificationType.EMAIL -> verifyEmail()
            VerificationType.PHONE -> verifyPhone()
        }
    }
}