package com.example.tripapp.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.tripapp.R
import com.example.tripapp.databinding.FragmentCameraBinding
import com.example.tripapp.utils.viewBinding

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private val binding by viewBinding(FragmentCameraBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}