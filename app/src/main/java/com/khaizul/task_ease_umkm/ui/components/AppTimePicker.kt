package com.khaizul.task_ease_umkm.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
        initialHour = selectedHour, initialMinute = selectedMinute, is24Hour = true
    )

    Column(modifier = modifier) {
        OutlinedTextField(value = if (selectedHour >= 0 && selectedMinute >= 0) "%02d:%02d".format(
            selectedHour,
            selectedMinute
        ) else "",
            onValueChange = {},
            label = { Text(text = label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showTimePicker = true },
            trailingIcon = {
                IconButton(
                    onClick = { showTimePicker = true }, modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Select time",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            )
        )

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = 6.dp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Select Time",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        TimePicker(
                            state = timePickerState,
                            modifier = Modifier.padding(8.dp),
                            colors = TimePickerDefaults.colors(
                                selectorColor = MaterialTheme.colorScheme.primary,
                                clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showTimePicker = false },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                                    showTimePicker = false
                                }, colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("OK", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}