package io.github.pygojrc.androidmoderndemo

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import io.github.pygojrc.androidmoderndemo.navigation.AppNavigation
import io.github.pygojrc.androidmoderndemo.navigation.HomeDestination
import io.github.pygojrc.androidmoderndemo.navigation.navigationItems
import io.github.pygojrc.androidmoderndemo.navigation.titleRes
import io.github.pygojrc.androidmoderndemo.ui.theme.AndroidModernDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernAndroidApp() {
    var dynamicColor by rememberSaveable { mutableStateOf(true) }

    AndroidModernDemoTheme(dynamicColor = dynamicColor) {
        val backStack = rememberNavBackStack(HomeDestination)
        val currentDestination = backStack.lastOrNull() ?: HomeDestination

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(currentDestination.titleRes())) },
                )
            },
            bottomBar = {
                AppBottomBar(
                    currentDestination = currentDestination,
                    onDestinationSelected = { destination ->
                        selectTopLevelDestination(backStack, destination)
                    },
                )
            },
        ) { innerPadding ->
            AppNavigation(
                backStack = backStack,
                dynamicColor = dynamicColor,
                onDynamicColorChange = { dynamicColor = it },
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            )
        }
    }
}

@Composable
private fun AppBottomBar(
    currentDestination: NavKey,
    onDestinationSelected: (NavKey) -> Unit,
) {
    NavigationBar {
        navigationItems.forEach { item ->
            NavigationBarItem(
                modifier = Modifier.testTag(item.testTag),
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
    }
}

private fun selectTopLevelDestination(backStack: MutableList<NavKey>, destination: NavKey) {
    if (backStack.lastOrNull() == destination) return

    backStack.clear()
    backStack.add(HomeDestination)
    if (destination != HomeDestination) {
        backStack.add(destination)
    }
}
