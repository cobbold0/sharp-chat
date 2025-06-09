package com.vortexen.sharpchat.di

import android.app.Application
import com.vortexen.sharpchat.services.ContactDiscoveryService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    @Provides
    @Singleton
    fun provideContactDiscoveryService(
        supabase: SupabaseClient, application: Application
    ): ContactDiscoveryService {
        return ContactDiscoveryService(supabase, application)
    }
}