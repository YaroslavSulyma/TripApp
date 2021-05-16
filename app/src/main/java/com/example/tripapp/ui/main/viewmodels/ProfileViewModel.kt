package com.example.tripapp.ui.main.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tripapp.data.entities.Post
import com.example.tripapp.data.entities.User
import com.example.tripapp.repositories.MainRepository
import com.example.tripapp.utils.Event
import com.example.tripapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : BasePostViewModel(repository, dispatcher) {
    private val _profileMeta = MutableLiveData<Event<Resource<User>>>()
    val profileMeta: LiveData<Event<Resource<User>>> = _profileMeta

    private val _followStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val followStatus: LiveData<Event<Resource<Boolean>>> = _followStatus

    private val _posts = MutableLiveData<Event<Resource<List<Post>>>>()
    override val posts: LiveData<Event<Resource<List<Post>>>>
        get() = _posts

    override fun getPosts(uid: String) {
        _posts.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getPostsForProfile(uid)
            _posts.postValue(Event(result))
        }
    }

    fun toggleFollowForUser(uid: String) {
        _followStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.toggleFollowForUser(uid)
            _followStatus.postValue(Event(result))
        }
    }

    fun loadProfile(uid: String) {
        _profileMeta.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getUser(uid)
            _profileMeta.postValue(Event(result))
        }
        getPosts(uid)
    }

}