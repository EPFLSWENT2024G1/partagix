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
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEnd {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val composeTestRule2 = createComposeRule()

  @Test
  fun NavigationBetweenScreens() {
    val scenario = ActivityScenario.launch(MainActivity::class.java)

    scenario.onActivity { mainActivity ->
      // Call functions on MainActivity instance here
      mainActivity.myInitializationFunction("Home")
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
}
