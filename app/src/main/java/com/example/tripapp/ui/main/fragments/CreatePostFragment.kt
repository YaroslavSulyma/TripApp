package com.example.tripapp.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tripapp.R
import com.example.tripapp.databinding.FragmentCreatePostBinding
import com.example.tripapp.ui.main.viewmodels.CreatePostViewModel
import com.example.tripapp.ui.snackbar
import com.example.tripapp.utils.EventObserver
import com.example.tripapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePostFragment : Fragment(R.layout.fragment_create_post) {

    private var binding: FragmentCreatePostBinding by autoCleared()

    private val viewModel: CreatePostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePostBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.createPostStatus.observe(
            viewLifecycleOwner,
            EventObserver(
                onError = {
                    binding.createPostProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = { binding.createPostProgressBar.isVisible = true }
            ) {
                binding.createPostProgressBar.isVisible = false
                findNavController().popBackStack()
            }
        )
    }
}