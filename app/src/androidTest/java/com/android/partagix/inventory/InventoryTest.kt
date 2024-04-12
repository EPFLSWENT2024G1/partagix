package com.android.partagix.inventory

import android.location.Location
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.InventoryUIState
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
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
import io.mockk.verify
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

  private lateinit var emptyMockUiState: MutableStateFlow<InventoryUIState>
  private lateinit var nonEmptyMockUiState: MutableStateFlow<InventoryUIState>

  @Before
  fun testSetup() {
    emptyMockUiState = MutableStateFlow(InventoryUIState(emptyList(), ""))
    val cat1 = Category("1", "Category 1")
    val vis1 = com.android.partagix.model.visibility.Visibility.PUBLIC
    val loc1 = Location("1")
    val items = listOf(Item("1", cat1, "Name 1", "Description 1", vis1, 1, loc1))
    nonEmptyMockUiState = MutableStateFlow(InventoryUIState(items, ""))

    mockInventoryViewModel = mockk()
    // every { mockInventoryViewModel.uiState } returns emptyMockUiState
    every { mockInventoryViewModel.getInventory() } just Runs
    every { mockInventoryViewModel.filterItems(any()) } just Runs

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs

    /*    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }*/
  }

  @Test
  fun testTest() = run {
    every { mockInventoryViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }

    assert(true)
  }

  @Test
  fun searchBarIsDisplayed() = run {
    every { mockInventoryViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }

    onComposeScreen<InventoryScreen>(composeTestRule) { searchBar { assertIsDisplayed() } }
  }

  @Test
  fun searchBarWorks() = run {
    every { mockInventoryViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }

    onComposeScreen<InventoryScreen>(composeTestRule) {
      searchBar {
        assertIsDisplayed()
        performClick()
      }
      searchBarBackIcon { assertIsDisplayed() }
      searchBarBackIcon { performClick() }
      searchBar {
        performClick()
        // performTextInput("test")
      }
      searchBarSearchIcon { assertIsDisplayed() }
      searchBarSearchIcon { performClick() }
      /*      val textField: KNode = searchBarSearchIcon.child<KNode> { hasSetTextAction() }
      textField {
        performTextClearance()
        performTextInput("test")
      }*/
      verify { mockInventoryViewModel.filterItems("") }
    }
  }

  @Test
  fun fabIsDisplayed() = run {
    every { mockInventoryViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }

    onComposeScreen<InventoryScreen>(composeTestRule) { fab { assertIsDisplayed() } }
  }

  @Test
  fun noItemBoxIsDisplayed() = run {
    every { mockInventoryViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }

    onComposeScreen<InventoryScreen>(composeTestRule) { noItemBox { assertIsDisplayed() } }
  }

  @Test
  fun noItemTextIsDisplayed() = run {
    every { mockInventoryViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }

    onComposeScreen<InventoryScreen>(composeTestRule) {
      noItemText {
        assertIsDisplayed()
        assertTextEquals("There is no items in the inventory.")
      }
    }
  }

  @Test
  fun bottomNavBarIsDisplayed() = run {
    every { mockInventoryViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }

    onComposeScreen<InventoryScreen>(composeTestRule) { bottomNavBar { assertIsDisplayed() } }
  }

  @Test
  fun bottomNavBarItemInventoryIsDisplayed() = run {
    every { mockInventoryViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }

    onComposeScreen<InventoryScreen>(composeTestRule) {
      bottomNavBarItemInventory { assertIsDisplayed() }
    }
  }

  @Test
  fun itemListIsDisplayed() = run {
    every { mockInventoryViewModel.uiState } returns nonEmptyMockUiState
    composeTestRule.setContent {
      InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
    }

    onComposeScreen<InventoryScreen>(composeTestRule) {
      itemList { assertIsDisplayed() }
      // noItemBox { assertIsDisplayed()}
    }
  }
}
