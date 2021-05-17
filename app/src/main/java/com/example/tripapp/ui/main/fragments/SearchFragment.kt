package com.example.tripapp.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tripapp.R
import com.example.tripapp.adapters.UserAdapter
import com.example.tripapp.databinding.FragmentSearchBinding
import com.example.tripapp.ui.main.viewmodels.SearchViewModel
import com.example.tripapp.ui.snackbar
import com.example.tripapp.utils.Constants.SEARCH_TIME_DELAY
import com.example.tripapp.utils.EventObserver
import com.example.tripapp.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    @Inject
    lateinit var userAdapter: UserAdapter

    private val viewModel: SearchViewModel by viewModels()

    private val binding by viewBinding(FragmentSearchBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        subscribeToObservers()

        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    viewModel.searchUser(it.toString())
                }
            }
        }

        userAdapter.setOnUserClickListener { user ->
            findNavController().navigate(
                SearchFragmentDirections.globalActionToOthersProfileFragment(
                    user.uid
                )
            )
        }
    }

    private fun setupRecyclerView() = binding.rvSearchResults.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = userAdapter
        itemAnimator = null
    }

    private fun subscribeToObservers() {
        viewModel.searchResults.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.searchProgressBar.isVisible = false
                snackbar(it)
            },
            onLoading = { binding.searchProgressBar.isVisible = true }
        ) { users ->
            binding.searchProgressBar.isVisible = false
            userAdapter.users = users
        })
    }
}