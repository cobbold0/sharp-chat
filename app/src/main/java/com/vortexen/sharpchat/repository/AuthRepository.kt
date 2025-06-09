package com.vortexen.sharpchat.repository

import com.vortexen.sharpchat.data.DataState
import com.vortexen.sharpchat.data.remote.SignUpMetaData
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signIn(email: String, password: String): Flow<DataState<Boolean>>
    fun signInWithPhone(phoneNumber: String): Flow<DataState<Boolean>>
    fun verifyPhone( phoneNumber: String, token: String): Flow<DataState<Boolean>>
    fun verifyEmail(email: String, token: String): Flow<DataState<Boolean>>
    fun signUp(email: String, password: String, data: SignUpMetaData): Flow<DataState<Boolean>>
    fun signInWithGoogle(): Flow<DataState<Boolean>>
    fun signOut(): Flow<DataState<Boolean>>
    fun loadCurrentSession(): Flow<DataState<UserSession>>
}