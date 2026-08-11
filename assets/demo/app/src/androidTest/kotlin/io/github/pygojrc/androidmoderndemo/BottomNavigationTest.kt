package io.github.pygojrc.androidmoderndemo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class BottomNavigationTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomNavigationSwitchesScreens() {
        composeRule.onNodeWithTag("navigation_components").performClick()
        composeRule.onNodeWithText("常用 Compose 组件").assertIsDisplayed()

        composeRule.onNodeWithTag("navigation_settings").performClick()
        composeRule.onNodeWithText("外观").assertIsDisplayed()

        composeRule.onNodeWithTag("navigation_home").performClick()
        composeRule.onNodeWithText("现代 Android，从可靠模板开始").assertIsDisplayed()
    }
}
