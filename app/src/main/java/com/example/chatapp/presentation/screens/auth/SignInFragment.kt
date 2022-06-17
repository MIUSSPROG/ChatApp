package com.example.chatapp.presentation.screens.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment: Fragment(R.layout.fragment_sign_in) {

    private lateinit var binding: FragmentSignInBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignInBinding.bind(view)
        binding.apply {
            editNumber.requestFocus()
            continueButton.setOnClickListener {
                val phoneNumber = editNumber.text.toString()
                if (phoneNumber.isNotEmpty()){
                    findNavController().navigate(
                        SignInFragmentDirections.actionSignInFragmentToOTPFragment(phoneNumber = phoneNumber)
                    )
                }
            }
        }

    }

}