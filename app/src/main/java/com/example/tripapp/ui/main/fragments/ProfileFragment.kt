package com.example.tripapp.ui.main.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tripapp.R
import com.example.tripapp.databinding.FragmentProfileBinding
import com.example.tripapp.ui.main.BasePostFragment
import com.example.tripapp.ui.main.viewmodels.BasePostViewModel
import com.example.tripapp.ui.main.viewmodels.ProfileViewModel
import com.example.tripapp.ui.snackbar
import com.example.tripapp.utils.EventObserver
import com.example.tripapp.utils.viewBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
open class ProfileFragment : BasePostFragment(R.layout.fragment_profile) {

    private val binding by viewBinding(FragmentProfileBinding::bind)

    override val basePostViewModel: BasePostViewModel
        get() {
            val vm: ProfileViewModel by viewModels()
            return vm
        }

    protected val viewModel: ProfileViewModel
        get() = basePostViewModel as ProfileViewModel

    protected open val uid: String
        get() = FirebaseAuth.getInstance().uid!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        subscribeToObservers()

        binding.btnToggleFollow.isVisible = false
        viewModel.loadProfile(uid)

        lifecycleScope.launch {
            viewModel.getPagingFlow(uid).collect {
                postAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            postAdapter.loadStateFlow.collectLatest {
                binding.profilePostsProgressBar?.isVisible = it.refresh is LoadState.Loading ||
                        it.append is LoadState.Loading
            }
        }
    }

    private fun setupRecyclerView() = binding.rvPosts.apply {
        adapter = postAdapter
        itemAnimator = null
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObservers() {
        viewModel.profileMeta.observe(
            viewLifecycleOwner, EventObserver(
                onError = {
                    binding.profileMetaProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = {
                    binding.profileMetaProgressBar.isVisible = true
                }
            ) { user ->
                binding.profileMetaProgressBar.isVisible = false
                binding.tvUsername.text = user.username
                binding.tvProfileDescription.text = if (user.description.isEmpty()) {
                    requireContext().getString(R.string.no_description)
                } else user.description
                glide.load(user.profilePictureUrl).into(binding.ivProfileImage)
            }
        )
        basePostViewModel.deletePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                snackbar(it)
            }
        ) { deletedPost ->
            postAdapter.refresh()
        })
    }
}