package com.android.partagix.startEndLoan

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.StartOrEndLoanUIState
import com.android.partagix.model.StartOrEndLoanViewModel
import com.android.partagix.model.emptyConst.emptyItem
import com.android.partagix.model.emptyConst.emptyLoan
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.screens.StartLoanScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.StartLoanScreen
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
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartLoanTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockStartOrEndLoanViewModel: StartOrEndLoanViewModel
  @RelaxedMockK lateinit var mockStartOrEndLoanUiState: MutableStateFlow<StartOrEndLoanUIState>
  @RelaxedMockK lateinit var mockItemViewModel: ItemViewModel

  @Before
  fun testSetup() {
    mockStartOrEndLoanUiState =
        MutableStateFlow(StartOrEndLoanUIState(emptyLoan, emptyItem, emptyUser, emptyUser))

    mockStartOrEndLoanViewModel = mockk()
    every { mockStartOrEndLoanViewModel.uiState } returns mockStartOrEndLoanUiState
    every { mockStartOrEndLoanViewModel.onStart() } just Runs
    every { mockStartOrEndLoanViewModel.onCancel() } just Runs

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.goBack() } just Runs

    mockItemViewModel = mockk()
    every { mockItemViewModel.updateUiItem(emptyItem) } just Runs
    every { mockNavActions.navigateTo(Route.VIEW_ITEM) } just Runs
    every { mockNavActions.navigateTo(Route.INVENTORY) } just Runs

    composeTestRule.setContent {
      StartLoanScreen(mockStartOrEndLoanViewModel, mockNavActions, mockItemViewModel)
    }
  }

  @Test
  fun contentIsDisplayed() = run {
    onComposeScreen<StartLoanScreen>(composeTestRule) {
      title { assertIsDisplayed() }
      item { assertIsDisplayed() }
      startButton { assertIsDisplayed() }
      cancelButton { assertIsDisplayed() }
      close { assertIsDisplayed() }
    }
  }

  @Test
  fun startButton() = run {
    onComposeScreen<StartLoanScreen>(composeTestRule) {
      startButton { performClick() }
      coVerify { mockStartOrEndLoanViewModel.onStart() }
      popUp { assertDoesNotExist() }
    }
  }

  @Test
  fun cancelButton() = run {
    onComposeScreen<StartLoanScreen>(composeTestRule) {
      cancelButton { performClick() }
      coVerify { mockStartOrEndLoanViewModel.onCancel() }
      popUp { assertDoesNotExist() }
    }
  }

  @Test
  fun clickOnItem() = run {
    onComposeScreen<StartLoanScreen>(composeTestRule) {
      item { performClick() }
      coVerify { mockItemViewModel.updateUiItem(emptyItem) }
      coVerify { mockNavActions.navigateTo(Route.VIEW_ITEM) }
      popUp { assertDoesNotExist() }
    }
  }

  @Test
  fun closeButton() = run {
    onComposeScreen<StartLoanScreen>(composeTestRule) {
      close { performClick() }
      popUp { assertDoesNotExist() }
    }
  }
}
