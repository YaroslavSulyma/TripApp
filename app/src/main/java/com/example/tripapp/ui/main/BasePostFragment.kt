package com.example.tripapp.ui.main

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.RequestManager
import com.example.tripapp.adapters.PostAdapter
import com.example.tripapp.ui.main.viewmodels.BasePostViewModel
import com.example.tripapp.ui.snackbar
import com.example.tripapp.utils.EventObserver
import javax.inject.Inject

abstract class BasePostFragment(
    layoutId: Int
) : Fragment(layoutId) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var postAdapter: PostAdapter

    protected abstract val postProgressBar: ProgressBar

    protected abstract val basePostViewModel: BasePostViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        basePostViewModel.posts.observe(
            viewLifecycleOwner, EventObserver(
                onError = {
                    postProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = {
                    postProgressBar.isVisible = false
                }
            ) { posts ->
                postProgressBar.isVisible = false
                postAdapter.posts = posts
            }
        )
    }
}