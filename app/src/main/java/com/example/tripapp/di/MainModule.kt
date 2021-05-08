package com.example.tripapp.di

import com.example.tripapp.repositories.DefaultMainRepository
import com.example.tripapp.repositories.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object MainModule {

    @ViewModelScoped
    @Provides
    fun provideMainRepository() = DefaultMainRepository() as MainRepository
}