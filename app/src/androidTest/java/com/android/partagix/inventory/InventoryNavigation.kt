package com.android.partagix.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.ui.navigation.NavigationActions
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InventoryFeatureTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    @RelaxedMockK
    lateinit var mockNavActions: NavigationActions
    @RelaxedMockK
    lateinit var mockInventoryViewModel: InventoryViewModel

    @Test
    fun testNavigateToCreateItem() {
        // Launch the inventory screen
        composeTestRule.setContent {
            InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
        }

        // Click on the "Create" floating action button
        composeTestRule.onNodeWithContentDescription("Create").performClick()

        // Verify navigation to the create item screen
        composeTestRule.onNodeWithText("Create Item").assertExists()
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
        composeTestRule.onNodeWithText("Object name").assertExists()
        composeTestRule.onNodeWithText("Author").assertExists()
        // Add more assertions for other item details displayed on the view item screen
    }

    @Test
    fun testNavigateToEditItem() {
        // Launch the inventory screen
        composeTestRule.setContent {
            InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
        }

        // Click on the first item in the inventory list
        composeTestRule.onNodeWithTag("inventoryItem_0").performClick()

        // Click on the "Edit" button
        composeTestRule.onNodeWithText("Edit").performClick()

        // Verify navigation to the edit item screen
        composeTestRule.onNodeWithText("Edit Item").assertExists()
        // Add more assertions for other fields or buttons on the edit item screen
    }
}
