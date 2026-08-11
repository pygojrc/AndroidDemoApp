package io.github.pygojrc.androidmoderndemo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import io.github.pygojrc.androidmoderndemo.ui.screens.HomeScreen
import io.github.pygojrc.androidmoderndemo.ui.screens.InputScreen
import io.github.pygojrc.androidmoderndemo.ui.screens.SettingsScreen

@Composable
fun AppNavigation(
    backStack: MutableList<NavKey>,
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        },
        entryProvider = entryProvider {
            entry<HomeDestination> {
                HomeScreen()
            }
            entry<InputDestination> {
                InputScreen()
            }
            entry<SettingsDestination> {
                SettingsScreen(
                    dynamicColor = dynamicColor,
                    onDynamicColorChange = onDynamicColorChange,
                )
            }
        },
    )
}
