package com.android.partagix.inventory

import android.location.Location
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.ItemUIState
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.screens.InventoryCreateOrEditScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.InventoryCreateOrEditItem
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
class InventoryCreateOrEditTest :
    TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockViewModel: ItemViewModel

  private lateinit var emptyMockUiState: MutableStateFlow<ItemUIState>
  private lateinit var nonEmptyMockUiState: MutableStateFlow<ItemUIState>

  @Before
  fun testSetup() {

    val emptyItem = Item("", Category("", ""), "", "", Visibility.PUBLIC, 1, Location(""))
    val emptyUser = User("", "", "", "", Inventory("", emptyList()))
    emptyMockUiState = MutableStateFlow(ItemUIState(emptyItem, emptyUser))
    val cat1 = Category("1", "Category 1")
    val vis1 = Visibility.PUBLIC
    val loc1 = Location("1")
    val item = Item("1", cat1, "Name 1", "Description 1", vis1, 1, loc1)

    nonEmptyMockUiState = MutableStateFlow(ItemUIState(item, emptyUser))

    mockViewModel = mockk()
    // every { mockInventoryViewModel.uiState } returns emptyMockUiState
    every { mockViewModel.save(item) } just Runs

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs

    /*    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }*/
  }

  @Test
  fun testTest() = run {
    every { mockViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryCreateOrEditItem(mockViewModel, mockNavActions, mode = "")
    }

    assert(true)
  }

  @Test
  fun topBarIsDisplayed() = run {
    every { mockViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryCreateOrEditItem(mockViewModel, mockNavActions, mode = "")
    }

    onComposeScreen<InventoryCreateOrEditScreen>(composeTestRule) {
      topBar { assertIsDisplayed() }

      navigationIcon { assertIsDisplayed() }
    }
  }

  @Test
  fun titleOnEdit() = run {
    every { mockViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryCreateOrEditItem(mockViewModel, mockNavActions, mode = "edit")
    }
    onComposeScreen<InventoryCreateOrEditScreen>(composeTestRule) {
      title {
        assertIsDisplayed()
        assertTextEquals("Edit item")
      }
    }
  }

  @Test
  fun titleOnCreate() = run {
    every { mockViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryCreateOrEditItem(mockViewModel, mockNavActions, mode = "")
    }
    onComposeScreen<InventoryCreateOrEditScreen>(composeTestRule) {
      title {
        assertIsDisplayed()
        assertTextEquals("Create a new item")
      }
    }
  }

  @Test
  fun emptyItemIsDisplayed() = run {
    every { mockViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryCreateOrEditItem(mockViewModel, mockNavActions, mode = "")
    }

    onComposeScreen<InventoryCreateOrEditScreen>(composeTestRule) {
      image { assertIsDisplayed() }

      name {
        assertIsDisplayed()
        assertTextContains("Object name")
      }

      idUser {
        assertIsDisplayed()
        assertTextContains("Author")
      }

      description {
        assertIsDisplayed()
        assertTextContains("Description")
      }

      category { assertIsDisplayed() }

      visibility { assertIsDisplayed() }

      quantity { assertIsDisplayed() }
    }
  }
}
