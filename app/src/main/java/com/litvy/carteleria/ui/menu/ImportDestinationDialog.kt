package com.litvy.carteleria.ui.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.R
import com.litvy.carteleria.domain.external.ExternalFolder

private val textColor: Color = Color.LightGray.copy(alpha = 0.90f)

@Composable
fun ImportDestinationDialog(
    folders: List<ExternalFolder>,
    selectedFolderPath: String?,
    onFolderSelected: (String) -> Unit,
    onCreateFolderRequested: () -> Unit,
    onContinue: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = Color.Black.copy(alpha = 0.90f),
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.select_folder), color = textColor) },
        text = {
            Column {
                folders.forEach { folder ->
                    TextButton(
                        onClick = { onFolderSelected(folder.path) }
                    ) {
                        RadioButton(
                            selected = selectedFolderPath == folder.path,
                            onClick = { onFolderSelected(folder.path) }
                        )
                        Text(text = folder.name, color = textColor)
                    }
                }

                TextButton(onClick = onCreateFolderRequested) {
                    Text(text = stringResource(R.string.new_folder), color = textColor)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = selectedFolderPath != null,
                onClick = onContinue
            ) {
                Text(stringResource(R.string.continue_action), color = textColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = textColor)
            }
        }
    )
}

@Composable
fun NewFolderNameDialog(
    folderExists: (String) -> Boolean,
    onCreate: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var folderName by remember { mutableStateOf("") }
    val trimmedName = folderName.trim()
    val duplicate = trimmedName.isNotEmpty() && folderExists(trimmedName)
    val canCreate = trimmedName.isNotEmpty() && !duplicate

    AlertDialog(
        containerColor = Color.Black.copy(alpha = 0.90f),
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.new_folder), color = textColor) },
        text = {
            Column {
                OutlinedTextField(
                    value = folderName,
                    onValueChange = { folderName = it },
                    label = { Text(stringResource(R.string.folder_name), color = textColor) },
                    isError = duplicate,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,

                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,

                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = textColor,

                        errorTextColor = Color.White,
                        errorCursorColor = Color.White,
                        errorBorderColor = Color.Red,
                        errorLabelColor = Color.Red
                    )
                )

                if (duplicate) {
                    Text(
                        text = stringResource(R.string.folder_already_exists),
                        color = Color(0xFFFF8888),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = canCreate,
                onClick = { onCreate(trimmedName) }
            ) {
                Text(stringResource(R.string.create), color = textColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = textColor)
            }
        }
    )
}
