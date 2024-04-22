package com.android.partagix.inventory

import android.location.Location
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.R
import com.android.partagix.model.InventoryUIState
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.ItemUIState
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.screens.InventoryCreateOrEditScreen
import com.android.partagix.screens.InventoryScreen
import com.android.partagix.screens.NavigationBar
import com.android.partagix.ui.MainActivity
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.screens.InventoryScreen
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
import java.util.regex.Pattern.matches

@RunWith(AndroidJUnit4::class)
class InventoryEndToEnd {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val composeTestRule2 = createComposeRule()

  @Test
  fun NavigationInventory(){
    val scenario = ActivityScenario.launch(MainActivity::class.java)

    scenario.onActivity { mainActivity ->
      // Call functions on MainActivity instance here
      mainActivity.myInitializationFunction()
    }

    // Wait for the activity to be in the resumed state
    scenario.moveToState(Lifecycle.State.RESUMED)

    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {
      homeButton { assertIsDisplayed() }
      loanButton { assertIsDisplayed() }
      inventoryButton { assertIsDisplayed() }
      accountButton { assertIsDisplayed() }

      inventoryButton { performClick() }
    }

    ComposeScreen.onComposeScreen<InventoryScreen>(composeTestRule){
      fab {assertIsDisplayed()}
      fab {performClick()}
    }

    ComposeScreen.onComposeScreen<InventoryCreateOrEditScreen>(composeTestRule){
      button{assertIsDisplayed()}
      button{assertTextContains("Create")}
      topBar{assertIsDisplayed()}
      navigationIcon{performClick()}
    }

    ComposeScreen.onComposeScreen<InventoryScreen>(composeTestRule){
      //itemList {assertIsDisplayed()}
      //itemList {performClick()}
    }
  }

  /*@get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockInventoryViewModel: InventoryViewModel
  @RelaxedMockK lateinit var mockItemViewModel: ItemViewModel

  private val i = Item("1", Category("1","cat"), "name", "description", Visibility.PUBLIC, 1, Location(""), "")
  private val iempty = Item("", Category("", ""), "", "", Visibility.PUBLIC, 1, Location(""), "")
  private val mockUiState = MutableStateFlow(InventoryUIState(List(1) { i }, "", emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
  private val mockUiState2 = MutableStateFlow(ItemUIState(i))
  private val mockUiState2empty = MutableStateFlow(ItemUIState(iempty))

  @Before
  fun testSetup() {
    mockInventoryViewModel = mockk()
    every { mockInventoryViewModel.uiState } returns mockUiState
    every { mockInventoryViewModel.getInventory() } just Runs

      mockItemViewModel = mockk()
      every { mockItemViewModel.uiState } returns mockUiState2
      every { mockItemViewModel.updateUiState(i) } just Runs
      every { mockItemViewModel.updateUiState(iempty) } just Runs
      every { mockItemViewModel.save(i) } just Runs
      every { mockItemViewModel.save(iempty) } just Runs


    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo("CreateItem") } just Runs
    every { mockNavActions.navigateTo("ViewItem") } just Runs
    every { mockNavActions.navigateTo("EditItem") } just Runs

  }



  @Test
  fun testNavigateToCreateItem() {
    // Launch the inventory screen
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions ,mockItemViewModel)
    }

    // Click on the "Create" floating action button
    composeTestRule.onNodeWithTag("inventoryScreenFab").performClick()

    // Verify navigation to the create item screen
    composeTestRule.onNodeWithTag("inventoryCreateItem").assertExists()
  }

  @Test
  fun testNavigateToViewItem() {
    // Launch the inventory screen
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions, mockItemViewModel)
    }

    // Click on the first item in the inventory list
    composeTestRule.onNodeWithTag("inventoryScreenItemList").performClick()

    // Verify navigation to the view item screen
    composeTestRule.onNodeWithTag("inventoryViewItem").assertExists()
  }

  @Test
  fun testNavigateToEditItem() {
    // Launch the inventory screen
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions, mockItemViewModel)
    }

    // Click on the first item in the inventory list
    composeTestRule.onNodeWithTag("inventoryScreenItemList").performClick()

    // Verify navigation to the view item screen
    composeTestRule.onNodeWithTag("inventoryViewItem").assertExists()

    // Click on the "Edit" button
    composeTestRule.onNodeWithTag("editItemButton").performClick()

    // Verify navigation to the edit item screen
    composeTestRule.onNodeWithTag("inventoryCreateItem").assertExists()
  }

  @Test
  fun testGoBack() {
    // Launch the inventory screen
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions, mockItemViewModel)
    }

    // Click on the "Create" floating action button
    composeTestRule.onNodeWithTag("inventoryScreenFab").performClick()

    // Verify navigation to the create item screen
    composeTestRule.onNodeWithTag("inventoryCreateItem").assertExists()

    // Click on the back button
    composeTestRule.onNodeWithTag("navigationIcon").performClick()

    // Verify navigation back to the inventory screen
    composeTestRule.onNodeWithTag("inventoryViewItem").assertExists()

    // Click on the first item in the inventory list
    composeTestRule.onNodeWithTag("inventoryScreenItemList").performClick()

    // Verify navigation to the view item screen
    composeTestRule.onNodeWithTag("inventoryViewItem").assertExists()

    // Click on the "Edit" button
    composeTestRule.onNodeWithTag("editItemButton").performClick()

    // Verify navigation to the edit item screen
    composeTestRule.onNodeWithTag("inventoryCreateItem").assertExists()

    // Click on the back button
    composeTestRule.onNodeWithTag("navigationIcon").performClick()

    // Verify navigation to the view item screen
    composeTestRule.onNodeWithTag("inventoryViewItem").assertExists()

    // Click on the back button
    composeTestRule.onNodeWithTag("navigationIcon").performClick()

    // Verify navigation to the inventory screen
    composeTestRule.onNodeWithTag("inventoryScreen").assertExists()
  }*/
}
