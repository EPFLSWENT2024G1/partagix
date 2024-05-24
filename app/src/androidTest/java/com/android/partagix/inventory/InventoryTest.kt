package com.android.partagix.inventory

import android.location.Location
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.InventoryUIState
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
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
  @RelaxedMockK lateinit var mockManageLoanViewModelIncoming: ManageLoanViewModel
  @RelaxedMockK lateinit var mockManageLoanViewModelOutgoing: ManageLoanViewModel
  @RelaxedMockK lateinit var mockItemViewModel: ItemViewModel

  private lateinit var emptyMockUiState: MutableStateFlow<InventoryUIState>
  private lateinit var nonEmptyMockUiState: MutableStateFlow<InventoryUIState>

  @Before
  fun testSetup() {
    emptyMockUiState =
        MutableStateFlow(
            InventoryUIState(
                emptyList(), "", emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
    val cat1 = Category("1", "Category 1")
    val vis1 = Visibility.PUBLIC
    val loc1 = Location("1")
    val items = listOf(Item("1", cat1, "Name 1", "Description 1", vis1, 1, loc1))

    nonEmptyMockUiState =
        MutableStateFlow(
            InventoryUIState(items, "", items, emptyList(), emptyList(), emptyList(), emptyList()))

    mockItemViewModel = mockk()
    mockManageLoanViewModelIncoming = mockk()
    mockManageLoanViewModelOutgoing = mockk()
    mockInventoryViewModel = mockk()

    every { mockManageLoanViewModelIncoming.getCount() } returns 0
    every { mockManageLoanViewModelOutgoing.getCount() } returns 0

    every { mockInventoryViewModel.getInventory(any()) } just Runs
    every { mockInventoryViewModel.findTime(any(), any(), any()) } just Runs
    every { mockInventoryViewModel.getUsers(any(), any()) } just Runs

    every { mockInventoryViewModel.filterItems(query = any()) } just Runs
    every { mockInventoryViewModel.filterItems(atLeastQuantity = any()) } just Runs

    every { mockItemViewModel.updateUiItem(any()) } just Runs

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs

    // Useful when we will have fix the count for incoming/outgoing request
    /* every { mockManageLoanViewModel.getInComingRequestCount(any()) } just Runs
    every { mockManageLoanViewModel.getOutGoingRequestCount(any()) } just Runs*/
  }

  @Test
  fun contentIsDisplayed() = run {
    every { mockInventoryViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryScreen(
          mockInventoryViewModel,
          mockNavActions,
          mockManageLoanViewModelOutgoing,
          mockManageLoanViewModelIncoming,
          mockItemViewModel)
    }

    onComposeScreen<InventoryScreen>(composeTestRule) {
      fab { assertIsDisplayed() }
      noItemBox { assertIsDisplayed() }
      noItemText {
        assertIsDisplayed()
        assertTextEquals(
            "There is no items in your inventory, click on the + button to add your first item")
      }
      bottomNavBar { assertIsDisplayed() }
      bottomNavBarItemInventory { assertIsDisplayed() }
    }
  }

  @Test
  fun itemListIsDisplayed() = run {
    every { mockInventoryViewModel.uiState } returns nonEmptyMockUiState
    composeTestRule.setContent {
      InventoryScreen(
          mockInventoryViewModel,
          mockNavActions,
          mockManageLoanViewModelOutgoing,
          mockManageLoanViewModelIncoming,
          mockItemViewModel)
    }

    onComposeScreen<InventoryScreen>(composeTestRule) {
      itemList { assertIsDisplayed() }
      borrowedItemList { assertIsDisplayed() }
    }
  }
}
