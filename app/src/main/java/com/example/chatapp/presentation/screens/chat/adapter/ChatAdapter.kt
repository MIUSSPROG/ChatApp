package com.example.chatapp.presentation.screens.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.core.util.ActionListener
import com.example.chatapp.databinding.ReceiveMsgBinding
import com.example.chatapp.databinding.SendMsgBinding
import com.example.chatapp.domain.model.Message
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject


class ChatAdapter(private val actionListener: ActionListener<Message>): ListAdapter<Message, RecyclerView.ViewHolder>(DiffCallback()), View.OnLongClickListener {

    val ITEM_SEND = 1
    val ITEM_RECEIVE = 2

    inner class SendMsgHolder(private val binding: SendMsgBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(message: Message){
            binding.root.tag = message
            if (message.imageUrl?.isNotEmpty() == true){
                binding.sendPhoto.visibility = View.VISIBLE
                binding.messageSendItem.visibility = View.GONE
                Glide.with(binding.root)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.ic_image)
                    .into(binding.sendPhoto)
            }else{
                binding.sendPhoto.visibility = View.GONE
                binding.messageSendItem.visibility = View.VISIBLE
                binding.messageSendItem.text = message.message
            }
        }
    }

    inner class ReceiveMsgHolder(private val binding: ReceiveMsgBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(message: Message){
            if (message.imageUrl?.isNotEmpty() == true){
                binding.receivePhoto.visibility = View.VISIBLE
                binding.messageReceiveItem.visibility = View.GONE
                Glide.with(binding.root)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.ic_empty_message)
                    .into(binding.receivePhoto)
            }else{
                binding.receivePhoto.visibility = View.GONE
                binding.messageReceiveItem.visibility = View.VISIBLE
                binding.messageReceiveItem.text = message.message
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Message>(){
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (FirebaseAuth.getInstance().uid == message.senderId){
            ITEM_SEND
        }else{
            ITEM_RECEIVE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SEND){
            val binding = SendMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            binding.root.setOnLongClickListener(this)
            SendMsgHolder(binding)
        }else{
            val binding = ReceiveMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceiveMsgHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SendMsgHolder){
            holder.bind(getItem(position))
        } else if (holder is ReceiveMsgHolder){
            holder.bind(getItem(position))
        }
    }

    override fun onLongClick(v: View): Boolean {
        val message = v.tag as Message
        actionListener.itemClick(message)
        return true
    }
}