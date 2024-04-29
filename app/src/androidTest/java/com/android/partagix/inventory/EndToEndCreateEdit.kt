package com.android.partagix.inventory

import android.content.Intent
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.android.partagix.MainActivity
import com.android.partagix.model.Database
import com.android.partagix.model.HomeViewModel
import com.android.partagix.model.InventoryUIState
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.ItemUIState
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.StampViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.partagix.ui.screens.HomeScreen
import com.android.partagix.ui.screens.InventoryCreateOrEditItem
import com.android.partagix.ui.screens.InventoryScreen
import com.android.partagix.ui.screens.InventoryViewItem
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.common.base.CharMatcher.any
import com.google.firebase.auth.FirebaseUser
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class EndToEndCreateEdit {

  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockHomeViewModel: HomeViewModel
  @RelaxedMockK lateinit var mockInventoryViewModel: InventoryViewModel
  @RelaxedMockK lateinit var mockItemViewModel: ItemViewModel
  @RelaxedMockK lateinit var mockStampViewModel: StampViewModel

  private lateinit var mockUiState: MutableStateFlow<InventoryUIState>
  private lateinit var mockUiState2: MutableStateFlow<InventoryUIState>
  private lateinit var mockUiState3: MutableStateFlow<InventoryUIState>
  private lateinit var mockItemUiState: MutableStateFlow<ItemUIState>
  private lateinit var mockItemUiState2: MutableStateFlow<ItemUIState>
  private lateinit var mockItemUiState3: MutableStateFlow<ItemUIState>

  val cat1 = Category("1", "Category 1")
  val vis1 = com.android.partagix.model.visibility.Visibility.FRIENDS
  val loc1 = Location("")
  val items = emptyList<Item>()
  val item2 = Item("1234", cat1, "Object 1", "Description 1", vis1, 2, loc1)
  val item3 = Item("1234", cat1, "Object 1 edited", "Description 1 edited", vis1, 3, loc1)

  @Before
  fun setup() {
    mockUiState =
        MutableStateFlow(
            InventoryUIState(items, "", items, emptyList(), emptyList(), emptyList(), emptyList()))
    mockUiState2 =
        MutableStateFlow(
            InventoryUIState(
                listOf(item2), "", items, emptyList(), emptyList(), emptyList(), emptyList()))
    mockUiState3 =
        MutableStateFlow(
            InventoryUIState(
                listOf(item3), "", items, emptyList(), emptyList(), emptyList(), emptyList()))

    mockItemUiState =
        MutableStateFlow(
            ItemUIState(Item("", Category("", ""), "", "", Visibility.PUBLIC, 1, Location(""))))

    mockItemUiState2 = MutableStateFlow(ItemUIState(item2))

    mockItemUiState3 = MutableStateFlow(ItemUIState(item3))

    mockNavActions = mockk()
    mockHomeViewModel = mockk()
    mockInventoryViewModel = mockk()
    mockItemViewModel = mockk()
    mockStampViewModel = mockk()

    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.CREATE_ITEM) } just Runs
    every { mockNavActions.navigateTo(Route.VIEW_ITEM) } just Runs
    every { mockNavActions.navigateTo(Route.EDIT_ITEM) } just Runs
    every { mockNavActions.goBack() } just Runs
    every { mockNavActions.navigateTo(TOP_LEVEL_DESTINATIONS[2]) } just Runs

    every { mockItemViewModel.updateUiState(any()) } just Runs

    val mockMainActivity = mockk<MainActivity>()
    val mockDatabase = mockk<Database>()
    val mockActivityResultLauncher = mockk<ActivityResultLauncher<Intent>>()
    val mockFirebaseUser = mockk<FirebaseUser>()

    every {
      mockMainActivity.registerForActivityResult(any<FirebaseAuthUIActivityResultContract>(), any())
    } returns mockActivityResultLauncher

    every { mockDatabase.getUserInventory(any(), any()) } just Runs
    every { mockDatabase.getLoans(any()) } just Runs
    every { mockDatabase.getUser(any(), any(), any()) } just Runs
    every { mockDatabase.createUser(any()) } just Runs

    every { mockItemViewModel.check(any(), any()) } returns true

    every { mockFirebaseUser.uid } returns "1234"
    every { mockFirebaseUser.displayName } returns "name"
    every { mockFirebaseUser.email } returns "email"
  }

  @Test
  fun testA_goFromHomeToInventory() {
    every { mockInventoryViewModel.uiState } returns mockUiState
    every { mockItemViewModel.uiState } returns mockItemUiState

    composeTestRule.setContent {
      HomeScreen(
          homeViewModel = mockHomeViewModel,
          inventoryViewModel = mockInventoryViewModel,
          navigationActions = mockNavActions)
    }

    // val app = App(mockMainActivity, mockAuthentication, mockDatabase)

    composeTestRule.onNodeWithText("Inventory").performClick()

    coVerify(exactly = 1) { mockNavActions.navigateTo(TOP_LEVEL_DESTINATIONS[2]) }
  }

  @Test
  fun testB_InventoryEmptyToCreate() {
    every { mockInventoryViewModel.uiState } returns mockUiState
    every { mockItemViewModel.uiState } returns mockItemUiState

    composeTestRule.setContent {
      InventoryScreen(
          inventoryViewModel = mockInventoryViewModel,
          navigationActions = mockNavActions,
          itemViewModel = mockItemViewModel)
    }

    composeTestRule.onNodeWithText("There is no items in the inventory.").assertIsDisplayed()

    composeTestRule.onNodeWithTag("inventoryScreenFab").performClick()

    coVerify(exactly = 1) { mockNavActions.navigateTo(Route.CREATE_ITEM) }
  }

  @Test
  fun testC_CreateItem() {
    every { mockInventoryViewModel.uiState } returns mockUiState
    every { mockItemViewModel.uiState } returns mockItemUiState
    composeTestRule.setContent {
      InventoryCreateOrEditItem(
          itemViewModel = mockItemViewModel, navigationActions = mockNavActions, mode = "create")
    }

    every { mockItemViewModel.updateUiState(any()) } just Runs
    every { mockItemViewModel.save(any()) } just Runs

    composeTestRule.onNodeWithTag("name").performTextInput("Object 1")
    composeTestRule.onNodeWithTag("description").performTextInput("Description 1")
    composeTestRule.onNodeWithTag("quantity").performTextReplacement("2")
    composeTestRule.onNodeWithTag("visibility").performClick()
    composeTestRule.onNodeWithText("Friends").performClick()
    composeTestRule.onNodeWithTag("category").performClick()
    composeTestRule.onNodeWithText("Category 1").performClick()
    composeTestRule.onNodeWithText("Create").performScrollTo()
    composeTestRule.onNodeWithText("Create").performClick()

    coVerify(exactly = 1) { mockItemViewModel.save(any()) }
    coVerify(exactly = 1) { mockNavActions.goBack() }
  }

  @Test
  fun testD_InventoryOneItem() {
    every { mockInventoryViewModel.uiState } returns mockUiState2
    every { mockItemViewModel.uiState } returns mockItemUiState2
    composeTestRule.setContent {
      InventoryScreen(
          inventoryViewModel = mockInventoryViewModel,
          navigationActions = mockNavActions,
          itemViewModel = mockItemViewModel)
    }
    composeTestRule.onNodeWithText("Object 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Object 1").performClick()

    coVerify(exactly = 1) { mockNavActions.navigateTo(Route.VIEW_ITEM) }
  }

  @Test
  fun testE_goToViewItem() {
    every { mockInventoryViewModel.uiState } returns mockUiState2
    every { mockItemViewModel.uiState } returns mockItemUiState2
    composeTestRule.setContent {
      InventoryViewItem(
          navigationActions = mockNavActions,
          viewModel = mockItemViewModel,
          stampViewModel = mockStampViewModel)
    }

    composeTestRule.onNodeWithText("Object 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Description 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("2").assertIsDisplayed()
    composeTestRule.onNodeWithText("FRIENDS").assertIsDisplayed()
    composeTestRule.onNodeWithText("Category 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Edit").performScrollTo()
    composeTestRule.onNodeWithText("Edit").performClick()

    coVerify(exactly = 1) { mockNavActions.navigateTo(Route.EDIT_ITEM) }
  }

  @Test
  fun testF_EditItem() {
    every { mockItemViewModel.uiState } returns mockItemUiState2

    composeTestRule.setContent {
      InventoryCreateOrEditItem(
          itemViewModel = mockItemViewModel, navigationActions = mockNavActions, mode = "edit")
    }

    every { mockItemViewModel.updateUiState(any()) } just Runs
    every { mockItemViewModel.save(any()) } just Runs

    composeTestRule.onNodeWithTag("name").performTextReplacement("Object 1 edited")
    composeTestRule.onNodeWithTag("description").performTextReplacement("Description 1 edited")
    composeTestRule.onNodeWithTag("quantity").performTextReplacement("3")

    composeTestRule.onNodeWithText("Save").performScrollTo()
    composeTestRule.onNodeWithText("Save").performClick()

    coVerify(exactly = 1) { mockItemViewModel.save(any()) }
    coVerify(exactly = 1) { mockNavActions.goBack() }
  }

  @Test
  fun testG_viewEditedToInventory() {
    every { mockInventoryViewModel.uiState } returns mockUiState3
    every { mockItemViewModel.uiState } returns mockItemUiState3
    composeTestRule.setContent {
      InventoryViewItem(
          navigationActions = mockNavActions,
          viewModel = mockItemViewModel,
          stampViewModel = mockStampViewModel)
    }
    composeTestRule.onNodeWithText("Object 1 edited").assertIsDisplayed()
    composeTestRule.onNodeWithText("Description 1 edited").assertIsDisplayed()
    composeTestRule.onNodeWithText("3").assertIsDisplayed()
    composeTestRule.onNodeWithText("FRIENDS").assertIsDisplayed()
    composeTestRule.onNodeWithText("Category 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("navigationIcon").performClick()

    coVerify(exactly = 1) { mockNavActions.goBack() }
  }

  @Test
  fun testH_inventoryWithEditedItem() {
    every { mockInventoryViewModel.uiState } returns mockUiState3
    composeTestRule.setContent {
      InventoryScreen(
          inventoryViewModel = mockInventoryViewModel,
          navigationActions = mockNavActions,
          itemViewModel = mockItemViewModel)
    }
    composeTestRule.onNodeWithText("Object 1 edited").assertIsDisplayed()
    composeTestRule.onNodeWithText("Object 1 edited").performClick()
  }
}
