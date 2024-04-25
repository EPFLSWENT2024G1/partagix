package com.android.partagix.stamp

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.StampViewModel
import com.android.partagix.screens.StampScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.StampScreen
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
class StampTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockStampViewModel: StampViewModel

  @Before
  fun testSetup() {

    mockStampViewModel = mockk()

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs
    every { mockNavActions.navigateTo(Route.ACCOUNT) } just Runs
    every { mockNavActions.navigateTo(Route.INVENTORY) } just Runs
    every { mockNavActions.navigateTo(Route.LOAN) } just Runs
    every { mockNavActions.navigateTo(Route.BOOT) } just Runs
    every { mockNavActions.navigateTo(Route.VIEW_ITEM) } just Runs
    every { mockNavActions.navigateTo(Route.EDIT_ITEM) } just Runs
    every { mockNavActions.navigateTo(Route.CREATE_ITEM) } just Runs
    every { mockNavActions.navigateTo(Route.STAMP) } just Runs
    every { mockNavActions.goBack() } just Runs

    composeTestRule.setContent { StampScreen(Modifier, mockStampViewModel, mockNavActions) }
  }

  @Test fun testTest() = run { assert(true) }

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
}
