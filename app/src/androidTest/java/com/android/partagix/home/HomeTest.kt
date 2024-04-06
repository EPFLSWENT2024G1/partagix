package com.android.partagix.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.screens.HomeScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.HomeScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

    @get:Rule
    val composeTestRule = createComposeRule()
    @RelaxedMockK
    lateinit var mockNavActions: NavigationActions

    @Before
    fun testSetup() {
        mockNavActions = mockk<NavigationActions>()
        every { mockNavActions.navigateTo(Route.HOME) } just Runs
        every { mockNavActions.navigateTo(Route.LOGIN) } just Runs

        composeTestRule.setContent {
            HomeScreen(mockNavActions)
        }
    }

    @Test
    fun mainContentIsDisplayed() = run {
        ComposeScreen.onComposeScreen<HomeScreen>(composeTestRule) { mainContent { assertIsDisplayed() } }
    }

    @Test
    fun bottomNavBarIsDisplayed() = run {
        ComposeScreen.onComposeScreen<HomeScreen>(composeTestRule) { bottomNavBar { assertIsDisplayed() } }
    }

    @Test
    fun bottomNavBarItemInventoryIsDisplayed() = run {
        ComposeScreen.onComposeScreen<HomeScreen>(composeTestRule) {
            bottomNavBarItemInventory { assertIsDisplayed() }
        }
    }
}
