package com.example.tripapp.repositories

import android.net.Uri
import com.example.tripapp.data.entities.Post
import com.example.tripapp.data.entities.User
import com.example.tripapp.utils.Resource

interface MainRepository {

    suspend fun createPost(
        imageUri: Uri,
        locationName: String,
        text: String,
        infoAboutCamping: String,
        totalPrice: String
    ): Resource<Any>

    suspend fun getPostForFollows(): Resource<List<Post>>

    suspend fun getUsers(uids: List<String>): Resource<List<User>>

    suspend fun getUser(uid: String): Resource<User>

    suspend fun toggleLikeForPost(post: Post): Resource<Boolean>
}