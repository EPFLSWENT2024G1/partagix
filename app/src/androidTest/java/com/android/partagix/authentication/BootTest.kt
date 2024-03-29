package com.android.partagix.authentication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.auth.Authentication
import com.android.partagix.screen.BootScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.BootScreen
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BootTest {
  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var authentication: Authentication
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun setup() {
    authentication = mockk<Authentication>()
    mockNavActions = mockk<NavigationActions>()

    composeTestRule.setContent { BootScreen(authentication, mockNavActions) }

    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs
    every { authentication.isAlreadySignedIn() } returns true
  }

  @Test
  fun logoIsDisplayed() {
    ComposeScreen.onComposeScreen<BootScreen>(composeTestRule) { bootLogo { assertIsDisplayed() } }
  }

  @Test
  fun goToHomeIFSignedIn() {
    clearMocks(authentication)
    every { authentication.isAlreadySignedIn() } returns true

    ComposeScreen.onComposeScreen<BootScreen>(composeTestRule) { bootLogo { assertIsDisplayed() } }

    Thread.sleep(SLEEP_TIME)

    verify { mockNavActions.navigateTo(Route.HOME) }
  }

  @Test
  fun goToLoginIFNotSignedIn() {
    clearMocks(authentication)
    every { authentication.isAlreadySignedIn() } returns false

    ComposeScreen.onComposeScreen<BootScreen>(composeTestRule) { bootLogo { assertIsDisplayed() } }

    Thread.sleep(SLEEP_TIME)

    verify { mockNavActions.navigateTo(Route.LOGIN) }
  }

  companion object {
    const val SLEEP_TIME = 2000L
  }
}
