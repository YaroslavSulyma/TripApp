package com.example.tripapp.ui.main.viewmodels

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.tripapp.data.pagingsource.FollowPostPagingSource
import com.example.tripapp.repositories.MainRepository
import com.example.tripapp.utils.Constants.PAGE_SIZE
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : BasePostViewModel(repository, dispatcher) {
    val pagingFlow = Pager(PagingConfig(PAGE_SIZE)){
        FollowPostPagingSource(FirebaseFirestore.getInstance())
    }.flow.cachedIn(viewModelScope)
}