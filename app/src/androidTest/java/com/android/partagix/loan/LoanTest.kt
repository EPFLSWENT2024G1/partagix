package com.android.partagix.loan

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock
import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.TouchInjectionScope
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.android.partagix.model.InventoryUIState
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.UserUIState
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.screens.LoanScreen
import com.android.partagix.ui.components.TopSearchBar
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

  @get:Rule
  val grantPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          android.Manifest.permission.ACCESS_FINE_LOCATION,
          android.Manifest.permission.ACCESS_COARSE_LOCATION)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockInventoryViewModel: InventoryViewModel
  @RelaxedMockK lateinit var mockUserViewModel: UserViewModel
  @RelaxedMockK lateinit var itemViewModel: ItemViewModel

  private lateinit var inventoryUIState: MutableStateFlow<InventoryUIState>
  private lateinit var userUIStateWithLocation: MutableStateFlow<UserUIState>
  private lateinit var userUIStateWithoutLocation: MutableStateFlow<UserUIState>

  private val currentPosition =
      Location("").apply {
        latitude = 46.520238
        longitude = 6.566109
      }

  @Before
  fun testSetup() {
    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.LOAN) } just Runs
    every { mockNavActions.navigateTo(Route.VIEW_ITEM) } just Runs

    mockInventoryViewModel = mockk()
    every { mockInventoryViewModel.getInventory() } just Runs
    every { mockInventoryViewModel.filterItems(atLeastQuantity = any()) } just Runs
    every { mockInventoryViewModel.filterItems(currentPosition = any(), radius = any()) } just Runs
    every { mockInventoryViewModel.getusers(any(), any()) } just Runs
    every { mockInventoryViewModel.findtime(any(), any()) } just Runs

    mockUserViewModel = mockk()
    every { mockUserViewModel.updateLocation(any()) } just Runs

    itemViewModel = mockk()

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
                  longitude = 6.566209
                },
            idUser = "id_user1")

    val item2 =
        Item(
            id = "id2",
            category = Category("id", "category1"),
            name = "dog",
            description = "this is a description",
            visibility = Visibility.PRIVATE,
            quantity = 10,
            location =
                Location("").apply {
                  latitude = 46.450438
                  longitude = 6.576509
                },
            idUser = "id_user2")

    val user = User("id_user", "name", "addr", "rank", mockk())

    // Position link: https://maps.app.goo.gl/kXxVHqw8NQ63jczBA
    val location =
        Location("").apply {
          latitude = 46.520238
          longitude = 6.566109
        }

    inventoryUIState =
        MutableStateFlow(
            InventoryUIState(
                listOf(item1, item2),
                "",
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList()))
    userUIStateWithLocation = MutableStateFlow(UserUIState(user, location))
    userUIStateWithoutLocation = MutableStateFlow(UserUIState(user))

    every { mockInventoryViewModel.uiState } returns inventoryUIState

    val locationManager =
        InstrumentationRegistry.getInstrumentation()
            .targetContext
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // Create a new Location object with the mock location data
    val mockLocation =
        Location(LocationManager.GPS_PROVIDER).apply {
          latitude = currentPosition.latitude
          longitude = currentPosition.longitude
          altitude = 300.0
          time = System.currentTimeMillis()
          elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }

    // Set the mock location
    try {
      locationManager.addTestProvider(
          LocationManager.GPS_PROVIDER,
          false,
          false,
          false,
          false,
          true,
          true,
          true,
          Criteria.POWER_LOW,
          Criteria.ACCURACY_FINE)
      locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
      locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation)
    } catch (e: SecurityException) {
      // Handle the exception
      Log.e(TAG, "SecurityException: ${e.message}")
    }
  }

  @Test
  fun searchBarIsDisplayed() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, itemViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { searchBar { assertIsDisplayed() } }
  }

  @Test
  fun mapsIsDisplayed() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, itemViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { maps { assertIsDisplayed() } }
  }

  @Test
  fun distanceFilterIsDisplayed() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, itemViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { distanceFilter { assertIsDisplayed() } }
  }

  @Test
  fun quantityFilterIsDisplayed() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, itemViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { qtyFilter { assertIsDisplayed() } }
  }

  @Test
  fun itemsListIsDisplayed() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, itemViewModel, mockUserViewModel)
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
      LoanScreen(mockNavActions, mockInventoryViewModel, itemViewModel, mockUserViewModel)
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
      LoanScreen(mockNavActions, mockInventoryViewModel, itemViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { maps { assertIsDisplayed() } }
  }

  @Test
  fun searchBarWorks() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    every { mockInventoryViewModel.filterItems(query = any()) } answers
        {
          val query = firstArg<String>()
          val filteredItems = inventoryUIState.value.items.filter { it.name.contains(query) }
          inventoryUIState.value = inventoryUIState.value.copy(items = filteredItems, query = query)
        }

    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, itemViewModel, mockUserViewModel)
    }

    val node = composeTestRule.onNodeWithTag("LoanScreenSearchBar").onChild()

    node.assertIsDisplayed()
    node.performClick()
    node.performTextInput("dog")
    node.performImeAction()

    val state = mockInventoryViewModel.uiState.value
    assert(state.query == "dog")
    assert(state.items.size == 1)
  }

  @Test
  fun testSearchField() {
    var value = ""

    composeTestRule.setContent {
      TopSearchBar(filter = { value = it }, modifier = Modifier.testTag("SearchBar"))
    }

    // Find the SearchField by its test tag
    val node = composeTestRule.onNodeWithTag("SearchBar").onChild()

    node.assertIsDisplayed()
    node.performClick()
    node.performTextInput("dog")

    val backButton = composeTestRule.onNodeWithTag("SearchBarBack")
    backButton.assertIsDisplayed()
    backButton.performClick()
    assert(value == "")

    node.performClick()
    node.performTextInput("dog")

    val searchButton = composeTestRule.onNodeWithTag("SearchBarSearch")
    searchButton.assertIsDisplayed()
    searchButton.performClick()

    assert(value == "dog")
    node.assert(hasText("dog"))
  }

  @Test
  fun filtersAreClickable() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, itemViewModel, mockUserViewModel)
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

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun distanceFilterWorks() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    every { mockInventoryViewModel.filterItems(currentPosition = any(), radius = any()) } answers
        {
          val currentPosition = firstArg<Location>()
          val radius = secondArg<Double>()
          val filteredItems =
              inventoryUIState.value.items.filter {
                it.location.distanceTo(currentPosition) <= (radius * 1000)
              }
          inventoryUIState.value = inventoryUIState.value.copy(items = filteredItems)
        }

    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, itemViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) {
      distanceFilter {
        assertIsDisplayed()
        performClick()
      }
    }

    for (item in mockInventoryViewModel.uiState.value.items) {
      Log.d(TAG, "before: item: ${item.location.distanceTo(currentPosition)}")
    }

    val slider = composeTestRule.onNodeWithTag("SliderFilter")
    slider.assertIsDisplayed()
    slider.performTouchInput(
        fun TouchInjectionScope.() {
          swipeRight()
        })

    for (item in mockInventoryViewModel.uiState.value.items) {
      Log.d(TAG, "after: item: ${item.location.distanceTo(currentPosition)}")
    }

    val state = mockInventoryViewModel.uiState.value
    assert(state.items.size == 1)
    assert(state.items[0].name == "cat")
  }

  @Test
  fun quantityFilterWorks() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    every { mockInventoryViewModel.filterItems(atLeastQuantity = any()) } answers
        {
          val atLeastQuantity = firstArg<Int>()
          val filteredItems = inventoryUIState.value.items.filter { it.quantity >= atLeastQuantity }
          inventoryUIState.value = inventoryUIState.value.copy(items = filteredItems)
        }

    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, itemViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) {
      qtyFilter {
        assertIsDisplayed()
        performClick()
      }
    }

    val slider = composeTestRule.onNodeWithTag("SliderFilter")
    slider.assertIsDisplayed()
    slider.performTouchInput(
        fun TouchInjectionScope.() {
          swipeRight(endX = left + (right - left) / 8)
        })

    val state = mockInventoryViewModel.uiState.value
    assert(state.items.size == 1)
    assert(state.items[0].name == "dog")
  }

  @Test
  fun itemsListIsClickable() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(mockNavActions, mockInventoryViewModel, itemViewModel, mockUserViewModel)
    }

    onComposeScreen<LoanScreen>(composeTestRule) {
      itemListViewItem {
        assertIsDisplayed()
        every { itemViewModel.updateUiItem(any()) } just Runs
        // click the first one
        performClick()

        verify { mockNavActions.navigateTo(Route.VIEW_ITEM) }
      }
    }
  }

  companion object {
    private const val TAG = "LoanTest"
  }
}
