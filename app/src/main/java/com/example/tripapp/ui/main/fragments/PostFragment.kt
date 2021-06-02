package com.example.tripapp.ui.main.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.example.tripapp.R
import com.example.tripapp.databinding.FragmentPostBinding
import com.example.tripapp.databinding.PostUserItemBinding
import com.example.tripapp.ui.main.viewmodels.PostViewModel
import com.example.tripapp.ui.main.viewmodels.ProfileViewModel
import com.example.tripapp.ui.snackbar
import com.example.tripapp.utils.EventObserver
import com.example.tripapp.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import javax.inject.Inject

@AndroidEntryPoint
class PostFragment : Fragment(R.layout.fragment_post) {

    @Inject
    lateinit var glide: RequestManager

    private val viewModelPost: PostViewModel by viewModels()
    private val viewModelProfile: ProfileViewModel by viewModels()

    private val args: PostFragmentArgs by navArgs()

    private val binding by viewBinding(FragmentPostBinding::bind)
    private val includedBinding by viewBinding(PostUserItemBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()
        viewModelPost.getPost(args.postId)
        viewModelProfile.loadProfile(args.authorId)
    }

    private fun subscribeToObservers() {
        viewModelPost.getPost.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.progressBar.isVisible = false
                snackbar(it)
            },
            onLoading = { binding.progressBar.isVisible = true }
        ) { post ->
            binding.progressBar.isVisible = false
            glide.load(post.imageUrl).into(binding.ivImage)
            binding.tvUserExperience.text = post.text
            binding.tvCampingInfo.text = post.infoAboutCamping
            val formatter = SimpleDateFormat("dd.MM.yyyy");
            val date = formatter.format(post.date);
            includedBinding.tvDate.text = date
            includedBinding.tvPostText.text = post.locationName
            binding.tvMoney.text = post.totalPrice
        })
        viewModelProfile.profileMeta.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.progressBar.isVisible = false
                snackbar(it)
            },
            onLoading = { binding.progressBar.isVisible = true }
        ) { user ->
            binding.progressBar.isVisible = false
            glide.load(user.profilePictureUrl).into(includedBinding.ivAuthorProfileImage)
            includedBinding.tvUsername.text = user.username
        })
    }
}