package com.android.partagix.loan

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock
import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
import com.android.partagix.model.Database
import com.android.partagix.model.FilterState
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.LoanDetails
import com.android.partagix.model.LoanUIState
import com.android.partagix.model.LoanViewModel
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
import io.mockk.spyk
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
          android.Manifest.permission.ACCESS_COARSE_LOCATION,
          android.Manifest.permission.POST_NOTIFICATIONS)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockLoanViewModel: LoanViewModel
  @RelaxedMockK lateinit var mockUserViewModel: UserViewModel
  @RelaxedMockK lateinit var mockItemViewModel: ItemViewModel
  @RelaxedMockK lateinit var mockDatabase: Database

  private lateinit var loanUIState: MutableStateFlow<LoanUIState>
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
    every { mockNavActions.navigateTo(Route.VIEW_OTHERS_ITEM) } just Runs

    mockDatabase = mockk()
    every { mockDatabase.getUser(any(), any(), any()) } just Runs

    every { mockDatabase.getUserWithImage(any(), any(), any()) } just Runs

    mockLoanViewModel = spyk(LoanViewModel(db = mockDatabase))
    every { mockLoanViewModel.getAvailableLoans(any()) } just Runs

    mockUserViewModel = spyk(UserViewModel(db = mockDatabase))
    every { mockUserViewModel.updateLocation(any()) } just Runs

    mockItemViewModel = spyk(ItemViewModel(db = mockDatabase))

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

    val user = User("id_user", "name", "addr", "0.48", mockk())

    val user1 = User("id_user1", "name1", "addr1", "2.123445", mockk())
    val user2 = User("id_user2", "name2", "addr2", "4.99999", mockk())

    // Position link: https://maps.app.goo.gl/kXxVHqw8NQ63jczBA
    val location =
        Location("").apply {
          latitude = 46.520238
          longitude = 6.566109
        }

    val listLoanDetails = listOf(LoanDetails(item1, user1), LoanDetails(item2, user2))

    loanUIState = MutableStateFlow(LoanUIState(listLoanDetails))
    userUIStateWithLocation = MutableStateFlow(UserUIState(user, location))
    userUIStateWithoutLocation = MutableStateFlow(UserUIState(user))

    every { mockLoanViewModel.uiState } returns loanUIState

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
          LocationManager.GPS_PROVIDER, false, false, false, false, true, true, true, 1, 1)
      locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
      locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation)
    } catch (e: SecurityException) {
      // Handle the exception
      Log.e(TAG, "SecurityException: ${e.message}")
    }
  }

  @Test
  fun contentIsDisplayed1() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
    }

    onComposeScreen<LoanScreen>(composeTestRule) {
      searchBar { assertIsDisplayed() }
      maps { assertIsDisplayed() }
      distanceFilter { assertIsDisplayed() }
      qtyFilter { assertIsDisplayed() }
      itemListView { assertIsDisplayed() }
      bottomNavBar { assertIsDisplayed() }
      bottomNavBarItemInventory { assertIsDisplayed() }
    }
  }

  @Test
  fun contentIsDisplayed2SearchBar() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { searchBar { assertIsDisplayed() } }
  }

  @Test
  fun contentIsDisplayedMaps() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { maps { assertIsDisplayed() } }
  }

  @Test
  fun contentIsDisplayedDistanceFilter() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { distanceFilter { assertIsDisplayed() } }
  }

  @Test
  fun contentIsDisplayedQtyFilter() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { qtyFilter { assertIsDisplayed() } }
  }

  @Test
  fun contentIsDisplayedItemListView() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { itemListView { assertIsDisplayed() } }
  }

  @Test
  fun contentIsDisplayedItemListViewItem() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { itemListView { assertIsDisplayed() } }
  }

  @Test
  fun contentIsDisplayedBottomNavBar() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { bottomNavBar { assertIsDisplayed() } }
  }

  @Test
  fun contentIsDisplayedBottomNavBarItemInventory() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
    }

    onComposeScreen<LoanScreen>(composeTestRule) {
      bottomNavBarItemInventory { assertIsDisplayed() }
    }
  }

  @Test
  fun userWithoutLocationWorks() {
    every { mockUserViewModel.uiState } returns userUIStateWithoutLocation
    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
    }

    onComposeScreen<LoanScreen>(composeTestRule) { maps { assertIsDisplayed() } }
  }

  @Test
  fun searchBarWorks() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    every { mockLoanViewModel.applyFilters(any()) } answers
        {
          val filterState = firstArg<FilterState>()
          val query = filterState.query ?: ""
          val filteredItems =
              loanUIState.value.availableLoans.filter { it.item.name.contains(query) }
          val filteredState = loanUIState.value.filterState.copy(query = query)
          loanUIState.value =
              loanUIState.value.copy(availableLoans = filteredItems, filterState = filteredState)
        }

    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
    }

    val node = composeTestRule.onNodeWithTag("LoanScreenSearchBar").onChild()

    node.assertIsDisplayed()
    node.performClick()
    node.performTextInput("dog")
    node.performImeAction()

    val state = mockLoanViewModel.uiState.value
    assert(state.filterState.query == "dog")
    assert(state.availableLoans.size == 1)
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
  fun filtersAndItemListAreClickable() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
    }

    every { mockItemViewModel.updateUiItem(any()) } just Runs

    onComposeScreen<LoanScreen>(composeTestRule) {
      distanceFilter {
        assertIsDisplayed()
        performClick()
      }

      qtyFilter {
        assertIsDisplayed()
        performClick()
      }
      itemListView { assertIsDisplayed() }
    }

    // look for the item "cat" within itemListView and click on it
    val catItem = composeTestRule.onNode(hasText("cat"))
    catItem.assertIsDisplayed()
    catItem.performClick()

    verify { mockNavActions.navigateTo(Route.VIEW_OTHERS_ITEM) }
  }

  @Test
  fun distanceFilterWorks() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    every { mockLoanViewModel.applyFilters(any()) } answers
        {
          val filterState = firstArg<FilterState>()
          val currentPosition = filterState.location ?: Location("")
          val radius = filterState.radius ?: 0.0
          val filteredItems =
              loanUIState.value.availableLoans.filter {
                it.item.location.distanceTo(currentPosition) <= (radius * 1000)
              }
          loanUIState.value = loanUIState.value.copy(availableLoans = filteredItems)
        }
    every { mockLoanViewModel.resetFilter(any()) } answers
        {
          loanUIState.value =
              loanUIState.value.copy(availableLoans = loanUIState.value.availableLoans)
        }

    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
    }

    onComposeScreen<LoanScreen>(composeTestRule) {
      distanceFilter {
        assertIsDisplayed()
        performClick()
      }
    }

    for (item in mockLoanViewModel.uiState.value.availableLoans) {
      Log.d(TAG, "before: item: ${item.item.location.distanceTo(currentPosition)}")
    }

    val slider = composeTestRule.onNodeWithTag("SliderFilter")
    slider.assertIsDisplayed()
    slider.performTouchInput(
        fun TouchInjectionScope.() {
          swipeRight()
        })

    for (item in mockLoanViewModel.uiState.value.availableLoans) {
      Log.d(TAG, "after: item: ${item.item.location.distanceTo(currentPosition)}")
    }

    val state = mockLoanViewModel.uiState.value
    assert(state.availableLoans.size == 1)
    assert(state.availableLoans[0].item.name == "cat")
  }

  @Test
  fun quantityFilterWorks() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    every { mockLoanViewModel.applyFilters(any()) } answers
        {
          val filterState = firstArg<FilterState>()
          val atLeastQuantity = filterState.atLeastQuantity ?: 0
          val filteredItems =
              loanUIState.value.availableLoans.filter { it.item.quantity >= atLeastQuantity }
          loanUIState.value = loanUIState.value.copy(availableLoans = filteredItems)
        }
    every { mockLoanViewModel.resetFilter(any()) } answers
        {
          loanUIState.value =
              loanUIState.value.copy(availableLoans = loanUIState.value.availableLoans)
        }

    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
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

    val state = mockLoanViewModel.uiState.value
    assert(state.availableLoans.size == 1)
    assert(state.availableLoans[0].item.name == "dog")
  }

  @Test
  fun itemsListIsClickable() {
    every { mockUserViewModel.uiState } returns userUIStateWithLocation
    composeTestRule.setContent {
      LoanScreen(
          mockNavActions,
          mockLoanViewModel,
          mockItemViewModel,
          mockUserViewModel,
          isMapLoadingOptimized = false)
    }
    every { mockItemViewModel.updateUiItem(any()) } just Runs

    onComposeScreen<LoanScreen>(composeTestRule) { itemListView { assertIsDisplayed() } }

    // look for the item "cat" within itemListView and click on it
    val catItem = composeTestRule.onNode(hasText("cat"))
    catItem.assertIsDisplayed()
    catItem.performClick()

    verify { mockNavActions.navigateTo(Route.VIEW_OTHERS_ITEM) }
  }

  companion object {
    private const val TAG = "LoanTest"
  }
}
