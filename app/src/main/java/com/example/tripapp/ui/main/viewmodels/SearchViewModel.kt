package com.example.tripapp.ui.main.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class SearchViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _searchResults = MutableLiveData<Event<Resource<List<User>>>>()
    private val searchResults: LiveData<Event<Resource<List<User>>>> = _searchResults

    fun search(query: String) {
        if (query.isEmpty()) return

        _searchResults.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.searchUser(query)
            _searchResults.postValue(Event(result))
        }
    }
}