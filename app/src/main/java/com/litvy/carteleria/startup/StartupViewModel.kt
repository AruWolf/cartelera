package com.litvy.carteleria.startup

import StartupManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StartupViewModel(
    private val startupManager: StartupManager = StartupManager()
) : ViewModel() {

    private val _state = MutableStateFlow<StartupState>(StartupState.Loading)

    val state: StateFlow<StartupState> = _state

    private var isInitializing = false

    fun start() {

        if (isInitializing) return

        isInitializing = true

        _state.value = StartupState.Loading

        viewModelScope.launch {

            try {

                startupManager.initialize()

                _state.value = StartupState.Ready

            } catch (e: Exception) {

                _state.value =
                    StartupState.Error(
                        StartupStage.INITIALIZATION,
                        e
                    )

            } finally {

                isInitializing = false

            }

        }

    }
}