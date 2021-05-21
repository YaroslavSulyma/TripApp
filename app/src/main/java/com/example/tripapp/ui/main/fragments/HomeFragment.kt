package com.example.tripapp.ui.main.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tripapp.R
import com.example.tripapp.databinding.FragmentHomeBinding
import com.example.tripapp.ui.main.BasePostFragment
import com.example.tripapp.ui.main.viewmodels.BasePostViewModel
import com.example.tripapp.ui.main.viewmodels.HomeViewModel
import com.example.tripapp.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BasePostFragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    override val basePostViewModel: BasePostViewModel
        get() {
            val vm: HomeViewModel by viewModels()
            return vm
        }

    private val viewModel: HomeViewModel by lazy { basePostViewModel as HomeViewModel }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        lifecycleScope.launch {
            viewModel.pagingFlow.collect {
                postAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            postAdapter.loadStateFlow.collectLatest {
                binding.allPostsProgressBar?.isVisible = it.refresh is LoadState.Loading ||
                        it.append is LoadState.Loading
            }
        }
    }

    private fun setupRecyclerView() = binding.rvAllPosts.apply {
        adapter = postAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }
}