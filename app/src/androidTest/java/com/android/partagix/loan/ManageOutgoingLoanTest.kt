package com.android.partagix.loan

import android.location.Location
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.ManagerUIState
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.screens.ManageOutgoingLoanScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.ManageOutgoingLoan
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
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ManageOutgoingLoanTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockManageViewModel: ManageLoanViewModel

  private lateinit var emptyMockUiState: MutableStateFlow<ManagerUIState>
  private lateinit var nonEmptyMockUiState: MutableStateFlow<ManagerUIState>
  @RelaxedMockK lateinit var mockItemViewModel: ItemViewModel

  @Before
  fun testSetup() {
    mockNavActions = mockk<NavigationActions>()
    emptyMockUiState =
        MutableStateFlow(
            ManagerUIState(
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList(),
            ))
    val cat1 = Category("1", "Category 1")
    val vis1 = com.android.partagix.model.visibility.Visibility.PUBLIC
    val loc1 = Location("1")
    val items = listOf(Item("1", cat1, "Name 1", "Description 1", vis1, 1, loc1))
    val users = listOf(User("2", "User 1", "", "", Inventory("1", emptyList())))
    val loan =
        listOf(
            Loan(
                "4",
                "2",
                "3",
                "1",
                Date(),
                Date(),
                "Borrowing requests",
                "Pending",
                "",
                "",
                LoanState.PENDING))
    val boolean = listOf(true)
    nonEmptyMockUiState = MutableStateFlow(ManagerUIState(items, users, loan, boolean))

    mockItemViewModel = mockk()
    mockManageViewModel = mockk()

    every { mockManageViewModel.getLoanRequests() } just Runs
    every { mockNavActions.navigateTo(Route.OTHER_ACCOUNT + "/2") } just Runs
  }

  @Test
  fun noItemBoxIsDisplayed() = run {
    every { mockManageViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ManageOutgoingLoan(
          mockManageViewModel,
          mockNavActions,
          mockItemViewModel,
      )
    }

    ComposeScreen.onComposeScreen<ManageOutgoingLoanScreen>(composeTestRule) {
      noLoanBox { assertIsDisplayed() }
    }
  }

  @Test
  fun noItemTextIsDisplayed() = run {
    every { mockManageViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ManageOutgoingLoan(
          mockManageViewModel,
          mockNavActions,
          mockItemViewModel,
      )
    }

    ComposeScreen.onComposeScreen<ManageOutgoingLoanScreen>(composeTestRule) {
      noItemText {
        assertIsDisplayed()
        assertTextEquals("You have no outgoing loan request.")
      }
    }
  }

  @Test
  fun bottomNavBarIsDisplayed() = run {
    every { mockManageViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ManageOutgoingLoan(
          mockManageViewModel,
          mockNavActions,
          mockItemViewModel,
      )
    }

    ComposeScreen.onComposeScreen<ManageOutgoingLoanScreen>(composeTestRule) {
      bottomNavBar { assertIsDisplayed() }
    }
  }

  @Test
  fun bottomNavBarItemInventoryIsDisplayed() = run {
    every { mockManageViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ManageOutgoingLoan(
          mockManageViewModel,
          mockNavActions,
          mockItemViewModel,
      )
    }

    ComposeScreen.onComposeScreen<ManageOutgoingLoanScreen>(composeTestRule) {
      bottomNavBarItemInventory { assertIsDisplayed() }
    }
  }

  @Test
  fun itemListIsDisplayed() = run {
    every { mockManageViewModel.uiState } returns nonEmptyMockUiState
    composeTestRule.setContent {
      ManageOutgoingLoan(
          mockManageViewModel,
          mockNavActions,
          expandable = true,
          itemViewModel = mockItemViewModel,
      )
    }

    ComposeScreen.onComposeScreen<ManageOutgoingLoanScreen>(composeTestRule) {
      mainContent { assertIsDisplayed() }
      itemList { assertIsDisplayed() }
      composeTestRule.onNodeWithText("User 1").performClick()
      coVerify { mockNavActions.navigateTo(Route.OTHER_ACCOUNT + "/2") }
    }
  }
}
