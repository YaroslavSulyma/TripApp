package com.example.tripapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.tripapp.R
import com.example.tripapp.data.entities.Post
import com.example.tripapp.databinding.ItemPostBinding
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text
import javax.inject.Inject

class PostAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val ivPostImage: ImageView = binding.ivPostImage
        val ivAuthorProfileImage: ImageView = binding.ivAuthorProfileImage
        val tvPostAuthor: TextView = binding.tvPostAuthor
        val tvPostText: TextView = binding.tvPostText
        val tvLikedBy: TextView = binding.tvLikedBy
        val ibLike: ImageButton = binding.ibLike
        val ibComments: ImageButton = binding.ibComments
        val ibDeletePost: ImageButton = binding.ibDeletePost
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Post>() {

        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var posts: List<Post>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.apply {
            glide.load(post.imageUrl).into(ivPostImage)
            glide.load(post.authorProfilePictureUrl).into(ivAuthorProfileImage)
            tvPostAuthor.text = post.authorUsername
            tvPostText.text = post.locationName
            val likeCount = post.likedBy.size
            tvLikedBy.text = when {
                likeCount <= 0 -> "No Likes"
                likeCount <= 1 -> "Liked by 1 person"
                else -> "Liked by $likeCount people"
            }
            val uid = FirebaseAuth.getInstance().uid!!
            ibDeletePost.isVisible = uid == post.authorUid
            ibLike.setImageResource(
                if (post.isLiked) {
                    R.drawable.ic_like
                } else R.drawable.ic_like_white
            )
            tvPostAuthor.setOnClickListener {
                onUserClickListener?.let { click ->
                    click(post.authorUid)
                }
            }
            ivAuthorProfileImage.setOnClickListener {
                onUserClickListener?.let { click ->
                    click(post.authorUid)
                }
            }
            tvLikedBy.setOnClickListener {
                onLikedByClickListener?.let { click ->
                    click(post)
                }
            }
            ibLike.setOnClickListener {
                onLikeClickListener?.let { click ->
                    if (!post.isLiking) click(post, holder.layoutPosition)
                }
            }
            ibComments.setOnClickListener {
                onCommentsClickListener?.let { click ->
                    click(post)
                }
            }
            ibDeletePost.setOnClickListener {
                onDeletePostClickListener?.let { click ->
                    click(post)
                }
            }
        }
    }

    private var onLikeClickListener: ((Post, Int) -> Unit)? = null
    private var onUserClickListener: ((String) -> Unit)? = null
    private var onCommentsClickListener: ((Post) -> Unit)? = null
    private var onLikedByClickListener: ((Post) -> Unit)? = null
    private var onDeletePostClickListener: ((Post) -> Unit)? = null

    fun setOnLikeClickListener(listener: (Post, Int) -> Unit) {
        onLikeClickListener = listener
    }

    fun setOnUserClickListener(listener: (String) -> Unit) {
        onUserClickListener = listener
    }

    fun setOnDeletePostClickListener(listener: (Post) -> Unit) {
        onDeletePostClickListener = listener
    }

    fun setOnLikedByClickListener(listener: (Post) -> Unit) {
        onLikedByClickListener = listener
    }

    fun setOnCommentsClickListener(listener: (Post) -> Unit) {
        onCommentsClickListener = listener
    }
}