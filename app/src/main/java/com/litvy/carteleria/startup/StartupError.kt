package com.litvy.carteleria.ui.startup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.startup.StartupState

@Composable
fun StartupError(
    state: StartupState.Error,
    onRetry: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "No fue posible iniciar la aplicación."
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = state.stage.name
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = onRetry
            ) {

                Text("Reintentar")

            }

        }

    }

}