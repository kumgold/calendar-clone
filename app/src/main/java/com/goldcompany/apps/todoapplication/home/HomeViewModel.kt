package com.goldcompany.apps.todoapplication.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.data.repository.TaskRepository
import com.goldcompany.apps.todoapplication.util.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskUiState(
    val items: List<Task> = emptyList(),
    val loadingState: LoadingState = LoadingState.INIT,
    val userMessage: Int? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState

    init {
        observeAllTask()
    }

    private fun observeAllTask() {
        viewModelScope.launch {
            repository.getTasksStream().map { tasks ->
                loading()

                _uiState.update {
                    it.copy(
                        items = tasks,
                        loadingState = LoadingState.SUCCESS
                    )
                }
            }
        }
    }

    private fun loading() {
        _uiState.update {
            it.copy(
                loadingState = LoadingState.LOADING
            )
        }
    }
}