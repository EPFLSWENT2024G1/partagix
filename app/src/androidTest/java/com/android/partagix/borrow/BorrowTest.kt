package com.android.partagix.borrow

import android.location.Location
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.core.os.bundleOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.BorrowViewModel
import com.android.partagix.model.ItemUIState
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.screens.BorrowScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.BorrowScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BorrowTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockViewModel: BorrowViewModel
  @RelaxedMockK lateinit var mockItemViewModel: ItemViewModel

  private lateinit var mockLoanUiState: MutableStateFlow<Loan>
  private lateinit var mockItemUiState: MutableStateFlow<Item>
  private lateinit var mockUserUiState: MutableStateFlow<User>
  private lateinit var mockItemViewUiState: StateFlow<ItemUIState>
  private lateinit var mockItemAvailability: StateFlow<Boolean>

  val cat1 = Category("1", "Category 1")
  val vis1 = Visibility.PUBLIC
  val loc1 = mockk<Location>()
  val item = Item("1", cat1, "Name 1", "Description 1", vis1, 1, loc1)
  val loan = Loan("1", "1", "1", "1", Date(), Date(), "1", "1", "1", "1", LoanState.PENDING)
  val user = User("1", "Name 1", "Email 1", "Phone 1", Inventory("1", emptyList()))

  @Before
  fun testSetup() {
    mockLoanUiState = MutableStateFlow(loan)
    mockItemUiState = MutableStateFlow(item)
    mockUserUiState = MutableStateFlow(user)
    mockItemViewUiState = MutableStateFlow(ItemUIState(item, user))
    mockItemAvailability = MutableStateFlow(true)

    mockItemViewModel = mockk()
    every { mockItemViewModel.uiState } returns mockItemViewUiState

    every { loc1.latitude } returns 1.0
    every { loc1.longitude } returns 1.0
    every { loc1.extras } returns bundleOf("display_name" to "Location 1")

    mockViewModel = mockk()
    every { mockViewModel.itemAvailability } returns mockItemAvailability
    every { mockViewModel.loanUiState } returns mockLoanUiState
    every { mockViewModel.itemUiState } returns mockItemUiState
    every { mockViewModel.userUiState } returns mockUserUiState
    every { mockViewModel.startBorrow(any(), any()) } just Runs
    every { mockViewModel.updateLoan(any()) } just Runs
    every { mockViewModel.createLoan(any()) } just Runs

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOAN) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs
    every { mockNavActions.navigateTo(Route.LOAN) } just Runs

    every { mockNavActions.goBack() } just Runs
  }

  @Test
  fun testIsDisplayed() {
    composeTestRule.setContent {
      BorrowScreen(
          viewModel = mockViewModel,
          navigationActions = mockNavActions,
          itemViewModel = mockItemViewModel)
    }

    onComposeScreen<BorrowScreen>(composeTestRule) {
      assertIsDisplayed()
      topBar { assertIsDisplayed() }
      backText { assertIsDisplayed() }
      backButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      itemImage { assertIsDisplayed() }
      itemName { assertIsDisplayed() }
      itemOwner { assertIsDisplayed() }
      description { assertIsDisplayed() }
      location { assertIsDisplayed() }
      composeTestRule.onNodeWithText("Location 1").assertIsDisplayed()
      startDate { assertIsDisplayed() }
      startDateButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      startDateCancel {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      endDate { assertIsDisplayed() }
      endDateButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      endDateCancel {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      saveButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun editAndSave() {
    composeTestRule.setContent {
      BorrowScreen(
          viewModel = mockViewModel,
          navigationActions = mockNavActions,
          itemViewModel = mockItemViewModel)
    }

    mockViewModel.startBorrow(item, user)

    onComposeScreen<BorrowScreen>(composeTestRule) {
      startDateButton { performClick() }
      startDateOk { performClick() }
      endDateButton { performClick() }
      endDateOk { performClick() }
      saveButton { performClick() }
    }
  }

  @Test
  fun testPopup() {
    every { mockViewModel.createLoan { any() } } just runs
    every { mockViewModel.updateItemAvailability(any()) } just runs
    composeTestRule.setContent {
      BorrowScreen(
          viewModel = mockViewModel,
          navigationActions = mockNavActions,
          itemViewModel = mockItemViewModel)
    }

    onComposeScreen<BorrowScreen>(composeTestRule) { popup { assertIsDisplayed() } }
  }
}
