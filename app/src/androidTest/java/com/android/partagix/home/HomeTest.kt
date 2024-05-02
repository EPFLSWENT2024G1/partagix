package com.android.partagix.home

import android.location.Location
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.HomeViewModel
import com.android.partagix.model.InventoryUIState
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.screens.HomeScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.HomeScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
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
class HomeTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockInventoryViewModel: InventoryViewModel
  @RelaxedMockK lateinit var mockHomeViewModel: HomeViewModel

  private lateinit var mockUiState: MutableStateFlow<InventoryUIState>

  @Before
  fun testSetup() {
    val cat1 = Category("1", "Category 1")
    val vis1 = com.android.partagix.model.visibility.Visibility.PUBLIC
    val loc1 = Location("1")
    val items = listOf(Item("1", cat1, "Name 1", "Description 1", vis1, 1, loc1))
    mockUiState =
        MutableStateFlow(
            InventoryUIState(items, "", items, emptyList(), emptyList(), emptyList(), emptyList()))

    mockInventoryViewModel = mockk()
    mockHomeViewModel = mockk()

    every { mockInventoryViewModel.getInventory() } just Runs

    every { mockInventoryViewModel.findtime(any(), any()) } just Runs
    every { mockInventoryViewModel.getusers(any(), any()) } just Runs

    every { mockInventoryViewModel.filterItems(query = any()) } just Runs
    every { mockInventoryViewModel.filterItems(atLeastQuantity = any()) } just Runs
    every { mockInventoryViewModel.filterItems(currentPosition = any(), radius = any()) } just Runs

    every { mockInventoryViewModel.uiState } returns mockUiState

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs
    every { mockNavActions.navigateTo(Route.INVENTORY) } just Runs
    every { mockNavActions.navigateTo(Route.QR_SCAN) } just Runs
    every { mockNavActions.navigateTo(Route.LOAN) } just Runs

    composeTestRule.setContent {
      HomeScreen(
          homeViewModel = mockHomeViewModel,
          inventoryViewModel = mockInventoryViewModel,
          navigationActions = mockNavActions)
    }
  }

  @Test
  fun contentIsDisplayed() = run {
    ComposeScreen.onComposeScreen<HomeScreen>(composeTestRule) {
      topBar { assertIsDisplayed() }
      mainContent { assertIsDisplayed() }
      bottomNavBar { assertIsDisplayed() }
      bottomNavBarItemInventory { assertIsDisplayed() }
      firstBigButton {
        assertIsDisplayed()
        performClick()
      }
      secondBigButton {
        assertIsDisplayed()
        performClick()
      }
      thirdBigButton {
        assertIsDisplayed()
        performClick()
      }
      performClick()
    }
  }
}
