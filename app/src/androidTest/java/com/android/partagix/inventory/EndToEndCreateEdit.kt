package com.android.partagix.inventory

import android.content.Intent
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.core.os.bundleOf
import com.android.partagix.MainActivity
import com.android.partagix.model.BorrowViewModel
import com.android.partagix.model.Database
import com.android.partagix.model.HomeUIState
import com.android.partagix.model.HomeViewModel
import com.android.partagix.model.InventoryUIState
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.ItemUIState
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.ManagerUIState
import com.android.partagix.model.StampViewModel
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.stampDimension.StampDimension
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.ui.components.CategoryItems
import com.android.partagix.ui.components.locationPicker.LocationPickerViewModel
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.partagix.ui.screens.HomeScreen
import com.android.partagix.ui.screens.InventoryCreateOrEditItem
import com.android.partagix.ui.screens.InventoryScreen
import com.android.partagix.ui.screens.InventoryViewItemScreen
import com.android.partagix.ui.screens.StampScreen
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseUser
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import java.io.File
import junit.framework.TestCase.assertEquals
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
  @RelaxedMockK lateinit var mockManageViewModelIncoming: ManageLoanViewModel
  @RelaxedMockK lateinit var mockManageViewModelOutgoing: ManageLoanViewModel
  @RelaxedMockK lateinit var mockItemViewModel: ItemViewModel
  @RelaxedMockK lateinit var mockStampViewModel: StampViewModel
  @RelaxedMockK lateinit var mockBorrowViewModel: BorrowViewModel
  @RelaxedMockK lateinit var mockLocationViewModel: LocationPickerViewModel
  @RelaxedMockK lateinit var mockUserViewModel: UserViewModel

  private lateinit var mockUiState: MutableStateFlow<InventoryUIState>
  private lateinit var mockUiState2: MutableStateFlow<InventoryUIState>
  private lateinit var mockUiState3: MutableStateFlow<InventoryUIState>
  private lateinit var mockItemUiState: MutableStateFlow<ItemUIState>
  private lateinit var mockItemUiState2: MutableStateFlow<ItemUIState>
  private lateinit var mockItemUiState3: MutableStateFlow<ItemUIState>
  private lateinit var mockManageUiState: MutableStateFlow<ManagerUIState>
  private lateinit var mockHomeUiState: MutableStateFlow<HomeUIState>

  val cat1 = Category("1", CategoryItems[1])
  val vis1 = Visibility.PUBLIC
  val loc1 = Location("")
  val loc2 = com.android.partagix.model.location.Location(12.0, 12.0, "Lausanne")
  val loc3 = com.android.partagix.model.location.Location(13.0, 13.0, "Paris")

  val items = emptyList<Item>()
  val item2 = Item("1234", cat1, "Object 1", "Description 1", vis1, 2, loc1)
  val item3 = Item("1234", cat1, "Object 1 edited", "Description 1 edited", vis1, 3, loc1)

  val user = User("1234", "name", "Lausanne", "1234", Inventory("1234", emptyList()))

  @Before
  fun setup() {
    mockUiState =
        MutableStateFlow(
            InventoryUIState(items, "", items, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
    mockUiState2 =
        MutableStateFlow(
            InventoryUIState(
                listOf(item2), "", items, emptyList(), emptyList(), emptyList(), emptyList() ,emptyList(), emptyList()))
    mockUiState3 =
        MutableStateFlow(
            InventoryUIState(
                listOf(item3), "", items, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))

    mockManageUiState =
        MutableStateFlow(ManagerUIState(items, emptyList(), emptyList(), emptyList()))
    mockItemUiState =
        MutableStateFlow(
            ItemUIState(
                Item("", Category("", ""), "", "", Visibility.PUBLIC, 1, Location("")), user))
    mockItemUiState2 = MutableStateFlow(ItemUIState(item2, user))
    mockItemUiState3 = MutableStateFlow(ItemUIState(item3, user))

    mockHomeUiState = MutableStateFlow(HomeUIState(user))
    mockNavActions = mockk()
    mockHomeViewModel = mockk()
    mockInventoryViewModel = mockk()
    mockManageViewModelIncoming = mockk()
    mockManageViewModelOutgoing = mockk()

    mockItemViewModel = mockk()
    mockStampViewModel = mockk()
    mockBorrowViewModel = mockk()
    mockLocationViewModel = mockk()
    mockUserViewModel = mockk()

    every { mockBorrowViewModel.startBorrow(any(), any()) } just Runs

    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.CREATE_ITEM) } just Runs
    every { mockNavActions.navigateTo(Route.VIEW_ITEM) } just Runs
    every { mockNavActions.navigateTo(Route.EDIT_ITEM) } just Runs
    every { mockNavActions.goBack() } just Runs
    every { mockNavActions.navigateTo(TOP_LEVEL_DESTINATIONS[2]) } just Runs

    every { mockItemViewModel.updateUiItem(any()) } just Runs

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

    every { mockItemViewModel.compareIDs(any(), any()) } returns true

    every { mockFirebaseUser.uid } returns "1234"
    every { mockFirebaseUser.displayName } returns "name"
    every { mockFirebaseUser.email } returns "email"

    every { mockManageViewModelIncoming.getCount() } returns 0
    every { mockManageViewModelOutgoing.getCount() } returns 0
  }

  // Navigate from Home screen to Inventory screen
  @Test
  fun testA_goFromHomeToInventory() {
    every { mockManageViewModelIncoming.uiState } returns mockManageUiState
    every { mockItemViewModel.uiState } returns mockItemUiState
    every { mockHomeViewModel.uiState } returns mockHomeUiState

    composeTestRule.setContent {
      HomeScreen(
          homeViewModel = mockHomeViewModel,
          manageLoanViewModel = mockManageViewModelIncoming,
          navigationActions = mockNavActions)
    }

    composeTestRule.onNodeWithText("Inventory").performClick()

    coVerify(exactly = 1) { mockNavActions.navigateTo(TOP_LEVEL_DESTINATIONS[2]) }
  }

  // Navigate from Inventory screen to Create Item screen
  @Test
  fun testB_InventoryEmptyToCreate() {
    every { mockInventoryViewModel.uiState } returns mockUiState
    every { mockItemViewModel.uiState } returns mockItemUiState

    composeTestRule.setContent {
      InventoryScreen(
          inventoryViewModel = mockInventoryViewModel,
          navigationActions = mockNavActions,
          manageLoanViewModelIncoming = mockManageViewModelIncoming,
          manageLoanViewModelOutgoing = mockManageViewModelOutgoing,
          itemViewModel = mockItemViewModel)
    }

    composeTestRule
        .onNodeWithText(
            "You have no items in your inventory, click on the + button to add your first item")
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("inventoryScreenFab").performClick()

    coVerify(exactly = 1) { mockNavActions.navigateTo(Route.CREATE_ITEM) }
  }

  // Create an item
  @Test
  fun testC_CreateItem() {
    val savedItem = slot<Item>()

    every { mockInventoryViewModel.uiState } returns mockUiState
    every { mockItemViewModel.uiState } returns mockItemUiState
    every { mockItemViewModel.updateUiItem(any()) } just Runs
    every { mockItemViewModel.save(capture(savedItem)) } just Runs

    val androidLocation2 = Location("")
    androidLocation2.latitude = 12.0
    androidLocation2.longitude = 12.0
    androidLocation2.extras = bundleOf("display_name" to "Lausanne")

    val androidLocation3 = Location("")
    androidLocation3.latitude = 13.0
    androidLocation3.longitude = 13.0
    androidLocation3.extras = bundleOf("display_name" to "Paris")

    val expectedItem =
        Item(
            "",
            Category("", CategoryItems[1]),
            "Object 1",
            "Description 1",
            Visibility.PUBLIC,
            2,
            androidLocation3,
            imageId = File("default-image.jpg"))

    every { mockLocationViewModel.getLocation(any(), any()) } answers
        {
          val loc = secondArg<MutableState<com.android.partagix.model.location.Location>>()
          loc.value = loc3
        }

    every { mockLocationViewModel.getLocation("Lausanne", any()) } answers
        {
          val loc = secondArg<MutableState<com.android.partagix.model.location.Location>>()
          loc.value = loc2
        }
    every { mockLocationViewModel.ourLocationToAndroidLocation(loc2) } returns androidLocation2

    every { mockLocationViewModel.ourLocationToAndroidLocation(loc3) } returns androidLocation3

    composeTestRule.setContent {
      InventoryCreateOrEditItem(
          itemViewModel = mockItemViewModel,
          navigationActions = mockNavActions,
          locationViewModel = mockLocationViewModel,
          mode = "create")
    }

    composeTestRule.onNodeWithTag("name").performTextInput("Object 1")
    composeTestRule.onNodeWithTag("description").performTextInput("Description 1")
    composeTestRule.onNodeWithTag("quantity").performTextReplacement("2")
    composeTestRule.onNodeWithTag("visibility").performClick()
    composeTestRule.onNodeWithText("Everyone").performClick()

    composeTestRule.onNodeWithTag("addressField").performScrollTo()
    composeTestRule.onNodeWithTag("addressField").performClick()
    composeTestRule.onNodeWithTag("addressField").performTextReplacement("Paris")
    composeTestRule.onNodeWithTag("category").performClick()
    composeTestRule.onNodeWithTag("addressField").performClick()

    composeTestRule.onNodeWithTag("category").performScrollTo()
    composeTestRule.onNodeWithTag("category").performClick()
    composeTestRule.onNodeWithText(CategoryItems[1]).performScrollTo()
    composeTestRule.onNodeWithText(CategoryItems[1]).performClick()

    composeTestRule.onNodeWithText("Create").performScrollTo()
    composeTestRule.onNodeWithText("Create").performClick()

    coVerify(exactly = 1) { mockItemViewModel.save(any()) }
    coVerify(exactly = 1) { mockNavActions.goBack() }

    assertEquals(expectedItem, savedItem.captured)
  }

  // Go back to the Inventory screen and check if there is the created item
  @Test
  fun testD_InventoryOneItem() {
    every { mockInventoryViewModel.uiState } returns mockUiState2
    every { mockItemViewModel.uiState } returns mockItemUiState2

    composeTestRule.setContent {
      InventoryScreen(
          inventoryViewModel = mockInventoryViewModel,
          navigationActions = mockNavActions,
          manageLoanViewModelIncoming = mockManageViewModelIncoming,
          manageLoanViewModelOutgoing = mockManageViewModelOutgoing,
          itemViewModel = mockItemViewModel)
    }

    composeTestRule.onNodeWithText("Object 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Object 1").performClick()

    coVerify(exactly = 1) { mockNavActions.navigateTo(Route.VIEW_ITEM) }
  }

  // Go on the created item and check if the fields are correct
  @Test
  fun testE_goToViewItem() {
    every { mockInventoryViewModel.uiState } returns mockUiState2
    every { mockItemViewModel.uiState } returns mockItemUiState2

    composeTestRule.setContent {
      InventoryViewItemScreen(
          navigationActions = mockNavActions,
          itemViewModel = mockItemViewModel,
          borrowViewModel = mockBorrowViewModel,
          userViewModel = mockUserViewModel)
    }

    composeTestRule.onNodeWithText("Object 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Description 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("2").assertIsDisplayed()
    composeTestRule.onNodeWithText("Everyone").assertIsDisplayed()
    composeTestRule.onNodeWithText(CategoryItems[1]).assertIsDisplayed()
    composeTestRule.onNodeWithText("Edit").performScrollTo()
    composeTestRule.onNodeWithText("Edit").performClick()

    coVerify(exactly = 1) { mockNavActions.navigateTo(Route.EDIT_ITEM) }
  }

  // Edit the item
  @Test
  fun testF_EditItem() {
    mockLocationViewModel = mockk()
    every { mockLocationViewModel.getLocation(any(), any()) } answers
        {
          val loc = secondArg<MutableState<com.android.partagix.model.location.Location>>()
          loc.value = loc2
        }
    every { mockLocationViewModel.ourLocationToAndroidLocation(loc2) } returns Location("")
    every { mockItemViewModel.uiState } returns mockItemUiState2
    every { mockItemViewModel.updateUiItem(any()) } just Runs
    every { mockItemViewModel.save(any()) } just Runs

    composeTestRule.setContent {
      InventoryCreateOrEditItem(
          itemViewModel = mockItemViewModel,
          navigationActions = mockNavActions,
          locationViewModel = mockLocationViewModel,
          mode = "edit")
    }

    composeTestRule.onNodeWithTag("name").performTextReplacement("Object 1 edited")
    composeTestRule.onNodeWithTag("description").performTextReplacement("Description 1 edited")
    composeTestRule.onNodeWithTag("quantity").performTextReplacement("3")
    composeTestRule.onNodeWithText("Save").performScrollTo()
    composeTestRule.onNodeWithText("Save").performClick()

    coVerify(exactly = 1) { mockItemViewModel.save(any()) }
    coVerify(exactly = 1) { mockNavActions.goBack() }
  }

  // Go back and check that the field have been updated
  @Test
  fun testG_viewEditedToInventory() {
    every { mockInventoryViewModel.uiState } returns mockUiState3
    every { mockItemViewModel.uiState } returns mockItemUiState3

    composeTestRule.setContent {
      InventoryViewItemScreen(
          navigationActions = mockNavActions,
          itemViewModel = mockItemViewModel,
          borrowViewModel = mockBorrowViewModel,
          userViewModel = mockUserViewModel)
    }

    composeTestRule.onNodeWithText("Object 1 edited").assertIsDisplayed()
    composeTestRule.onNodeWithText("Description 1 edited").assertIsDisplayed()
    composeTestRule.onNodeWithText("3").assertIsDisplayed()
    composeTestRule.onNodeWithText("Everyone").assertIsDisplayed()
    composeTestRule.onNodeWithText(CategoryItems[1]).assertIsDisplayed()
    composeTestRule.onNodeWithTag("navigationIcon").performClick()

    coVerify(exactly = 1) { mockNavActions.goBack() }
  }

  // Go back to the Inventory screen and check if the edited item is displayed
  @Test
  fun testH_inventoryWithEditedItem() {
    every { mockInventoryViewModel.uiState } returns mockUiState3

    composeTestRule.setContent {
      InventoryScreen(
          inventoryViewModel = mockInventoryViewModel,
          navigationActions = mockNavActions,
          manageLoanViewModelIncoming = mockManageViewModelIncoming,
          manageLoanViewModelOutgoing = mockManageViewModelOutgoing,
          itemViewModel = mockItemViewModel)
    }

    composeTestRule.onNodeWithText("Object 1 edited").assertIsDisplayed()
    composeTestRule.onNodeWithText("Object 1 edited").performClick()
  }

  // Generate a QR code for the edited item
  @Test
  fun testI_generateQRCode() {
    every { mockStampViewModel.generateQRCodeAndSave(any(), any(), any()) } just Runs

    composeTestRule.setContent { StampScreen(Modifier, mockStampViewModel, "1234", mockNavActions) }
    composeTestRule.onNodeWithTag("labelTextField").performTextReplacement("label of QR code")

    composeTestRule.onNodeWithTag("dimensionBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dimensionBox").performClick()
    composeTestRule.onNodeWithText(StampDimension.MEDIUM.detailedDimension).performClick()

    composeTestRule.onNodeWithText("Download stamps").performClick()

    coVerify(exactly = 1) {
      mockStampViewModel.generateQRCodeAndSave(
          "1234", "label of QR code", StampDimension.MEDIUM.detailedDimension)
    }
  }
}
