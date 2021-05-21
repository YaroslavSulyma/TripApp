package com.example.tripapp.data.pagingsource

import androidx.paging.PagingSource
import com.example.tripapp.data.entities.Post
import com.example.tripapp.data.entities.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class ProfilePostPagingSource(
    private val db: FirebaseFirestore,
    private val uid: String
) : PagingSource<QuerySnapshot, Post>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Post> {
        return try {
            val curPage = params.key ?: db.collection("posts")
                .whereEqualTo("authorUid", uid)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val lastDocumentSnapshot = curPage.documents[curPage.size() - 1]

            val nextPage = params.key ?: db.collection("posts")
                .whereEqualTo("authorUid", uid)
                .orderBy("date", Query.Direction.DESCENDING)
                .startAfter(lastDocumentSnapshot)
                .get()
                .await()

            LoadResult.Page(
                curPage.toObjects(Post::class.java)
                    .onEach { post ->
                        val user = db.collection("users").document(uid).get().await()
                            .toObject(User::class.java)!!
                        post.authorProfilePictureUrl = user.profilePictureUrl
                        post.authorUsername = user.username
                        post.isLiked = uid in post.likedBy
                    }, null,
                nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}