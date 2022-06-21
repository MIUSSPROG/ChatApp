package com.example.chatapp.presentation.screens.chat

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.core.util.ActionListener
import com.example.chatapp.core.util.DataState
import com.example.chatapp.databinding.DeleteLayoutBinding
import com.example.chatapp.databinding.FragmentChatBinding
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.User
import com.example.chatapp.presentation.screens.chat.adapter.ChatAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment: Fragment(R.layout.fragment_chat) {

    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private val args: ChatFragmentArgs by navArgs()
    private var adapter: ChatAdapter? = null
    private lateinit var dialog: ProgressDialog
    private var user: User? = null
    private val resultLauncher = registerForActivityResult(StartActivityForResult()){
        if (it.resultCode == RESULT_OK && it.data!= null && it.data!!.data != null) {
            val uri = it.data!!.data!!
            dialog.show()
            viewModel.attachFile(uri, args.user.uuid!!)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)

        binding.backBtn.setOnClickListener { findNavController().popBackStack() }
        initObservers()

        dialog = ProgressDialog(requireContext())
        user = args.user
        binding.toolbarProfileName.text = user!!.name
        Glide.with(this).load(user!!.profileImage).into(binding.toolbarProfileImage)

        viewModel.getReceiverStatus(user!!.uuid!!)
        viewModel.getMessages(user!!.uuid!!)

        adapter = ChatAdapter(actionListener = object : ActionListener<Message>{
            override fun itemClick(item: Message) {
                val binding = DeleteLayoutBinding.bind(LayoutInflater.from(requireContext()).inflate(R.layout.delete_layout, null))
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()
                dialog.show()
                binding.deleteForEveryone.setOnClickListener {
                    item.message = "This message is removed"
                    item.imageUrl = null
                    viewModel.deleteMessage(args.user.uuid!!, item)
                }
                binding.cancel.setOnClickListener { dialog.dismiss() }
            }
        })
        binding.rvMessages.adapter = adapter

        binding.messageBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.textTyping()
            }
        })

        binding.btnAttachFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }

        binding.btnSend.setOnClickListener {
            val messageTxt = binding.messageBox.text.toString()
            viewModel.sendMessage(messageTxt, user!!.uuid!!)
        }
    }

    private fun initObservers(){

        viewModel.messagesLiveData.observe(viewLifecycleOwner){ dataState ->
            when(dataState){
                is DataState.Success -> {
                    adapter!!.submitList(dataState.data)
                    if (dataState.data!!.isNotEmpty()) {
                        binding.rvMessages.smoothScrollToPosition(dataState.data!!.size - 1)
                    }
                }
                is DataState.Error -> {
                    Toast.makeText(requireContext(), "Ошибка подгрузки данных!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        viewModel.sendMessageLiveData.observe(viewLifecycleOwner){ dataState ->
            when(dataState){
                is DataState.Success -> {
                    binding.messageBox.setText("")
                }
                is DataState.Error -> {
                    Toast.makeText(requireContext(), "Ошибка отправки сообщения!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.deletedMessageLiveData.observe(viewLifecycleOwner){ dataState ->
            when(dataState){
                is DataState.Success -> {
                    dialog.dismiss()
                }
                is DataState.Error -> {
                    Toast.makeText(requireContext(), "Ошибка удаления!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.receiverStatusLiveData.observe(viewLifecycleOwner){ dataState ->
            when(dataState){
                is DataState.Success -> {
                    if (dataState.data == "Offline"){
                        binding.toolbarProfileStatus.visibility = View.GONE
                    }else{
                        binding.toolbarProfileStatus.visibility = View.VISIBLE
                        binding.toolbarProfileStatus.text = dataState.data
                    }
                }
                is DataState.Error -> {
                    Toast.makeText(requireContext(), "Ошибка статуса", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.fileAttachedLiveData.observe(viewLifecycleOwner){ dataState ->
            when(dataState){
                is DataState.Success -> {
                    dialog.dismiss()
                    binding.messageBox.setText("")
                }
                is DataState.Error -> {
                    Toast.makeText(requireContext(), "Ошибка отправки файла", Toast.LENGTH_SHORT).show()
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