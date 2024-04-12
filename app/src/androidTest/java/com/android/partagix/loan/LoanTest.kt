package com.android.partagix.loan

import android.location.Location
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.InventoryUIState
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.UserUIState
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.screens.LoanScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.LoanScreen
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
class LoanTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockInventoryViewModel: InventoryViewModel
  @RelaxedMockK lateinit var mockUserViewModel: UserViewModel

  private lateinit var inventoryUIState: MutableStateFlow<InventoryUIState>
  private lateinit var userUIStateWithLocation: MutableStateFlow<UserUIState>
  private lateinit var userUIStateWithoutLocation: MutableStateFlow<UserUIState>

  @Before
  fun testSetup() {
    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.LOAN) } just Runs
    every { mockNavActions.navigateTo(Route.VIEW_ITEM + "/id1") } just Runs

    mockInventoryViewModel = mockk()
    every { mockInventoryViewModel.getInventory() } just Runs
    every { mockInventoryViewModel.filterItems(query = any()) } just Runs
    every { mockInventoryViewModel.filterItems(atLeastQuantity = any()) } just Runs
    every { mockInventoryViewModel.filterItems(currentPosition = any(), radius = any()) } just Runs

    mockUserViewModel = mockk()
    every { mockUserViewModel.updateLocation(any()) } just Runs

    val item1 =
        Item(
            id = "id1",
            category = Category("id", "category1"),
            name = "cat",
            description = "brilliant description",
            visibility = Visibility.PUBLIC,
            quantity = 1,
            location =
                Location("").apply {
                  latitude = 46.520238
                  longitude = 6.566109
                },
            idUser = "id_user1")

    val item2 =
        Item(
            id = "id2",
            category = Category("id", "category1"),
            name = "dog",
            description = "this is a description",
            visibility = Visibility.PRIVATE,
            quantity = 3,
            location =
                Location("").apply {
                  latitude = 46.520438
                  longitude = 6.566509
                },
            idUser = "id_user2")

    val user = User("id_user", "name", "addr", "rank", mockk())

    // Position link: https://maps.app.goo.gl/kXxVHqw8NQ63jczBA
    val location =
        Location("").apply {
          latitude = 46.520238
          longitude = 6.566109
        }

    inventoryUIState = MutableStateFlow(InventoryUIState(listOf(item1, item2), ""))
    userUIStateWithLocation = MutableStateFlow(UserUIState(user, location))
    userUIStateWithoutLocation = MutableStateFlow(UserUIState(user))

    every { mockInventoryViewModel.uiState } returns inventoryUIState
  }

  @Test
  fun searchBarIsDisplayed() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { searchBar { assertIsDisplayed() } }
  }

  @Test
  fun mapsIsDisplayed() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { maps { assertIsDisplayed() } }
  }

  @Test
  fun distanceFilterIsDisplayed() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { distanceFilter { assertIsDisplayed() } }
  }

  @Test
  fun quantityFilterIsDisplayed() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { qtyFilter { assertIsDisplayed() } }
  }

  @Test
  fun itemsListIsDisplayed() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) {
      itemListView { assertIsDisplayed() }
      itemListViewItem { assertIsDisplayed() }
    }
  }

  @Test
  fun bottomBarIsDisplayed() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) {
      bottomNavBar { assertIsDisplayed() }
      bottomNavBarItemInventory { assertIsDisplayed() }
    }
  }

  @Test
  fun userWithoutLocationWorks() {
    every { mockUserViewModel.uiState } returns userUIStateWithoutLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { maps { assertIsDisplayed() } }
  }

  /*@Test
  fun searchBarWorks() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) {
      searchBar {
        assertIsDisplayed()
        performClick()
        performTextInput("dog")

        assert(inventoryUIState.value.query == "dog")
        assert(inventoryUIState.value.items.size == 1)
      }
    }
  }*/

  @Test
  fun filtersAreClickable() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) {
      distanceFilter {
        assertIsDisplayed()
        performClick()
      }

      qtyFilter {
        assertIsDisplayed()
        performClick()
      }
    }
  }

  @Test
  fun itemsListIsClickable() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) {
      itemListViewItem {
        assertIsDisplayed()
        // click the first one
        performClick()

        verify { mockNavActions.navigateTo(Route.VIEW_ITEM + "/id1") }
      }
    }
  }
}
