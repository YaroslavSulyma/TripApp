package com.example.tripapp.repositories

import android.net.Uri
import com.example.tripapp.data.entities.Comment
import com.example.tripapp.data.entities.Post
import com.example.tripapp.data.entities.ProfileUpdate
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

    suspend fun getUsers(uids: List<String>): Resource<List<User>>

    suspend fun getUser(uid: String): Resource<User>

    suspend fun getPostsForFollows(): Resource<List<Post>>

    suspend fun toggleLikeForPost(post: Post): Resource<Boolean>

    suspend fun deletePost(post: Post): Resource<Post>

    suspend fun getPostsForProfile(uid: String): Resource<List<Post>>

    suspend fun toggleFollowForUser(uid: String): Resource<Boolean>

    suspend fun searchUser(query: String): Resource<List<User>>

    suspend fun createComment(commentText: String, postId: String): Resource<Comment>

    suspend fun deleteComment(comment: Comment): Resource<Comment>

    suspend fun getCommentForPost(postId: String): Resource<List<Comment>>

    suspend fun updateProfile(profileUpdate: ProfileUpdate): Resource<Any>
    suspend fun updateProfilePicture(uid: String, imageUri: Uri): Uri?
}