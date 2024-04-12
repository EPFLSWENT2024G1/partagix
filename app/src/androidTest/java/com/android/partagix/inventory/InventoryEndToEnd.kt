package com.android.partagix.inventory

import android.location.Location
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.InventoryUIState
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.screens.InventoryScreen
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
class InventoryEndToEnd {

  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockInventoryViewModel: InventoryViewModel

  private val i = Item("1", Category("1","cat"), "name", "description", Visibility.PUBLIC, 1, Location(""), "")
  private val mockUiState = MutableStateFlow(InventoryUIState(List(1) { i }, ""))

  @Before
  fun testSetup() {
    mockInventoryViewModel = mockk()
    every { mockInventoryViewModel.uiState } returns mockUiState
    every { mockInventoryViewModel.getInventory() } just Runs

    mockNavActions = mockk<NavigationActions>()
  }

  @Test
  fun testNavigateToCreateItem() {
    // Launch the inventory screen
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }

    // Click on the "Create" floating action button
    composeTestRule.onNodeWithTag("CreateFloatingButton").performClick()

    // Verify navigation to the create item screen
    composeTestRule.onNodeWithTag("CreateScreen").assertExists()
  }

  @Test
  fun testNavigateToViewItem() {
    // Launch the inventory screen
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }

    // Click on the first item in the inventory list
    composeTestRule.onNodeWithTag("inventoryItem_0").performClick()

    // Verify navigation to the view item screen
    composeTestRule.onNodeWithTag("ViewItemScreen").assertExists()
  }

  @Test
  fun testNavigateToEditItem() {
    // Launch the inventory screen
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }

    // Click on the first item in the inventory list
    composeTestRule.onNodeWithTag("inventoryItem_0").performClick()

    // Verify navigation to the view item screen
    composeTestRule.onNodeWithTag("ViewItemScreen").assertExists()

    // Click on the "Edit" button
    composeTestRule.onNodeWithTag("editItemButton").performClick()

    // Verify navigation to the edit item screen
    composeTestRule.onNodeWithTag("EditScreen").assertExists()
  }

  @Test
  fun testGoBack() {
    // Launch the inventory screen
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }

    // Click on the "Create" floating action button
    composeTestRule.onNodeWithTag("CreateFloatingButton").performClick()

    // Verify navigation to the create item screen
    composeTestRule.onNodeWithTag("CreateScreen").assertExists()

    // Click on the back button
    composeTestRule.onNodeWithTag("goBack").performClick()

    // Verify navigation back to the inventory screen
    composeTestRule.onNodeWithTag("inventoryScreen").assertExists()

    // Click on the first item in the inventory list
    composeTestRule.onNodeWithTag("inventoryItem_0").performClick()

    // Verify navigation to the view item screen
    composeTestRule.onNodeWithTag("ViewItemScreen").assertExists()

    // Click on the "Edit" button
    composeTestRule.onNodeWithTag("editItemButton").performClick()

    // Verify navigation to the edit item screen
    composeTestRule.onNodeWithTag("EditScreen").assertExists()

    // Click on the back button
    composeTestRule.onNodeWithTag("goBack").performClick()

    // Verify navigation to the view item screen
    composeTestRule.onNodeWithTag("ViewItemScreen").assertExists()

    // Click on the back button
    composeTestRule.onNodeWithTag("goBack").performClick()

    // Verify navigation to the inventory screen
    composeTestRule.onNodeWithTag("inventoryScreen").assertExists()
  }
}
