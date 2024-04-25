package com.android.partagix.navigation

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
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
    val timeWait: Long = 100000
    val scenario = ActivityScenario.launch(MainActivity::class.java)

    scenario.onActivity { mainActivity ->
      // Call functions on MainActivity instance here
      mainActivity.myInitializationFunction("Home")
    }

    // Wait for the activity to be in the resumed state
    scenario.moveToState(Lifecycle.State.RESUMED)

    composeTestRule.waitUntil(timeWait) { composeTestRule.onNodeWithText("Home").isDisplayed() }
    // check that the bottom bar is well displayed
    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {
      homeButton { assertIsDisplayed() }
      loanButton { assertIsDisplayed() }
      inventoryButton { assertIsDisplayed() }
      accountButton { assertIsDisplayed() }

      inventoryButton { performClick() }
    }

    composeTestRule.waitUntil(timeWait + 1) {
      composeTestRule.onNodeWithTag("inventoryScreenFab").isDisplayed()
    }
    // click to create a new item
    ComposeScreen.onComposeScreen<InventoryScreen>(composeTestRule) {
      fab { assertIsDisplayed() }
      fab { performClick() }
    }

    composeTestRule.waitUntil(timeWait + 2) {
      composeTestRule.onNodeWithTag("inventoryCreateItem").isDisplayed()
    }
    composeTestRule.onNodeWithText("Create").performScrollTo()
    composeTestRule.waitUntil(timeWait + 9) {
      composeTestRule.onNodeWithText("Create").isDisplayed()
    }
    // check that the create item screen is well displayed and that you can use the fields
    ComposeScreen.onComposeScreen<InventoryCreateOrEditScreen>(composeTestRule) {
      button { assertIsDisplayed() }
      button { assertTextContains("Create") }
      topBar { assertIsDisplayed() }

      composeTestRule.onNodeWithTag("category").performScrollTo()
      category { performClick() }
      category { performClick() }
      composeTestRule.waitUntil(timeWait + 3) {
        composeTestRule.onNodeWithTag("Category 1").isDisplayed()
      }
      composeTestRule.onNodeWithTag("Category 1").performClick()
      composeTestRule.onNodeWithText("Category 1").assertExists()

      category { performClick() }
      composeTestRule.waitUntil(timeWait + 4) {
        composeTestRule.onNodeWithTag("Category 3").isDisplayed()
      }
      composeTestRule.onNodeWithTag("Category 3").performClick()
      composeTestRule.onNodeWithText("Category 3").assertExists()

      visibility { performClick() }
      composeTestRule.waitUntil(timeWait + 5) {
        composeTestRule.onNodeWithTag("Friends").isDisplayed()
      }
      composeTestRule.onNodeWithTag("Friends").performClick()
      composeTestRule.onNodeWithText("Friends").assertExists()

      visibility { performClick() }
      composeTestRule.waitUntil(timeWait + 6) {
        composeTestRule.onNodeWithTag("Private").isDisplayed()
      }
      composeTestRule.onNodeWithTag("Private").performClick()
      composeTestRule.onNodeWithText("Private").assertExists()

      name { performTextInput("Grinch") }

      description { performTextInput("Christmas Tree") }
      Espresso.closeSoftKeyboard()

      quantity { performScrollTo() }
      quantity { performTextReplacement("10") }
      navigationIcon { performClick() }
    }

    composeTestRule.waitUntil(timeWait + 7) {
      composeTestRule.onNodeWithTag("inventoryScreenNoItemBox").isDisplayed()
    }
    composeTestRule.waitUntil(timeWait + 8) {
      composeTestRule.onNodeWithTag("inventoryScreenFab").isDisplayed()
    }

    // check we are indeed in the inventory screen
    ComposeScreen.onComposeScreen<InventoryScreen>(composeTestRule) {
      noItemBox { assertIsDisplayed() }
      noItemText { assertIsDisplayed() }
    }

    // Close the activity after the test
    scenario.close()
  }

  @Test
  fun NavigationAccount() {
    val timeWait: Long = 10000
    val scenario = ActivityScenario.launch(MainActivity::class.java)

    scenario.onActivity { mainActivity ->
      // Call functions on MainActivity instance here
      mainActivity.myInitializationFunction("Account")
    }

    // Wait for the activity to be in the resumed state
    scenario.moveToState(Lifecycle.State.RESUMED)

    composeTestRule.waitUntil(timeWait) {
      composeTestRule.onNodeWithTag("viewAccount").isDisplayed()
    }

    ComposeScreen.onComposeScreen<ViewAccount>(composeTestRule) {
      viewAccount { assertIsDisplayed() }
      inventoryButton { performClick() }
    }

    composeTestRule.waitUntil(timeWait) {
      composeTestRule.onNodeWithTag("inventoryScreenNoItemBox").isDisplayed()
    }
    composeTestRule.waitUntil(timeWait) {
      composeTestRule.onNodeWithTag("inventoryScreenFab").isDisplayed()
    }

    ComposeScreen.onComposeScreen<InventoryScreen>(composeTestRule) {
      noItemBox { assertIsDisplayed() }
      noItemText { assertIsDisplayed() }
    }

    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {
      accountButton { assertIsDisplayed() }
      accountButton { performClick() }
    }

    ComposeScreen.onComposeScreen<ViewAccount>(composeTestRule) {
      viewAccount { assertIsDisplayed() }
      backButton { performClick() }
    }

    composeTestRule.waitUntil(timeWait) {
      composeTestRule.onNodeWithTag("inventoryScreenNoItemBox").isDisplayed()
    }
    // check we are indeed in the inventory screen
    ComposeScreen.onComposeScreen<InventoryScreen>(composeTestRule) {
      noItemBox { assertIsDisplayed() }
      noItemText { assertIsDisplayed() }
    }

    // Close the activity after the test
    scenario.close()
  }

  /*TODO: add the navigation to the loan screen when it's pushed on the main with its tests*/
}
