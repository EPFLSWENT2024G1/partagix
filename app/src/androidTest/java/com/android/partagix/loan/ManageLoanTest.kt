package com.android.partagix.loan

import android.location.Location
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.ManagerUIState
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.screens.ManageLoanScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.screens.ManageLoanRequest
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
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

class ManageLoanTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockManageViewModel: ManageLoanViewModel

  private lateinit var emptyMockUiState: MutableStateFlow<ManagerUIState>
  private lateinit var nonEmptyMockUiState: MutableStateFlow<ManagerUIState>

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

    mockManageViewModel = mockk()

    every { mockManageViewModel.getLoanRequests() } just Runs
    // every { mockManageViewModel.updateExpanded(any()) } just Runs

  }

  @Test
  fun noItemBoxIsDisplayed() = run {
    every { mockManageViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ManageLoanRequest(
          mockManageViewModel,
          mockNavActions,
      )
    }

    ComposeScreen.onComposeScreen<ManageLoanScreen>(composeTestRule) {
      noItemBox { assertIsDisplayed() }
    }
  }

  @Test
  fun noItemTextIsDisplayed() = run {
    every { mockManageViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ManageLoanRequest(
          mockManageViewModel,
          mockNavActions,
      )
    }

    ComposeScreen.onComposeScreen<ManageLoanScreen>(composeTestRule) {
      noItemText {
        assertIsDisplayed()
        assertTextEquals("There is no loan request.")
      }
    }
  }

  @Test
  fun bottomNavBarIsDisplayed() = run {
    every { mockManageViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ManageLoanRequest(
          mockManageViewModel,
          mockNavActions,
      )
    }

    ComposeScreen.onComposeScreen<ManageLoanScreen>(composeTestRule) {
      bottomNavBar { assertIsDisplayed() }
    }
  }

  @Test
  fun bottomNavBarItemInventoryIsDisplayed() = run {
    every { mockManageViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ManageLoanRequest(
          mockManageViewModel,
          mockNavActions,
      )
    }

    ComposeScreen.onComposeScreen<ManageLoanScreen>(composeTestRule) {
      bottomNavBarItemInventory { assertIsDisplayed() }
    }
  }

  @Test
  fun itemListIsDisplayed() = run {
    every { mockManageViewModel.uiState } returns nonEmptyMockUiState
    composeTestRule.setContent {
      ManageLoanRequest(
          mockManageViewModel,
          mockNavActions,
      )
    }

    ComposeScreen.onComposeScreen<ManageLoanScreen>(composeTestRule) {
      itemList { assertIsDisplayed() }
    }
  }
}
