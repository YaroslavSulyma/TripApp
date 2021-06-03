package com.example.tripapp.ui.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.data.entities.Post
import com.example.tripapp.repositories.MainRepository
import com.example.tripapp.utils.Event
import com.example.tripapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    private val _getPost = MutableLiveData<Event<Resource<Post>>>()
    val getPost: MutableLiveData<Event<Resource<Post>>> = _getPost


    fun getPost(uid: String) {
        _getPost.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getPost(uid)
            _getPost.postValue(Event(result))
        }
    }
}