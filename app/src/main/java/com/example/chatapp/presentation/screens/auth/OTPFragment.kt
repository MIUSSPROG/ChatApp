package com.example.chatapp.presentation.screens.auth

import android.app.ProgressDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.chatapp.R
import com.example.chatapp.core.util.DataState
import com.example.chatapp.databinding.FragmentOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class OTPFragment: Fragment(R.layout.fragment_otp) {

    private lateinit var binding: FragmentOtpBinding
    private val args: OTPFragmentArgs by navArgs()
    private var dialog: ProgressDialog? = null
    private val viewModel: OTPViewModel by viewModels()
    var verificationId: String? = null
    private var firebaseAuth: FirebaseAuth? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpBinding.bind(view)
        dialog = ProgressDialog(requireContext())
        dialog!!.setMessage("Sending OTP...")
        dialog!!.setCancelable(false)
        dialog!!.show()
        firebaseAuth = FirebaseAuth.getInstance()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth!!)
            .setPhoneNumber(args.phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {}

                override fun onVerificationFailed(p0: FirebaseException) {}

                override fun onCodeSent(verifyId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verifyId, forceResendingToken)
                    dialog!!.dismiss()
                    verificationId = verifyId
                    val imm = getSystemService(requireContext(), InputMethodManager::class.java)
                    imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    binding.otpView.requestFocus()
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        binding.otpView.setOtpCompletionListener{ otp ->
            val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
            viewModel.verifyPhoneNumber(credential)
        }
        viewModel.authResultLiveData.observe(viewLifecycleOwner){ dataState ->
            when(dataState){
                is DataState.Success ->{
                    findNavController().navigate(
                        OTPFragmentDirections.actionOTPFragmentToContactsFragment()
                    )
                }
                is DataState.Error -> {
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}