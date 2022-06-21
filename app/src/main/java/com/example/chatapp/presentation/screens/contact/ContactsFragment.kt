package com.example.chatapp.presentation.screens.contact

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.core.util.ActionListener
import com.example.chatapp.core.util.DataState
import com.example.chatapp.databinding.FragmentContactsBinding
import com.example.chatapp.domain.model.User
//import com.example.chatapp.presentation.screens.contact.adapter.ContactActionListener
import com.example.chatapp.presentation.screens.contact.adapter.ContactsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactsFragment: Fragment(R.layout.fragment_contacts) {

    private lateinit var binding: FragmentContactsBinding
    private val viewModel: ContactsViewModel by viewModels()
    private lateinit var adapter: ContactsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentContactsBinding.bind(view)

        adapter = ContactsAdapter(object: ActionListener<User>{
            override fun itemClick(user: User) {
                findNavController().navigate(ContactsFragmentDirections.actionContactsFragmentToChatFragment(user))
            }
        })
        binding.rv.adapter = adapter
        viewModel.getUsers()
        viewModel.usersLiveData.observe(viewLifecycleOwner){ dataState ->
            when(dataState){
                is DataState.Success -> {
                    adapter.submitList(dataState.data)
                }
                is DataState.Error -> {
                    Toast.makeText(requireContext(), "Ошибка подгрузки данных", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.changeUserChatStatus("Online")
    }

    override fun onPause() {
        super.onPause()
        viewModel.changeUserChatStatus("Offline")
    }
}