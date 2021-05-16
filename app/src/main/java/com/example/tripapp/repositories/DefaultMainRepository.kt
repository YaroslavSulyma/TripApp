package com.example.tripapp.repositories

import android.net.Uri
import com.example.tripapp.data.entities.Post
import com.example.tripapp.data.entities.User
import com.example.tripapp.utils.Resource
import com.example.tripapp.utils.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.IllegalStateException

@ActivityScoped
class DefaultMainRepository : MainRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage
    private val users = firestore.collection("users")
    private val posts = firestore.collection("posts")
    private val comments = firestore.collection("comments")

    override suspend fun createPost(
        imageUri: Uri,
        locationName: String,
        text: String,
        infoAboutCamping: String,
        totalPrice: String,
    ) = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.uid!!
            val postId = UUID.randomUUID().toString()
            val imageUploadResult = storage.getReference(postId).putFile(imageUri).await()
            val imageUrl = imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()
            val post = Post(
                id = postId,
                authorUid = uid,
                locationName = locationName,
                text = text,
                infoAboutCamping = infoAboutCamping,
                totalPrice = totalPrice,
                imageUrl = imageUrl,
                date = System.currentTimeMillis()
            )
            posts.document(postId).set(post).await()
            Resource.Success(Any())
        }
    }

    override suspend fun getPostForFollows() = withContext(Dispatchers.IO) {
        safeCall {
            val uid = FirebaseAuth.getInstance().uid!!
            val follows = getUser(uid).data!!.follows
            val allPosts =
                posts.whereIn("authorUid", follows).orderBy("date", Query.Direction.DESCENDING)
                    .get().await().toObjects(Post::class.java).onEach { post ->
                        val user = getUser(post.authorUid).data!!
                        post.authorProfilePictureUrl = user.profilePictureUrl
                        post.authorUsername = user.username
                        post.isLiked = uid in post.likedBy
                    }
            Resource.Success(allPosts)
        }
    }

    override suspend fun deletePost(post: Post) = withContext(Dispatchers.IO) {
        safeCall {
            posts.document(post.id).delete().await()
            storage.getReferenceFromUrl(post.imageUrl).delete().await()
            Resource.Success(post)
        }
    }

    override suspend fun getPostsForProfile(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val profilePost = posts.whereEqualTo("authorUid", uid)
                .orderBy("data", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Post::class.java)
                .onEach { post ->
                    val user = getUser(post.authorUid).data!!
                    post.authorProfilePictureUrl = user.profilePictureUrl
                    post.authorUsername = user.username
                    post.isLiked = uid in post.likedBy
                }
            Resource.Success(profilePost)
        }
    }

    override suspend fun getUsers(uids: List<String>) = withContext(Dispatchers.IO) {
        safeCall {
            val usersList = users.whereIn("uid", uids).orderBy("username").get().await().toObjects(
                User::class.java
            )
            Resource.Success(usersList)
        }
    }

    override suspend fun toggleFollowForUser(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            var isFollowing = false
            firestore.runTransaction { transaction ->
                val currentUid = auth.uid!!
                val currentUser =
                    transaction.get(users.document(currentUid)).toObject(User::class.java)!!
                isFollowing = uid in currentUser.follows
                val newFollows =
                    if (isFollowing) currentUser.follows - uid else currentUser.follows + uid
                transaction.update(users.document(currentUid), "follows", newFollows)
            }.await()
            Resource.Success(!isFollowing)
        }
    }

    override suspend fun toggleLikeForPost(post: Post) = withContext(Dispatchers.IO) {
        safeCall {
            var isLiked = false
            firestore.runTransaction { transaction ->
                val uid = FirebaseAuth.getInstance().uid!!
                val postResult = transaction.get(posts.document(post.id))
                val currentLikes = postResult.toObject(Post::class.java)?.likedBy ?: listOf()
                transaction.update(
                    posts.document(post.id),
                    "likedBy",
                    if (uid in currentLikes) currentLikes - uid else {
                        isLiked = true
                        currentLikes + uid
                    }
                )
            }.await()
            Resource.Success(isLiked)
        }
    }

    override suspend fun getUser(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val user = users.document("uid").get().await().toObject(User::class.java)
                ?: throw IllegalStateException()
            val currentUid = FirebaseAuth.getInstance().uid!!
            val currentUser = users.document(currentUid).get().await().toObject(User::class.java)
                ?: throw IllegalStateException()
            user.isFollowing = uid in currentUser.follows
            Resource.Success(user)
        }
    }

    override suspend fun searchUser(query: String) = withContext(Dispatchers.IO) {
        safeCall {
            val userResults =
                users.whereGreaterThanOrEqualTo("username", query.toUpperCase(Locale.ROOT)).get()
                    .await().toObjects(User::class.java)
            Resource.Success(userResults)
        }
    }
}