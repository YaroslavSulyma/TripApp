package com.example.tripapp.ui.main.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.utils.Resource
import com.example.tripapp.data.entities.Comment
import com.example.tripapp.repositories.MainRepository
import com.example.tripapp.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    private val _createCommentStatus = MutableLiveData<Event<Resource<Comment>>>()
    val createCommentStatus: LiveData<Event<Resource<Comment>>> = _createCommentStatus

    private val _deleteCommentStatus = MutableLiveData<Event<Resource<Comment>>>()
    val deleteCommentStatus: LiveData<Event<Resource<Comment>>> = _deleteCommentStatus

    private val _commentForPost = MutableLiveData<Event<Resource<List<Comment>>>>()
    val commentForPost: LiveData<Event<Resource<List<Comment>>>> = _commentForPost

    fun createComment(commentText: String, postId: String) {
        if (commentText.isEmpty()) return
        _createCommentStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.createComment(commentText, postId)
            _createCommentStatus.postValue(Event(result))
        }
    }

    fun deleteComment(comment: Comment) {
        _deleteCommentStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.deleteComment(comment)
            _deleteCommentStatus.postValue(Event(result))
        }
    }

    fun getCommentsForPost(postId: String) {
        _commentForPost.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getCommentsForPost(postId)
            _commentForPost.postValue(Event(result))
        }
    }
}