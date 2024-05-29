package com.android.partagix.home

import android.location.Location
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.partagix.model.HomeUIState
import com.android.partagix.model.HomeViewModel
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.ManagerUIState
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.user.User
import com.android.partagix.screens.HomeScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.HomeScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.Runs
import io.mockk.coVerify
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
class HomeTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockInventoryViewModel: InventoryViewModel
  @RelaxedMockK lateinit var mockHomeViewModel: HomeViewModel
  @RelaxedMockK lateinit var mockManageViewModel: ManageLoanViewModel

  private lateinit var mockUiState: MutableStateFlow<ManagerUIState>
  private lateinit var mockUiHomeState: MutableStateFlow<HomeUIState>

  @get:Rule
  val grantCameraPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

  @Before
  fun testSetup() {
    val cat1 = Category("1", "Category 1")
    val vis1 = com.android.partagix.model.visibility.Visibility.PUBLIC
    val loc1 = Location("1")
    val items = listOf(Item("1", cat1, "Name 1", "Description 1", vis1, 1, loc1))
    mockUiState = MutableStateFlow(ManagerUIState(items, emptyList(), emptyList(), listOf(false)))

    mockInventoryViewModel = mockk()
    mockHomeViewModel = mockk()
    mockManageViewModel = mockk()

    mockUiHomeState =
        MutableStateFlow(HomeUIState(User("1", "Name 1", "email", "phone", Inventory("1", items))))

    every { mockInventoryViewModel.getInventory(any()) } just Runs
    every { mockHomeViewModel.uiState } returns mockUiHomeState
    every { mockHomeViewModel.openQrScanner() } just Runs

    every { mockInventoryViewModel.findTime(any(), any(), any()) } just Runs
    every { mockInventoryViewModel.getUsers(any(), any()) } just Runs

    every { mockInventoryViewModel.filterItems(query = any()) } just Runs
    every { mockInventoryViewModel.filterItems(atLeastQuantity = any()) } just Runs

    every { mockManageViewModel.uiState } returns mockUiState
    every { mockManageViewModel.updateExpanded(any(), any()) } just Runs

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs
    every { mockNavActions.navigateTo(Route.INVENTORY) } just Runs
    every { mockNavActions.navigateTo(Route.QR_SCAN) } just Runs
    every { mockNavActions.navigateTo(Route.LOAN) } just Runs
    every { mockNavActions.navigateTo(Route.MANAGE_LOAN_REQUEST) } just Runs
    every { mockNavActions.navigateTo(Route.OTHER_ACCOUNT + "/") } just Runs

    composeTestRule.setContent {
      HomeScreen(
          homeViewModel = mockHomeViewModel,
          manageLoanViewModel = mockManageViewModel,
          navigationActions = mockNavActions)
    }
  }

  @Test
  fun contentIsDisplayed() = run {
    ComposeScreen.onComposeScreen<HomeScreen>(composeTestRule) {
      topBar { assertIsDisplayed() }
      mainContent { assertIsDisplayed() }
      bottomNavBar { assertIsDisplayed() }
      bottomNavBarItemInventory { assertIsDisplayed() }
      firstBigButton {
        assertIsDisplayed()
        performClick()
      }
      secondBigButton {
        assertIsDisplayed()
        performClick()
      }
      thirdBigButton {
        assertIsDisplayed()
        performClick()
      }
    }
  }

  @Test
  fun navigateToOtherAccount() {
    ComposeScreen.onComposeScreen<HomeScreen>(composeTestRule) {
      composeTestRule.onNodeWithText("noname").performClick()
      coVerify { mockNavActions.navigateTo(Route.OTHER_ACCOUNT + "/") }
    }
  }
}
