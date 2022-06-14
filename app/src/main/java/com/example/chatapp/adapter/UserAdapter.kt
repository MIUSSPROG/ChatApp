package com.example.chatapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.MessagingActivity
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemProfileBinding
import com.example.chatapp.model.User

class UserAdapter(val context: Context, private val userList: ArrayList<User>): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(private val binding: ItemProfileBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(user: User){
            binding.apply {
                userName.text = user.name
                Glide.with(context).load(user.profileImage)
                    .placeholder(R.drawable.acount_image)
                    .into(profileImage)
                binding.root.setOnClickListener {
                    val intent = Intent(context, MessagingActivity::class.java)
                    intent.putExtra("name", user.name)
                    intent.putExtra("image", user.profileImage)
                    intent.putExtra("uid", user.uuid)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val curItem = userList[position]
        holder.bind(curItem)
    }

    override fun getItemCount(): Int = userList.size
}