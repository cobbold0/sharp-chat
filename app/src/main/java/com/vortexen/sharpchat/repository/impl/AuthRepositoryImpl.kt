package com.vortexen.sharpchat.repository.impl

import com.vortexen.sharpchat.data.DataState
import com.vortexen.sharpchat.data.remote.SignUpMetaData
import com.vortexen.sharpchat.repository.AuthRepository
import com.vortexen.sharpchat.services.ContactDiscoveryService
import com.vortexen.sharpchat.utils.JsonUtil
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: Auth
) : AuthRepository {
    override fun signIn(email: String, password: String): Flow<DataState<Boolean>> {
        return flow {
            emit(DataState.Loading)
            try {
                Timber.d("Sign in with email: $email and password: $password")
                auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                Timber.d("Sign in successful")
                emit(DataState.Success(true))
            } catch (e: Exception) {
                Timber.e(e)
                emit(DataState.Error(Exception(e.localizedMessage)))
            }
        }
    }

    override fun signInWithPhone(phoneNumber: String): Flow<DataState<Boolean>> {
        return flow {
            emit(DataState.Loading)
            try {
                auth.signInWith(OTP) {
                    phone = phoneNumber
                }
                emit(DataState.Success(true))
            } catch (e: Exception) {
                Timber.e(e)
                emit(DataState.Error(Exception(e.localizedMessage)))
            }

        }
    }

    override fun verifyPhone(phoneNumber: String, token: String): Flow<DataState<Boolean>> {
        return flow {
            emit(DataState.Loading)
            try {
                auth.verifyPhoneOtp(
                    type = OtpType.Phone.SMS, phone = phoneNumber, token = token
                )
                emit(DataState.Success(true))
            } catch (e: Exception) {
                Timber.e(e)
                emit(DataState.Error(Exception(e.localizedMessage)))
            }
        }
    }

    override fun verifyEmail(email: String, token: String): Flow<DataState<Boolean>> {
        return flow {
            emit(DataState.Loading)
            Timber.d("Verifying email: $email with token: $token")
            try {
                auth.verifyEmailOtp(
                    type = OtpType.Email.EMAIL, email = email, token = token
                )
                Timber.d("Email verification successful")
                emit(DataState.Success(true))
            } catch (e: Exception) {
                Timber.e(e)
                emit(DataState.Error(Exception(e.localizedMessage)))
            }
        }
    }

    override fun signUp(
        email: String,
        password: String,
        data: SignUpMetaData
    ): Flow<DataState<Boolean>> {
        return flow {
            emit(DataState.Loading)
            try {
                val jsonObject = JsonUtil.toJsonObject(data)

                Timber.d("Sign up JSON Object: $jsonObject")

                auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    this.data = jsonObject
                }

                Timber.d("Sign up successful")
                emit(DataState.Success(true))
            } catch (e: Exception) {
                Timber.e(e)
                emit(DataState.Error(Exception(e.localizedMessage)))
            }
        }
    }

    override fun signInWithGoogle(): Flow<DataState<Boolean>> {
        return flow {
            emit(DataState.Loading)
            try {
                auth.signInWith(Google)
                emit(DataState.Success(true))
            } catch (e: Exception) {
                emit(DataState.Error(Exception(e.localizedMessage)))
            }
        }
    }

    override fun signOut(): Flow<DataState<Boolean>> {
        return flow {
            emit(DataState.Loading)
            try {
                auth.signOut()
                emit(DataState.Success(true))
            } catch (e: Exception) {
                emit(DataState.Error(Exception(e.localizedMessage)))
            }
        }
    }

    override fun loadCurrentSession(): Flow<DataState<UserSession>> {
        return flow {
            emit(DataState.Loading)
            try {
                val session = auth.sessionManager.loadSession()
                if (session == null) {
                    emit(DataState.Error(Exception("No session found")))
                    return@flow
                }
                emit(DataState.Success(session))
            } catch (e: Exception) {
                emit(DataState.Error(Exception(e.localizedMessage)))
            }
        }
    }
}
