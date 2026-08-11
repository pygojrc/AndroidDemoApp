package io.github.pygojrc.androidmoderndemo.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import io.github.pygojrc.androidmoderndemo.R
import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination : NavKey

@Serializable
data object ComponentsDestination : NavKey

@Serializable
data object SettingsDestination : NavKey

data class NavigationItem(
    val destination: NavKey,
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val iconRes: Int,
    val testTag: String,
)

val navigationItems = listOf(
    NavigationItem(HomeDestination, R.string.navigation_home, R.drawable.ic_home, "navigation_home"),
    NavigationItem(
        ComponentsDestination,
        R.string.navigation_components,
        R.drawable.ic_components,
        "navigation_components",
    ),
    NavigationItem(SettingsDestination, R.string.navigation_settings, R.drawable.ic_settings, "navigation_settings"),
)

@StringRes
fun NavKey.titleRes(): Int = when (this) {
    HomeDestination -> R.string.title_home
    ComponentsDestination -> R.string.title_components
    SettingsDestination -> R.string.title_settings
    else -> R.string.app_name
}
