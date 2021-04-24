package com.example.tripapp.data.entities

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
    val id: String = "",
    var authorUid: String = "",
    @Exclude var authorUsername: String = "",
    @Exclude var authorProfilePictureUrl: String = "",
    val locationName: String = "",
    val text: String = "",
    val infoAboutCamping: String = "",
    val coordinates: Coordinates?,
    val totalPrice: String = "0$",
    val imageUrl: String = "",
    val date: Long = 0L,
    @Exclude var liked: Boolean = false,
    @Exclude val isLiking: Boolean = false,
    val likedBy: List<String> = listOf()
)


