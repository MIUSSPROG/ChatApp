package com.example.chatapp.core.util

import com.example.chatapp.domain.model.User

sealed class DataState<T>(
    data: T? = null,
    exception: Exception? = null
){
    data class Success<T>(val data: T? = null): DataState<T>(data, null)
    data class Failure<T>(val data: T? = null): DataState<T>(data, null)
    data class Error<T>(val exception: Exception): DataState<T>(null, exception)
}