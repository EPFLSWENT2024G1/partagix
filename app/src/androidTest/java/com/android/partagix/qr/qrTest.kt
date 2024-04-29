package com.android.partagix.qr

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.screens.QrScanScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.QrScanScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
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
class qrTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
    @get:Rule val composeTestRule = createComposeRule()

    @RelaxedMockK lateinit var mockNavActions: NavigationActions

    @Before
    fun testSetup() {
        mockNavActions = mockk<NavigationActions>()
        every { mockNavActions.navigateTo(Route.HOME) } just Runs
        every { mockNavActions.navigateTo(Route.LOGIN) } just Runs
        every { mockNavActions.goBack() } just Runs

        composeTestRule.setContent {
            QrScanScreen(mockNavActions)
        }
    }

    @Test
    fun textIsDisplayed() {
        onComposeScreen<QrScanScreen>(composeTestRule){
            text {
                assertIsDisplayed()
                hasText("QrScanScreen")
            }
        }
    }

    @Test
    fun backButton() {
        onComposeScreen<QrScanScreen>(composeTestRule){
            backButton {
                assertIsDisplayed()
                performClick()
            }
        }
    }
}
