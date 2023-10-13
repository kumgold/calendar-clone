package com.goldcompany.apps.todoapplication.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.apps.data.repository.TaskRepository
import com.goldcompany.apps.todoapplication.TASK_ID
import com.goldcompany.apps.todoapplication.util.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditTaskUiState(
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val loadingState: LoadingState = LoadingState.INIT
)

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val taskId: String? = savedStateHandle[TASK_ID]

    private val _uiState = MutableStateFlow(AddEditTaskUiState())
    val uiState: StateFlow<AddEditTaskUiState> = _uiState

    init {
        getTask()
    }

    private fun getTask() {
        loading()

        viewModelScope.launch {
            taskId?.let { id ->
                repository.getTask(id).map { task ->
                    _uiState.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            isCompleted = task.isCompleted,
                            loadingState = LoadingState.SUCCESS
                        )
                    }
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