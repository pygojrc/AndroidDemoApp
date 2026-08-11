package io.github.pygojrc.androidmoderndemo.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.pygojrc.androidmoderndemo.R

@Composable
fun SettingsScreen(
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_appearance),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            style = MaterialTheme.typography.titleLarge,
        )
        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_dynamic_color)) },
            supportingContent = { Text(stringResource(R.string.settings_dynamic_color_description)) },
            trailingContent = {
                Switch(
                    checked = dynamicColor,
                    onCheckedChange = onDynamicColorChange,
                )
            },
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_dark_mode)) },
            supportingContent = { Text(stringResource(R.string.settings_dark_mode_description)) },
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_platform)) },
            supportingContent = { Text(stringResource(R.string.settings_platform_description)) },
        )
    }
}
