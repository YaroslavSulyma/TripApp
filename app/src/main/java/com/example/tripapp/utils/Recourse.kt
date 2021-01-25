package com.example.tripapp.utils

sealed class Recourse<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Recourse<T>(data)
    class Loading<T>(data: T) : Recourse<T>(data)
    class Error<T>(data: T? = null) : Recourse<T>(data)
}