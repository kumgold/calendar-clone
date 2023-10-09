package com.goldcompany.apps.todoapplication.home

import androidx.lifecycle.ViewModel
import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.data.repository.TaskRepository
import com.goldcompany.apps.todoapplication.util.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class TaskUiState(
    val items: List<Task> = emptyList(),
    val isLoading: LoadingState = LoadingState.INIT,
    val userMessage: Int? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

}