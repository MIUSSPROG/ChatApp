package com.example.chatapp.presentation.screens.setup

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.core.util.DataState
import com.example.chatapp.databinding.FragmentSetupProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupProfileFragment: Fragment(R.layout.fragment_setup_profile) {

    private lateinit var binding: FragmentSetupProfileBinding
    private val viewModel: SetupProfileViewModel by viewModels()
    private var dialog: ProgressDialog? = null
    var selectedImage: Uri? = null
    private val resultLauncher = registerForActivityResult(StartActivityForResult()){
        if (it.resultCode == RESULT_OK && it.data != null && it.data!!.data != null){
            val uri = it.data!!.data
            binding.imageProfilePicker.setImageURI(uri)
            selectedImage = uri
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetupProfileBinding.bind(view)
        dialog = ProgressDialog(requireContext())
        dialog!!.setMessage("Updating Profile...")
        dialog!!.setCancelable(false)

        binding.imageProfilePicker.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }
        binding.continueBtn2.setOnClickListener {
            val name = binding.nameBox.text.toString()
            if (name.isEmpty()){
                binding.nameBox.error = "Please type a name"
            }
            dialog!!.show()
            viewModel.saveUserInfo(selectedImage, name)
        }
        viewModel.resultLiveData.observe(viewLifecycleOwner){ dataState ->
            when(dataState){
                is DataState.Success -> {
                    dialog!!.dismiss()
                    findNavController().navigate(SetupProfileFragmentDirections.actionSetupProfileFragmentToContactsFragment())
                }
                is DataState.Error -> {
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}