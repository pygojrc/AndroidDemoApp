package io.github.pygojrc.androidmoderndemo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import io.github.pygojrc.androidmoderndemo.R
import io.github.pygojrc.androidmoderndemo.navigation.navigationItems

@Composable
fun AppDrawer(
    currentDestination: NavKey,
    onDestinationSelected: (NavKey) -> Unit,
    onAboutSelected: () -> Unit,
) {
    ModalDrawerSheet {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.large),
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(R.string.drawer_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        HorizontalDivider()
        Spacer(Modifier.height(12.dp))

        navigationItems.forEach { item ->
            NavigationDrawerItem(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .testTag("drawer_${item.testTag}"),
                selected = currentDestination == item.destination,
                onClick = { onDestinationSelected(item.destination) },
                icon = {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(item.labelRes)) },
            )
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider()
        NavigationDrawerItem(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            selected = false,
            onClick = onAboutSelected,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    contentDescription = null,
                )
            },
            label = { Text(stringResource(R.string.navigation_about)) },
        )
    }
}
