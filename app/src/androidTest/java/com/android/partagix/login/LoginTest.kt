package com.android.partagix.authentication

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.auth.Authentication
import com.android.partagix.screens.LoginScreen
import com.android.partagix.screens.LoginScreen2
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.LoginScreen
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
class LoginTest {
  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var authentication: Authentication
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun setup() {
    authentication = mockk<Authentication>()
    mockNavActions = mockk<NavigationActions>()

    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs
    every { authentication.isAlreadySignedIn() } returns true

    composeTestRule.setContent { LoginScreen(authentication) }
  }

  @Test
  fun basicDisplayed() {
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      loginTitle { assertIsDisplayed() }

      popUpLoginButton {
        assertIsDisplayed()
        performClick()
      }
    }
    ComposeScreen.onComposeScreen<LoginScreen2>(composeTestRule) {
      loginButton { assertIsDisplayed() }
    }
  }

  companion object {
    const val SLEEP_TIME = 2000L
  }
}
