package com.example.tripapp.ui.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.tripapp.R
import com.example.tripapp.databinding.FragmentLoginBinding
import com.example.tripapp.databinding.FragmentRegisterBinding
import com.example.tripapp.utils.autoCleared

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var binding: FragmentRegisterBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLogin.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(R.id.action_registerFragment_to_loginFragment)

        }

    }
}