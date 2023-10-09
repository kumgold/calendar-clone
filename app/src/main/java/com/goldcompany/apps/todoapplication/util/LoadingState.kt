package com.goldcompany.apps.todoapplication.util

sealed class LoadingState {
    object INIT: LoadingState()
    object LOADING: LoadingState()
    object SUCCESS: LoadingState()
    object ERROR: LoadingState()
}