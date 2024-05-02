package com.android.partagix.stamp

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.StampViewModel
import com.android.partagix.screens.StampScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.screens.StampScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StampTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockStampViewModel: StampViewModel

  @Before
  fun testSetup() {

    mockStampViewModel = mockk()
    every { mockStampViewModel.generateQRCodeAndSave(any(), any(), any()) } returns Unit

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.goBack() } just Runs

    composeTestRule.setContent {
      StampScreen(Modifier, mockStampViewModel, "123456", mockNavActions)
    }
  }

  @Test
  fun contentIsDisplayed() = run {
    onComposeScreen<StampScreen>(composeTestRule) {
      stampScreen { assertIsDisplayed() }
      topAppBar { assertIsDisplayed() }
      mainContent { assertIsDisplayed() }
      dimensionLabel { assertIsDisplayed() }
      dimensionBox { assertIsDisplayed() }
      labelLabel { assertIsDisplayed() }
      labelTextField { assertIsDisplayed() }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun componentsWorks() = run {
    onComposeScreen<StampScreen>(composeTestRule) {
      // top bar
      topAppBar { assertIsDisplayed() }
      title { assertIsDisplayed() }
      backButton { assertIsDisplayed() }
      backButton { performClick() }
      downloadRow { assertIsDisplayed() }
      downloadButton { assertIsDisplayed() }

      // download button
      downloadButton { assertIsDisplayed() }
      downloadButton { performClick() }
      coVerify(exactly = 1) { mockStampViewModel.generateQRCodeAndSave("123456", "", any()) }

      // label text field
      labelTextField { assertIsDisplayed() }
      labelTextField { performTextInput("label of QR code") }
      composeTestRule.onNodeWithText("label of QR code").assertExists()
    }
  }
}
