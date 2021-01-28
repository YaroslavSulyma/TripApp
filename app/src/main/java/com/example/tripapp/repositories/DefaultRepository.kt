package com.example.tripapp.repositories

import com.example.tripapp.utils.Resource
import com.google.firebase.auth.AuthResult

class DefaultRepository : AuthRepository {
    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): Resource<AuthResult> {
        TODO("Not yet implemented")
    }

    override suspend fun login(email: String, password: String): Resource<AuthResult> {
        TODO("Not yet implemented")
    }
}