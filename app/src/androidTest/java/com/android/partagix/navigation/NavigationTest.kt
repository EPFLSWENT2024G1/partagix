package com.android.partagix.authentication

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.auth.Authentication
import com.android.partagix.screens.NavigationBar
import com.android.partagix.ui.App
import com.android.partagix.ui.MainActivity
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {
  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var authentication: Authentication

  @Before
  fun setup() {
    authentication = mockk<Authentication>()
    val mockMain = mockk<MainActivity>()
    val mockResult: ActivityResultLauncher<Intent> = mockk()

    every { authentication.isAlreadySignedIn() } returns true

    every { mockMain.
    registerForActivityResult(
      any<FirebaseAuthUIActivityResultContract>(), any()
    )
    } returns mockResult
    


    composeTestRule.setContent { App(mockMain, authentication) }
  }

  @Test
  fun basicDisplay() {
    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {

    }
  }
  @Test
  fun testNavigation() {
    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {
      loanButton { performClick() }
    }
    //verify { mockNavActions.navigateTo(Route.LOAN) }
  }

  companion object {
    const val SLEEP_TIME = 2000L
  }
}
