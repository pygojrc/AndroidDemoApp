package io.github.pygojrc.androidmoderndemo

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import io.github.pygojrc.androidmoderndemo.navigation.AppNavigation
import io.github.pygojrc.androidmoderndemo.navigation.ComponentsDestination
import io.github.pygojrc.androidmoderndemo.navigation.HomeDestination
import io.github.pygojrc.androidmoderndemo.navigation.navigationItems
import io.github.pygojrc.androidmoderndemo.navigation.titleRes
import io.github.pygojrc.androidmoderndemo.ui.components.AppDrawer
import io.github.pygojrc.androidmoderndemo.ui.components.DemoFloatingActionMenu
import io.github.pygojrc.androidmoderndemo.ui.theme.AndroidModernDemoTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernAndroidApp() {
    var dynamicColor by rememberSaveable { mutableStateOf(true) }
    var showAboutDialog by rememberSaveable { mutableStateOf(false) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showFloatingPanel by rememberSaveable { mutableStateOf(false) }
    var floatingMenuExpanded by rememberSaveable { mutableStateOf(false) }

    AndroidModernDemoTheme(dynamicColor = dynamicColor) {
        val backStack = rememberNavBackStack(HomeDestination)
        val currentDestination = backStack.lastOrNull() ?: HomeDestination
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val snackbarMessage = stringResource(R.string.components_snackbar_message)

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppDrawer(
                    currentDestination = currentDestination,
                    onDestinationSelected = { destination ->
                        selectTopLevelDestination(backStack, destination)
                        floatingMenuExpanded = false
                        scope.launch { drawerState.close() }
                    },
                    onAboutSelected = {
                        showAboutDialog = true
                        scope.launch { drawerState.close() }
                    },
                )
            },
        ) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(stringResource(currentDestination.titleRes())) },
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("open_drawer"),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_menu),
                                    contentDescription = stringResource(R.string.action_open_drawer),
                                )
                            }
                        },
                    )
                },
                bottomBar = {
                    AppBottomBar(
                        currentDestination = currentDestination,
                        onDestinationSelected = { destination ->
                            selectTopLevelDestination(backStack, destination)
                            floatingMenuExpanded = false
                        },
                    )
                },
                floatingActionButton = {
                    if (currentDestination == ComponentsDestination) {
                        DemoFloatingActionMenu(
                            expanded = floatingMenuExpanded,
                            onExpandedChange = { floatingMenuExpanded = it },
                            onShowPanel = {
                                showFloatingPanel = true
                                floatingMenuExpanded = false
                            },
                            onShowSnackbar = {
                                floatingMenuExpanded = false
                                scope.launch { snackbarHostState.showSnackbar(snackbarMessage) }
                            },
                        )
                    }
                },
            ) { innerPadding ->
                AppNavigation(
                    backStack = backStack,
                    dynamicColor = dynamicColor,
                    onDynamicColorChange = { dynamicColor = it },
                    showFloatingPanel = showFloatingPanel,
                    onShowSnackbar = {
                        scope.launch { snackbarHostState.showSnackbar(snackbarMessage) }
                    },
                    onShowBottomSheet = { showBottomSheet = true },
                    onShowFloatingPanel = { showFloatingPanel = true },
                    onHideFloatingPanel = { showFloatingPanel = false },
                    modifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding),
                )
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = stringResource(R.string.components_bottom_sheet_title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = stringResource(R.string.components_bottom_sheet_description),
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(
                        onClick = { showBottomSheet = false },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.action_close))
                    }
                }
            }
        }

        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text(stringResource(R.string.about_title)) },
                text = { Text(stringResource(R.string.about_description)) },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text(stringResource(R.string.action_confirm))
                    }
                },
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
