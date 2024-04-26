package com.android.partagix.stamp

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextRange
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.StampViewModel
import com.android.partagix.screens.StampScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.screens.StampScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.Awaits
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

    composeTestRule.setContent { StampScreen(Modifier, mockStampViewModel, "123456",  mockNavActions) }
  }

  @Test fun testTest() = run { assert(true) }

  //    scaffold
  @Test
  fun stampScreenIsDisplayed() = run {
    onComposeScreen<StampScreen>(composeTestRule) { stampScreen { assertIsDisplayed() } }
  }

  //    topAppBar
  @Test
  fun topAppBarIsDisplayed() = run {
    onComposeScreen<StampScreen>(composeTestRule) { topAppBar { assertIsDisplayed() } }
  }

  @Test
  fun topAppBarWorks() = run {
    onComposeScreen<StampScreen>(composeTestRule) {
      topAppBar { assertIsDisplayed() }
      title { assertIsDisplayed() }
      backButton { assertIsDisplayed() }
      backButton { performClick() }
    }
  }

  //    mainContent
  @Test
  fun mainContentIsDisplayed() = run {
    onComposeScreen<StampScreen>(composeTestRule) { mainContent { assertIsDisplayed() } }
  }

  //    dimensionLabel
  @Test
  fun dimensionLabelIsDisplayed() = run {
    onComposeScreen<StampScreen>(composeTestRule) { dimensionLabel { assertIsDisplayed() } }
  }

  //    dimensionBox
  @Test
  fun dimensionBoxIsDisplayed() = run {
    onComposeScreen<StampScreen>(composeTestRule) { dimensionBox { assertIsDisplayed() } }
  }

  //    labelLabel
  @Test
  fun labelLabelIsDisplayed() = run {
    onComposeScreen<StampScreen>(composeTestRule) { labelLabel { assertIsDisplayed() } }
  }

  //    labelTextField
  @Test
  fun labelTextFieldIsDisplayed() = run {
    onComposeScreen<StampScreen>(composeTestRule) { labelTextField { assertIsDisplayed() } }
  }

  @OptIn(ExperimentalTestApi::class)
  fun labelTextFieldWorks() = run {
    onComposeScreen<StampScreen>(composeTestRule) {
      labelTextField {
        assertIsDisplayed()
        performTextInput(
            "this_is_a_long_text_this_is_a_long_text_this_is_a_long_text_this_is_a_long_text_this_is_a_long_text_this_is_a_long_text_this_is_a_long_text_this_is_a_long_text_this_is_a_long_text_this_is_a_long_text_")
      }
      performTextInputSelection(TextRange(0, 40))
    }
  }

  //    downloadRow
  @Test
  fun downloadRowIsDisplayed() = run {
    onComposeScreen<StampScreen>(composeTestRule) { downloadRow { assertIsDisplayed() } }
  }

  //    downloadButton
  @Test
  fun downloadButtonIsDisplayed() = run {
    onComposeScreen<StampScreen>(composeTestRule) { downloadButton { assertIsDisplayed() } }
  }

  @Test
  fun downloadButtonWorks() = run {
    onComposeScreen<StampScreen>(composeTestRule) {
      downloadButton { assertIsDisplayed() }
      downloadButton { performClick() }
      coVerify(exactly = 1) { mockStampViewModel.generateQRCodeAndSave("123456", "", any()) }
    }
  }
}
