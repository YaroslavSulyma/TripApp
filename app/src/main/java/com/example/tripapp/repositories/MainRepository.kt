package com.example.tripapp.repositories

import android.net.Uri
import com.example.tripapp.utils.Resource

interface MainRepository {

    suspend fun createPost(
        imageUri: Uri,
        locationName: String,
        text: String,
        infoAboutCamping: String,
        totalPrice: String
    ): Resource<Any>

}