package com.example.tripapp.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tripapp.R
import com.example.tripapp.databinding.FragmentLoginBinding
import com.example.tripapp.ui.auth.AuthViewModel
import com.example.tripapp.ui.main.MainActivity
import com.example.tripapp.ui.snackbar
import com.example.tripapp.utils.EventObserver
import com.example.tripapp.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)
    private lateinit var viewModel: AuthViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        subscribeToObservers()

        binding.btnLogin.setOnClickListener {
            viewModel.login(binding.etEmail.text.toString(), binding.etPassword.text.toString())
        }

        binding.tvRegisterNewAccount.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

    }

    private fun subscribeToObservers() {
        viewModel.loginStatus.observe(
            viewLifecycleOwner, EventObserver(
                onError = {
                    binding.loginProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = {
                    binding.loginProgressBar.isVisible = true
                },
                onSuccess = {
                    binding.loginProgressBar.isVisible = false
                    Intent(requireContext(), MainActivity::class.java).also {
                        startActivity(it)
                        requireActivity().finish()
                    }
                }
            )
        )
    }
}