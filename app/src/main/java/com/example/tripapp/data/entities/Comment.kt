package com.example.tripapp.data.entities

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Comment constructor(
    val commentId: String = "",
    val postId: String = "",
    val uid: String = "",
    @get:Exclude
    var username: String = "",
    @get:Exclude
    var profilePictureUrl: String = "",
    val comment: String = "",
    val date: Long = 0L
)