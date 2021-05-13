package com.example.tripapp.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tripapp.R
import com.example.tripapp.databinding.FragmentHomeBinding
import com.example.tripapp.ui.main.BasePostFragment
import com.example.tripapp.ui.main.viewmodels.BasePostViewModel
import com.example.tripapp.ui.main.viewmodels.HomeViewModel
import com.example.tripapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BasePostFragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override val postProgressBar: ProgressBar
        get() = binding.allPostsProgressBar
    override val basePostViewModel: BasePostViewModel
        get() {
            val vm: HomeViewModel by viewModels()
            return vm
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() = binding.rvAllPosts.apply {
        adapter = postAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }
}