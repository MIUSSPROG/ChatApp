package com.example.chatapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.DeleteLayoutBinding
import com.example.chatapp.databinding.ReceiveMsgBinding
import com.example.chatapp.databinding.SendMsgBinding
import com.example.chatapp.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MessagesAdapter(
    var context: Context,
    val messages: ArrayList<Message>,
    var senderRoom: String,
): RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    val ITEM_SEND = 1
    val ITEM_RECEIVE = 2

    inner class SendMsgHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var binding = SendMsgBinding.bind(itemView)
    }

    inner class ReceiveMsgHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var binding = ReceiveMsgBinding.bind(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (FirebaseAuth.getInstance().uid == message.senderId){
            ITEM_SEND
        }else{
            ITEM_RECEIVE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SEND){
            val view = LayoutInflater.from(context).inflate(R.layout.send_msg, parent, false)
            SendMsgHolder(view)
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.receive_msg, parent, false)
            ReceiveMsgHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curItem = messages[position]
        if (holder is SendMsgHolder) {
            if (curItem.imageUrl?.isNotEmpty() == true) {
                holder.binding.sendPhoto.visibility = View.VISIBLE
                holder.binding.messageSendItem.visibility = View.GONE
                Glide.with(context)
                    .load(curItem.imageUrl)
                    .placeholder(R.drawable.ic_image)
                    .into(holder.binding.sendPhoto)
            }else {
                holder.binding.sendPhoto.visibility = View.GONE
                holder.binding.messageSendItem.visibility = View.VISIBLE
                holder.binding.messageSendItem.text = curItem.message
            }
            holderLongClickListener(holder, curItem)
        } else if (holder is ReceiveMsgHolder){

            if (curItem.imageUrl?.isNotEmpty() == true) {
                holder.binding.receivePhoto.visibility = View.VISIBLE
                holder.binding.messageReceiveItem.visibility = View.GONE
                Glide.with(context)
                    .load(curItem.imageUrl)
                    .placeholder(R.drawable.ic_empty_message)
                    .into(holder.binding.receivePhoto)
            }else {
                holder.binding.receivePhoto.visibility = View.GONE
                holder.binding.messageReceiveItem.visibility = View.VISIBLE
                holder.binding.messageReceiveItem.text = curItem.message
            }
            holderLongClickListener(holder, curItem)
        }
    }

    private fun holderLongClickListener(holder: RecyclerView.ViewHolder, curItem: Message) {
        holder.itemView.setOnLongClickListener {

            val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
            val binding = DeleteLayoutBinding.bind(view)
            val dialog = AlertDialog.Builder(context)
                .setTitle("Delete Message")
                .setView(binding.root)
                .create()
            dialog.show()
            binding.deleteForEveryone.setOnClickListener {
                curItem.message = "This message is removed"
                curItem.imageUrl = null
                FirebaseDatabase.getInstance().reference.child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(curItem.messageId!!)
                    .setValue(null)

                dialog.dismiss()
            }

            binding.cancel.setOnClickListener { dialog.dismiss() }
            false
        }
    }

    override fun getItemCount(): Int = messages.size

}