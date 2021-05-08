package com.example.tripapp.ui.main.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.tripapp.R
import com.example.tripapp.databinding.FragmentCreatePostBinding
import com.example.tripapp.ui.main.viewmodels.CreatePostViewModel
import com.example.tripapp.ui.snackbar
import com.example.tripapp.utils.EventObserver
import com.example.tripapp.utils.autoCleared
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreatePostFragment : Fragment(R.layout.fragment_create_post) {

    @Inject
    lateinit var glide: RequestManager

    private var binding: FragmentCreatePostBinding by autoCleared()

    private val viewModel: CreatePostViewModel by viewModels()

    private lateinit var cropContent: ActivityResultLauncher<String>

    private val cropActivityResultContract = object : ActivityResultContract<String, Uri?>() {
        override fun createIntent(context: Context, input: String?): Intent {
            return CropImage.activity()
                .setAspectRatio(16, 9)
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent).uri
        }
    }

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropContent = registerForActivityResult(cropActivityResultContract) {
            it?.let {
                viewModel.setCurrentImageUri(it)
            }
        }
    }

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
        binding.btnSetPostImage.setOnClickListener {
            cropContent.launch("image/*")
        }
        binding.ivPostImage.setOnClickListener {
            cropContent.launch("image/*")
        }
        binding.btnPost.setOnClickListener {
            currentImageUri?.let { uri ->
                viewModel.createPost(
                    uri,
                    binding.etLocationName.text.toString(),
                    binding.etPostText.toString(),
                    binding.etCampingInfo.toString(),
                    binding.etAmountSum.toString()
                )
            } ?: snackbar(getString(R.string.error_no_image_chosen))
        }
    }

    private fun subscribeToObservers() {
        viewModel.currentImageUri.observe(viewLifecycleOwner) {
            currentImageUri = it
            binding.btnSetPostImage.isVisible = false
            glide.load(currentImageUri).into(binding.ivPostImage)
        }

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