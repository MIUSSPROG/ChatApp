package com.example.chatapp.core.util

import android.app.Activity
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.chatapp.domain.model.User

fun Activity.toast(text: String, length: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, text, length).show()
}

fun Activity.color(@ColorRes color: Int) = ContextCompat.getColor(this, color)

fun ImageView.load(url: String){
    if (url.isNotEmpty()){
        Glide.with(this.context).load(url).into(this)
    }
}

interface ActionListener<in T>{
    fun itemClick(item: T)
}