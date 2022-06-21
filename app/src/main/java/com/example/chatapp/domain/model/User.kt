package com.example.chatapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uuid: String? = null,
    val name: String? = null,
    val phoneNumber: String? = null,
    val profileImage: String? = null
): Parcelable