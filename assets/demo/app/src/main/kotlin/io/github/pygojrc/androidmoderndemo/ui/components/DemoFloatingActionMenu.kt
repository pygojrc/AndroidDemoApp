package io.github.pygojrc.androidmoderndemo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.pygojrc.androidmoderndemo.R

@Composable
fun DemoFloatingActionMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onShowPanel: () -> Unit,
    onShowSnackbar: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ExtendedFloatingActionButton(
                    onClick = onShowSnackbar,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_message),
                            contentDescription = null,
                        )
                    },
                    text = { Text(stringResource(R.string.components_show_snackbar)) },
                )
                ExtendedFloatingActionButton(
                    onClick = onShowPanel,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_floating_panel),
                            contentDescription = null,
                        )
                    },
                    text = { Text(stringResource(R.string.components_show_floating_panel)) },
                )
            }
        }

        FloatingActionButton(onClick = { onExpandedChange(!expanded) }) {
            Icon(
                painter = painterResource(if (expanded) R.drawable.ic_close else R.drawable.ic_add),
                contentDescription = stringResource(
                    if (expanded) R.string.action_close_floating_menu else R.string.action_open_floating_menu,
                ),
            )
        }
    }
}
