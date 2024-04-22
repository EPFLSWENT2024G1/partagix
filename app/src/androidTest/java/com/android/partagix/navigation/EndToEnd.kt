package com.android.partagix.navigation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.screens.InventoryCreateOrEditScreen
import com.android.partagix.screens.InventoryScreen
import com.android.partagix.screens.NavigationBar
import com.android.partagix.screens.ViewAccount
import com.android.partagix.ui.MainActivity
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEnd {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val composeTestRule2 = createComposeRule()

  @Test
  fun NavigationInventory() {
    val scenario = ActivityScenario.launch(MainActivity::class.java)

    scenario.onActivity { mainActivity ->
      // Call functions on MainActivity instance here
      mainActivity.myInitializationFunction()
    }

    // Wait for the activity to be in the resumed state
    scenario.moveToState(Lifecycle.State.RESUMED)

    // check that the bottom bar is well displayed
    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {
      homeButton { assertIsDisplayed() }
      loanButton { assertIsDisplayed() }
      inventoryButton { assertIsDisplayed() }
      accountButton { assertIsDisplayed() }

      inventoryButton { performClick() }
    }

    // click to create a new item
    ComposeScreen.onComposeScreen<InventoryScreen>(composeTestRule) {
      fab { assertIsDisplayed() }
      fab { performClick() }
    }

    // check that the create item screen is well displayed and that you can use the fields
    ComposeScreen.onComposeScreen<InventoryCreateOrEditScreen>(composeTestRule) {
      button { assertIsDisplayed() }
      button { assertTextContains("Create") }
      topBar { assertIsDisplayed() }
      name { performTextInput("Grinch") }
      description { performTextInput("Christmas Tree") }

      category { performClick() }
      composeTestRule.onNodeWithTag("Category 1").performClick()
      composeTestRule.onNodeWithText("Category 1").assertExists()
      category { performClick() }
      composeTestRule.onNodeWithTag("Category 3").performClick()
      composeTestRule.onNodeWithText("Category 3").assertExists()

      visibility { performClick() }
      composeTestRule.onNodeWithTag("Friends").performClick()
      composeTestRule.onNodeWithText("Friends").assertExists()
      visibility { performClick() }
      composeTestRule.onNodeWithTag("Private").performClick()
      composeTestRule.onNodeWithText("Private").assertExists()

      quantity { performTextReplacement("10") }
      navigationIcon { performClick() }
    }

    // check we are indeed in the inventory screen
    ComposeScreen.onComposeScreen<InventoryScreen>(composeTestRule) {
      noItemBox { assertIsDisplayed() }
      noItemText { assertIsDisplayed() }
    }

    // go on the account screen
    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {
      accountButton { performClick() }
    }

    // check we are indeed in the account screen and go to inventory via inventory button
    ComposeScreen.onComposeScreen<ViewAccount>(composeTestRule) {
      viewAccount { assertIsDisplayed() }
      inventoryButton { performClick() }
    }

    // check we are indeed in the inventory screen
    ComposeScreen.onComposeScreen<InventoryScreen>(composeTestRule) {
      noItemBox { assertIsDisplayed() }
      noItemText { assertIsDisplayed() }
    }

    // go to the account screen
    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {
      accountButton { performClick() }
    }

    // check we are indeed in the account screen and go to inventory via go back button
    ComposeScreen.onComposeScreen<ViewAccount>(composeTestRule) {
      viewAccount { assertIsDisplayed() }
      backButton { performClick() }
    }

    // go to the home screen
    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {
      // faudrait accepter de base la loca map sinon ça fait crash
      // loanButton { performClick() }
      homeButton { performClick() }
    }

    /*TODO: add the navigation to the loan screen when it's pushed on the main with its tests*/

    // Close the activity after the test
    scenario.close()
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
