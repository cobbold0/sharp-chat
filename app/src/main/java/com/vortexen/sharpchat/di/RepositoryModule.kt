package com.vortexen.sharpchat.di

import android.app.Application
import com.vortexen.sharpchat.repository.AuthRepository
import com.vortexen.sharpchat.repository.ContactRepository
import com.vortexen.sharpchat.repository.impl.AuthRepositoryImpl
import com.vortexen.sharpchat.repository.impl.ContactRepositoryImpl
import com.vortexen.sharpchat.services.ContactDiscoveryService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.auth.Auth
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(auth: Auth): AuthRepository {
        return AuthRepositoryImpl(auth)
    }

    @Provides
    @Singleton
    fun provideContactRepository(
        application: Application,
        contactDiscoveryService: ContactDiscoveryService
    ): ContactRepository {
        return ContactRepositoryImpl(application, contactDiscoveryService)
    }

}