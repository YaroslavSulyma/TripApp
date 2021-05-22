package com.example.tripapp.utils

import com.google.firebase.firestore.FirebaseFirestoreException

inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    }
    catch(e: Exception) {
        Resource.Error(e.localizedMessage ?: "An unknown error occured")
    }
}