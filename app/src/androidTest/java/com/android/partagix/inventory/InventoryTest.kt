package com.android.partagix.inventory

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.screens.InventoryScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.screens.InventoryScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InventoryTest: TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

    @get:Rule val composeTestRule = createComposeRule()

    //@get:Rule val mockkRule = MockKRule(this)

    lateinit var navActions: NavigationActions

    @Before
    fun testSetup() {
        val vm = InventoryViewModel()
        composeTestRule.setContent { InventoryScreen(vm, navActions::navigateTo) }
    }

    @Test
    fun centerTextIsDisplayed() = run {
        onComposeScreen<InventoryScreen>(composeTestRule) {
            mainContentText {
                assertIsDisplayed()
                assertTextContains("There is")
            }
        }
    }
}
