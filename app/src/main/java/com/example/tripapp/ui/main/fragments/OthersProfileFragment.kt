package com.example.tripapp.ui.main.fragments

import android.graphics.Color
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.example.tripapp.R
import com.example.tripapp.data.entities.User
import com.example.tripapp.databinding.FragmentProfileBinding
import com.example.tripapp.utils.EventObserver
import com.example.tripapp.utils.autoCleared

class OthersProfileFragment : ProfileFragment() {

    private var binding: FragmentProfileBinding by autoCleared()

    private val args: OthersProfileFragmentArgs by navArgs()

    override val uid: String
        get() = args.uid

    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()

        binding.btnToggleFollow.setOnClickListener {
            viewModel.toggleFollowForUser(uid)
        }

    }

    private fun subscribeToObservers() {
        viewModel.profileMeta.observe(viewLifecycleOwner, EventObserver {
            binding.btnToggleFollow.isVisible = true
            setupToggleFollowButton(it)
            currentUser = it
        })
        viewModel.followStatus.observe(viewLifecycleOwner, EventObserver {
            currentUser?.isFollowing = it
            setupToggleFollowButton(currentUser ?: return@EventObserver)
        })
    }

    private fun setupToggleFollowButton(user: User) {
        binding.btnToggleFollow.apply {
            val changeBounds = ChangeBounds().apply {
                duration = 300
                interpolator = OvershootInterpolator()
            }
            val set1 = ConstraintSet()
            val set2 = ConstraintSet()
            set1.clone(requireContext(), R.layout.fragment_profile)
            set2.clone(requireContext(), R.layout.fragment_profile_anim)
            TransitionManager.beginDelayedTransition(binding.clProfile, changeBounds)
            if (user.isFollowing) {
                text = requireContext().getString(R.string.unfollow)
                setBackgroundColor(Color.RED)
                setTextColor(Color.WHITE)
                set1.applyTo(binding.clProfile)
            } else {
                text = requireContext().getString(R.string.follow)
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
        }
    }
}