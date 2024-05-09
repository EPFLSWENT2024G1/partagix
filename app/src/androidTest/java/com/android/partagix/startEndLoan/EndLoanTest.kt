package com.android.partagix.startEndLoan

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.ManagerUIState
import com.android.partagix.model.StampViewModel
import com.android.partagix.model.StartOrEndLoanUIState
import com.android.partagix.model.StartOrEndLoanViewModel
import com.android.partagix.model.emptyConst.emptyItem
import com.android.partagix.model.emptyConst.emptyLoan
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.screens.EndLoanScreen
import com.android.partagix.screens.StampScreen
import com.android.partagix.screens.StartLoanScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.screens.EndLoanScreen
import com.android.partagix.ui.screens.StampScreen
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
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndLoanTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockStartOrEndLoanViewModel: StartOrEndLoanViewModel
  @RelaxedMockK lateinit var mockStartOrEndLoanUiState: MutableStateFlow<StartOrEndLoanUIState>

  @Before
  fun testSetup() {
    mockStartOrEndLoanUiState =
      MutableStateFlow(
        StartOrEndLoanUIState(
          emptyLoan,
          emptyItem,
          emptyUser,
          emptyUser
        )
      )

    mockStartOrEndLoanViewModel = mockk()
    every { mockStartOrEndLoanViewModel.uiState } returns mockStartOrEndLoanUiState
    every { mockStartOrEndLoanViewModel.onFinish() } just Runs


    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.goBack() } just Runs

    composeTestRule.setContent {
      EndLoanScreen(mockStartOrEndLoanViewModel, mockNavActions)
    }
  }

  @Test
  fun contentIsDisplayed() = run {
    onComposeScreen<EndLoanScreen>(composeTestRule) {
      item { assertIsDisplayed()}
      endLoanButton { assertIsDisplayed() }
    }
  }

  @Test
  fun buttonAction() = run {
    onComposeScreen<EndLoanScreen>(composeTestRule) {
      endLoanButton { performClick() }
      coVerify { mockStartOrEndLoanViewModel.onFinish() }


    }
  }
}
