package com.example.tripapp.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.tripapp.R
import com.example.tripapp.databinding.FragmentSettingsBinding
import com.example.tripapp.ui.slideUpViews
import com.example.tripapp.utils.viewBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        slideUpViews(
            requireContext(),
            binding.materialTextView,
            binding.ivProfileImage,
            binding.titleUsername,
            binding.titleDescription,
            binding.btnUpdateProfile
        )
    }
}