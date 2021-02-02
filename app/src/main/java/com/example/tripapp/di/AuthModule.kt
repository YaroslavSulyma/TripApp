package com.example.tripapp.di

import com.example.tripapp.repositories.AuthRepository
import com.example.tripapp.repositories.DefaultAuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class AuthModule{

    @Binds
    abstract fun bindAuthRepository(impl: DefaultAuthRepository): AuthRepository
}