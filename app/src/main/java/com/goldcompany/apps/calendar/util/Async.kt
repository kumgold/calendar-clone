package com.goldcompany.apps.calendar.util

sealed class Async<out T> {
    data class Error(val errorMessage: Int) : Async<Nothing>()

    data class Success<out T>(val data: T) : Async<T>()
}