package com.khaizul.task_ease_umkm.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimePicker(
    label: String,
    selectedHour: Int,
    selectedMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        is24Hour = true
    )

    Column(modifier = modifier) {
        OutlinedTextField(
            value = if (selectedHour >= 0 && selectedMinute >= 0)
                "%02d:%02d".format(selectedHour, selectedMinute)
            else "",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Select time"
                    )
                }
            }
        )

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                properties = DialogProperties(),
                content = {
                    Column {
                        TimePicker(state = timePickerState)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showTimePicker = false }
                            ) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                                    showTimePicker = false
                                }
                            ) {
                                Text("OK")
                            }
                        }
                    }
                }
            )
        }
    }
}