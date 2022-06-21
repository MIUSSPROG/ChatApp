package com.example.chatapp.presentation.screens.contact.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.core.util.ActionListener
import com.example.chatapp.databinding.ItemProfileBinding
import com.example.chatapp.domain.model.User
import com.example.chatapp.presentation.screens.contact.ContactsViewModel
import com.example.chatapp.presentation.screens.contact.adapter.ContactsAdapter.ContactsViewHolder

//interface ContactActionListener{
//    fun itemClick(user: User)
//}

class ContactsAdapter(
    private val actionListener: ActionListener<User>
): ListAdapter<User, ContactsViewHolder>(DiffCallback()), View.OnClickListener {
    inner class ContactsViewHolder(private val binding: ItemProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                root.tag = user
                userName.text = user.name
                Glide.with(binding.root.context).load(user.profileImage)
                    .placeholder(R.drawable.acount_image)
                    .into(profileImage)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val binding = ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setOnClickListener(this)
        return ContactsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onClick(v: View) {
        val user = v.tag as User
        actionListener.itemClick(user)
    }
}