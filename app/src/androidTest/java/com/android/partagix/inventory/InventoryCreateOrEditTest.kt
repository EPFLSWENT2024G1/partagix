package com.android.partagix.inventory

import android.location.Location
import android.net.Uri
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.DEFAULT_CATEGORY_ID
import com.android.partagix.model.DEFAULT_CATEGORY_NAME
import com.android.partagix.model.ItemUIState
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.screens.InventoryCreateOrEditScreen
import com.android.partagix.ui.components.locationPicker.LocationPickerViewModel
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
import io.mockk.slot
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
  @RelaxedMockK lateinit var mockLocationViewModel: LocationPickerViewModel

  private lateinit var emptyMockUiState: MutableStateFlow<ItemUIState>
  private lateinit var nonEmptyMockUiState: MutableStateFlow<ItemUIState>
  private lateinit var noCategoryMockUiState: MutableStateFlow<ItemUIState>

  private var savedItem = slot<Item>()

  val uri1 =
      Uri.parse(
          "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png")
  val uri2 =
      Uri.parse("https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_92x30dp.png")
  val uriList = listOf(uri1, uri2)

  @Before
  fun testSetup() {

    val emptyItem = Item("", Category("", ""), "", "", Visibility.PUBLIC, 1, Location(""))
    val emptyUser = User("", "", "", "", Inventory("", emptyList()))
    emptyMockUiState = MutableStateFlow(ItemUIState(emptyItem, emptyUser))
    val cat1 = Category("1", "Category 1")
    val vis1 = Visibility.PUBLIC
    val loc1 = Location("1")
    val item = Item("1", cat1, "Name 1", "Description 1", vis1, 1, loc1)

    val cat2 = Category(DEFAULT_CATEGORY_ID, DEFAULT_CATEGORY_NAME)
    val emptyCat = Category("", "Category")
    val vis2 = Visibility.PUBLIC
    val loc2 = Location("2")
    val noCategoryItem = Item("2", emptyCat, "Name 2", "Description 2", vis2, 2, loc2)

    nonEmptyMockUiState = MutableStateFlow(ItemUIState(item, emptyUser))
    noCategoryMockUiState = MutableStateFlow(ItemUIState(noCategoryItem, emptyUser))

    mockViewModel = mockk()
    // every { mockInventoryViewModel.uiState } returns emptyMockUiState
    every { mockViewModel.save(capture(savedItem)) } just Runs

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs
    every { mockNavActions.goBack() } just Runs
  }

  @Test
  fun topBarAndEmptyItemAreDisplayed() = run {
    every { mockViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryCreateOrEditItem(
          mockViewModel, mockNavActions, locationViewModel = mockLocationViewModel, mode = "")
    }

    onComposeScreen<InventoryCreateOrEditScreen>(composeTestRule) {
      // TopBar
      topBar { assertIsDisplayed() }

      navigationIcon { assertIsDisplayed() }

      // Empty item
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

  @Test
  fun titleOnEdit() = run {
    every { mockViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      InventoryCreateOrEditItem(
          mockViewModel, mockNavActions, locationViewModel = mockLocationViewModel, mode = "edit")
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
      InventoryCreateOrEditItem(
          mockViewModel, mockNavActions, locationViewModel = mockLocationViewModel, mode = "")
    }
    onComposeScreen<InventoryCreateOrEditScreen>(composeTestRule) {
      title {
        assertIsDisplayed()
        assertTextEquals("Create a new item")
      }
    }
  }

  @Test
  fun itemCreateTest() {
    every { mockViewModel.uiState } returns noCategoryMockUiState

    composeTestRule.setContent {
      InventoryCreateOrEditItem(
          mockViewModel, mockNavActions, locationViewModel = mockLocationViewModel, mode = "")
    }
    onComposeScreen<InventoryCreateOrEditScreen>(composeTestRule) {
      name { performTextReplacement("my object") }
      description { performTextReplacement("what a nice object") }
      button { performClick() }
      image { performClick() }

      assert(savedItem.captured.name == "my object")
      assert(savedItem.captured.description == "what a nice object")
      assert(savedItem.captured.category.name == "Category")
      assert(savedItem.captured.visibility == Visibility.PUBLIC)
    }
  }
}
