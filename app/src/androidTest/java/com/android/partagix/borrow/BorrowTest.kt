package com.android.partagix.borrow

import android.location.Location
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.BorrowViewModel
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
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BorrowTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockViewModel: BorrowViewModel

  private lateinit var mockLoanUiState: MutableStateFlow<Loan>
  private lateinit var mockItemUiState: MutableStateFlow<Item>
  private lateinit var mockUserUiState: MutableStateFlow<User>

  val cat1 = Category("1", "Category 1")
  val vis1 = Visibility.PUBLIC
  val loc1 = Location("1")
  val item = Item("1", cat1, "Name 1", "Description 1", vis1, 1, loc1)
  val loan = Loan("1", "1", "1", "1", Date(), Date(), "1", "1", "1", "1", LoanState.PENDING)
  val user = User("1", "Name 1", "Email 1", "Phone 1", Inventory("1", emptyList()))

  @Before
  fun testSetup() {
    mockLoanUiState = MutableStateFlow(loan)
    mockItemUiState = MutableStateFlow(item)
    mockUserUiState = MutableStateFlow(user)

    mockViewModel = mockk()
    every { mockViewModel.loanUiState } returns mockLoanUiState
    every { mockViewModel.itemUiState } returns mockItemUiState
    every { mockViewModel.userUiState } returns mockUserUiState
    every { mockViewModel.resetBorrow(any(), any()) } just Runs
    every { mockViewModel.updateLoan(any()) } just Runs
    every { mockViewModel.createLoan() } just Runs

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.LOGIN) } just Runs
    every { mockNavActions.goBack() } just Runs
  }

  @Test
  fun testIsDisplayed() {
    composeTestRule.setContent {
      BorrowScreen(viewModel = mockViewModel, navigationActions = mockNavActions)
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
      BorrowScreen(viewModel = mockViewModel, navigationActions = mockNavActions)
    }

    mockViewModel.resetBorrow(item, user)

    onComposeScreen<BorrowScreen>(composeTestRule) {
      description { performTextInput("test description") }
      startDateButton { performClick() }
      startDateOk { performClick() }
      endDateButton { performClick() }
      endDateOk { performClick() }
      saveButton { performClick() }
    }
  }
}
