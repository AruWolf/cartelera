package com.litvy.carteleria.ui.startup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StartupScreen(
    onStartupCompleted: @Composable () -> Unit
) {

    val viewModel: StartupViewModel = viewModel()

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.start()
    }

    when (val currentState = state) {

        StartupState.Loading -> {
            StartupLoading()
        }

        StartupState.Ready -> {
            onStartupCompleted()
        }

        is StartupState.Error -> {
            StartupError(
                state = currentState,
                onRetry = {
                    viewModel.start()
                }
            )
        }
    }
}