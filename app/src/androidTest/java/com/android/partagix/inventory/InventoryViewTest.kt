package com.android.partagix.inventory

import android.location.Location
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.os.bundleOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.BorrowViewModel
import com.android.partagix.model.ItemUIState
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.UserUIState
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.screens.InventoryViewItemScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.InventoryViewItemScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InventoryViewTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockItemViewModel: ItemViewModel
  @RelaxedMockK lateinit var mockBorrowViewModel: BorrowViewModel
  @RelaxedMockK lateinit var mockUserViewModel: UserViewModel

  val mockLocation = mockk<Location>()

  private var item1: Item =
      Item(
          id = "id",
          category = Category("id", "category1"),
          name = "item1",
          description = "description1",
          visibility = Visibility.PUBLIC,
          quantity = 1,
          location = mockLocation,
          idUser = "id_user")
  val emptyUser = User("", "", "", "", Inventory("", emptyList()))

  private var _uiState = MutableStateFlow(ItemUIState(item1, emptyUser))
  private var mockUiState: StateFlow<ItemUIState> = _uiState

  private var _userUiState = MutableStateFlow(UserUIState(emptyUser))
  private var mockUserUiState: StateFlow<UserUIState> = _userUiState

  @Before
  fun testSetup() {
    mockItemViewModel = mockk()
    every { mockItemViewModel.uiState } returns mockUiState
    every { mockLocation.latitude } returns 0.0
    every { mockLocation.longitude } returns 0.0
    every { mockLocation.extras } returns bundleOf("display_name" to "locationName")

    every { mockItemViewModel.compareIDs(any(), any()) } returns true
    mockBorrowViewModel = mockk()
    every { mockBorrowViewModel.startBorrow(any(), any()) } just Runs

    mockUserViewModel = mockk()
    every { mockUserViewModel.uiState } returns mockUserUiState
    every { mockUserViewModel.setUser(any()) } just Runs

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs
    every { mockNavActions.navigateTo(Route.ACCOUNT) } just Runs

    composeTestRule.setContent {
      InventoryViewItemScreen(mockNavActions, mockItemViewModel, mockBorrowViewModel, mockUserViewModel)
    }
  }

  @Test
  fun contentIsDisplayed() = run {
    onComposeScreen<InventoryViewItemScreen>(composeTestRule) {
      topBar { assertIsDisplayed() }
      bottomBar { assertIsDisplayed() }
      ownerField { assertIsDisplayed() }

      // Change the item name to trigger 'onValueChange' event to verify there is no erros
      item1 =
          Item(
              id = "id",
              category = Category("id", "category1"),
              name = "item1_ALTNAME",
              description = "description1",
              visibility = Visibility.PUBLIC,
              quantity = 1,
              location = mockk(),
              idUser = "id_user")
      assertIsDisplayed()
    }
  }

  @Test
  fun clickOwnerField() = run {
    onComposeScreen<InventoryViewItemScreen>(composeTestRule) {
      ownerField {
        assertIsDisplayed()
        performClick() }

      coVerify { mockNavActions.navigateTo(Route.ACCOUNT) }
    }
  }
}
