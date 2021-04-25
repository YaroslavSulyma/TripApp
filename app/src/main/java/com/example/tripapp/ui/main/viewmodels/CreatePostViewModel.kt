package com.example.tripapp.ui.main.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.R
import com.example.tripapp.data.entities.Coordinates
import com.example.tripapp.repositories.MainRepository
import com.example.tripapp.utils.Event
import com.example.tripapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val repository: MainRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _createPostStatus = MutableLiveData<Event<Resource<Any>>>()
    val createPostStatus: LiveData<Event<Resource<Any>>> = _createPostStatus

    fun createPost(
        imageUri: Uri,
        locationName: String,
        text: String,
        infoAboutCamping: String,
        coordinates: Coordinates?,
        totalPrice: String
    ) {
        if (text.isEmpty() || locationName.isEmpty() || infoAboutCamping.isEmpty() || totalPrice.isEmpty()) {
            val error = applicationContext.getString(R.string.error_input_empty)
            _createPostStatus.postValue(Event(Resource.Error(error)))
        } else {
            _createPostStatus.postValue(Event(Resource.Loading()))
            viewModelScope.launch(dispatcher) {
                val result = repository.createPost(
                    imageUri,
                    locationName,
                    text,
                    infoAboutCamping,
                    coordinates,
                    totalPrice
                )
                _createPostStatus.postValue(Event(result))
            }
        }
    }
}