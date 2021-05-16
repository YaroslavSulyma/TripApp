package com.example.tripapp.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tripapp.R
import com.example.tripapp.databinding.FragmentProfileBinding
import com.example.tripapp.ui.main.BasePostFragment
import com.example.tripapp.ui.main.viewmodels.BasePostViewModel
import com.example.tripapp.ui.main.viewmodels.ProfileViewModel
import com.example.tripapp.ui.snackbar
import com.example.tripapp.utils.EventObserver
import com.example.tripapp.utils.autoCleared
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class ProfileFragment : BasePostFragment(R.layout.fragment_profile) {

    private var binding: FragmentProfileBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override val postProgressBar: ProgressBar
        get() = binding.profilePostsProgressBar
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
    }
}