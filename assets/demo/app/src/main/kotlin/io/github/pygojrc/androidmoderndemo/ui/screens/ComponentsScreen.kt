package io.github.pygojrc.androidmoderndemo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.github.pygojrc.androidmoderndemo.R
import io.github.pygojrc.androidmoderndemo.ui.components.DraggableFloatingPanelHost

@Composable
fun ComponentsScreen(
    showFloatingPanel: Boolean,
    onShowSnackbar: () -> Unit,
    onShowBottomSheet: () -> Unit,
    onShowFloatingPanel: () -> Unit,
    onHideFloatingPanel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showMenu by rememberSaveable { mutableStateOf(false) }
    var checked by rememberSaveable { mutableStateOf(true) }
    var switched by rememberSaveable { mutableStateOf(true) }
    var sliderValue by rememberSaveable { mutableFloatStateOf(0.5f) }
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(PaddingValues(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 112.dp)),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.components_heading),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = stringResource(R.string.components_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            DemoSection(title = stringResource(R.string.components_feedback_title)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(onClick = onShowSnackbar, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.components_show_snackbar))
                    }
                    OutlinedButton(onClick = { showDialog = true }, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.components_show_dialog))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(onClick = onShowBottomSheet, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.components_show_bottom_sheet))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.components_show_menu))
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.components_menu_first)) },
                                onClick = { showMenu = false },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.components_menu_second)) },
                                onClick = { showMenu = false },
                            )
                        }
                    }
                }
            }

            DemoSection(title = stringResource(R.string.components_floating_title)) {
                Text(
                    text = stringResource(R.string.components_floating_description),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = if (showFloatingPanel) onHideFloatingPanel else onShowFloatingPanel,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        stringResource(
                            if (showFloatingPanel) R.string.components_hide_floating_panel
                            else R.string.components_show_floating_panel,
                        ),
                    )
                }
            }

            DemoSection(title = stringResource(R.string.components_controls_title)) {
                DemoToggleRow(
                    label = stringResource(R.string.components_checkbox),
                    control = {
                        Checkbox(checked = checked, onCheckedChange = { checked = it })
                    },
                )
                DemoToggleRow(
                    label = stringResource(R.string.components_switch),
                    control = {
                        Switch(checked = switched, onCheckedChange = { switched = it })
                    },
                )
                Text(stringResource(R.string.components_slider))
                Slider(value = sliderValue, onValueChange = { sliderValue = it })
            }

            DemoSection(title = stringResource(R.string.components_input_title)) {
                Text(
                    text = stringResource(R.string.input_description),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.input_title_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    ),
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.input_content_label)) },
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                )
            }
        }

        DraggableFloatingPanelHost(
            visible = showFloatingPanel,
            onClose = onHideFloatingPanel,
            modifier = Modifier.fillMaxSize(),
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.components_dialog_title)) },
            text = { Text(stringResource(R.string.components_dialog_description)) },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
        )
    }
}

@Composable
private fun DemoSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun DemoToggleRow(
    label: String,
    control: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label)
        control()
    }
}
