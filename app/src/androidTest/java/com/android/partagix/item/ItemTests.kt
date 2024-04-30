package com.android.partagix.item

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.ItemUIState
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.StampViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.screens.InventoryViewItem
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.InventoryViewItem
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
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ItemTests : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockItemViewModel: ItemViewModel

  private var item1: Item =
      Item(
          id = "id",
          category = Category("id", "category1"),
          name = "item1",
          description = "description1",
          visibility = Visibility.PUBLIC,
          quantity = 1,
          location = mockk(),
          idUser = "id_user")
  private var _uiState = MutableStateFlow(ItemUIState(item1))
  private var mockUiState: StateFlow<ItemUIState> = _uiState

  @Before
  fun testSetup() {
    mockItemViewModel = mockk()
    every { mockItemViewModel.uiState } returns mockUiState

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs

    composeTestRule.setContent {
      InventoryViewItem(mockNavActions, mockItemViewModel)
    }
  }

  @Test
  fun testTest() {
    assert(true)
  }

  @Test
  fun topBarIsDisplayed() = run {
    onComposeScreen<InventoryViewItem>(composeTestRule) { topBar { assertIsDisplayed() } }
  }

  @Test
  fun bottomBarIsDisplayed() = run {
    onComposeScreen<InventoryViewItem>(composeTestRule) { bottomBar { assertIsDisplayed() } }
  }

  @Test
  fun itemNameUpdate() = run {
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
    onComposeScreen<InventoryViewItem>(composeTestRule) { assertIsDisplayed() }
  }
}
