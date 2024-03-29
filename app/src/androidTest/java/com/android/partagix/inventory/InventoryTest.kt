package com.android.partagix.inventory

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.InventoryUIState
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.screens.InventoryScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.InventoryScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.Runs
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
class InventoryTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockInventoryViewModel: InventoryViewModel

  private val mockUiState = MutableStateFlow(InventoryUIState(emptyList()))

  @Before
  fun testSetup() {
    mockInventoryViewModel = mockk()
    every { mockInventoryViewModel.uiState } returns mockUiState

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs

    composeTestRule.setContent { InventoryScreen(mockInventoryViewModel, mockNavActions) }
  }

  @Test
  fun centerTextIsDisplayed() = run {
    onComposeScreen<InventoryScreen>(composeTestRule) {
      mainContentText {
        assertIsDisplayed()
        assertTextContains(value = "There is", substring = true, ignoreCase = true)
      }
    }
  }

  @Test
  fun bottomNavBarIsDisplayed() = run {
    onComposeScreen<InventoryScreen>(composeTestRule) { bottomNavBar { assertIsDisplayed() } }
  }

  @Test
    fun bottomNavBarItemInventoryIsDisplayed() = run {
        onComposeScreen<InventoryScreen>(composeTestRule) {
        bottomNavBarItemInventory {
            assertIsDisplayed()
        }
        }
    }
}
