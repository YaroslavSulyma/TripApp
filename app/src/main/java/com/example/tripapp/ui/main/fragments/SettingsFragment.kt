package com.example.tripapp.ui.main.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.bumptech.glide.RequestManager
import com.example.tripapp.R
import com.example.tripapp.data.entities.ProfileUpdate
import com.example.tripapp.databinding.FragmentSettingsBinding
import com.example.tripapp.ui.main.viewmodels.SettingsViewModel
import com.example.tripapp.ui.slideUpViews
import com.example.tripapp.ui.snackbar
import com.example.tripapp.utils.EventObserver
import com.example.tripapp.utils.viewBinding
import com.google.firebase.auth.FirebaseAuth
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    @Inject
    lateinit var glide: RequestManager

    private val viewModel: SettingsViewModel by viewModels()

    private var curImageUri: Uri? = null

    private lateinit var cropContent: ActivityResultLauncher<Any?>

    private val binding by viewBinding(FragmentSettingsBinding::bind)

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropContent = registerForActivityResult(cropActivityResultContract) { uri ->
            uri?.let {
                viewModel.setCurrentImageUri(it)
                binding.btnUpdateProfile.isEnabled = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObserver()
        val uid = FirebaseAuth.getInstance().uid!!
        viewModel.getUser(uid)
        binding.btnUpdateProfile.isEnabled = false
        binding.etUsername.addTextChangedListener {
            binding.btnUpdateProfile.isEnabled = true
        }
        binding.etDescription.addTextChangedListener {
            binding.btnUpdateProfile.isEnabled = true
        }

        binding.ivProfileImage.setOnClickListener {
            cropContent.launch(null)
        }

        binding.btnUpdateProfile.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val description = binding.etDescription.text.toString()
            val profileUpdate = ProfileUpdate(uid, username, description, curImageUri)
            viewModel.updateProfile(profileUpdate)
        }

        slideUpViews(
            requireContext(),
            binding.materialTextView,
            binding.ivProfileImage,
            binding.titleUsername,
            binding.titleDescription,
            binding.btnUpdateProfile
        )
    }

    private fun subscribeToObserver() {
        viewModel.getUserStatus.observe(
            viewLifecycleOwner, EventObserver(
                onError = {
                    binding.settingsProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = { binding.settingsProgressBar.isVisible = true },
            ) { user ->
                binding.settingsProgressBar.isVisible = false
                glide.load(user.profilePictureUrl).into(binding.ivProfileImage)
                binding.etUsername.setText(user.username)
                binding.etDescription.setText(user.description)
                binding.btnUpdateProfile.isEnabled = false
            }
        )
        viewModel.curImageUri.observe(viewLifecycleOwner) { uri ->
            curImageUri = uri
            glide.load(uri).into(binding.ivProfileImage)
        }
        viewModel.updateProfileStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.settingsProgressBar.isVisible = false
                snackbar(it)
                binding.btnUpdateProfile.isEnabled = true
            },
            onLoading = {
                binding.settingsProgressBar.isVisible = true
                binding.btnUpdateProfile.isEnabled = false
            }
        ) {
            binding.settingsProgressBar.isVisible = false
            binding.btnUpdateProfile.isEnabled = false
            snackbar(requireContext().getString(R.string.profile_updated))
        })
    }
}