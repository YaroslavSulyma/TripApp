package com.example.tripapp.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tripapp.R
import com.example.tripapp.databinding.FragmentCreatePostBinding
import com.example.tripapp.utils.autoCleared

class CreatePostFragment : Fragment(R.layout.fragment_create_post) {

    private var binding: FragmentCreatePostBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePostBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}