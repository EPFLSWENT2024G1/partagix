package com.android.partagix.components

import android.location.Location
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.emptyConst.emptyInventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.ui.components.ItemUi
import io.mockk.coEvery
import io.mockk.mockk
import java.util.Date
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ItemUiTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var viewModel: ManageLoanViewModel
  private lateinit var item: Item
  private lateinit var user: User
  private lateinit var loan: Loan

  @Before
  fun setUp() {
    viewModel = mockk(relaxed = true)

    item =
        Item(
            id = "1",
            name = "Test Item",
            quantity = 1,
            category = Category("1", "Category 1"),
            description = "Test Description",
            location = Location("Test Location"),
            visibility = Visibility.PUBLIC,
        )

    user =
        User(
            id = "8WuTkKJZLTAr6zs5L7rH",
            name = "User1",
            rank = "5",
            email = "test@example.com",
            phoneNumber = "1234567890",
            telegram = "@testuser",
            favorite = listOf(true, true, true),
            address = "Test Address",
            inventory = emptyInventory)

    loan =
        Loan(
            id = "1",
            idItem = "1",
            idBorrower = "2",
            idLender = "1",
            state = LoanState.ACCEPTED,
            startDate = Date(),
            endDate = Date(),
            commentBorrower = "",
            commentLender = "",
            reviewBorrower = "0",
            reviewLender = "0",
        )

    coEvery { viewModel.getUser(any(), any()) } answers { secondArg<(User) -> Unit>().invoke(user) }
  }

  @Test
  fun testItemUiExpanded() {
    composeTestRule.setContent {
      ItemUi(
          item = item,
          user = user,
          loan = loan,
          manageLoanViewModel = viewModel,
          isExpandable = true,
          expandState = false,
          navigationActions = mockk(),
      )
    }

    composeTestRule.onNodeWithText("Test Item").assertIsDisplayed().performClick()

    // Verify preferred contact is displayed
    composeTestRule.onNodeWithText("Preferred contact: ").assertIsDisplayed()
    composeTestRule.onNodeWithText("Email : test@example.com").assertIsDisplayed()
    composeTestRule.onNodeWithText("Phone : 1234567890").assertIsDisplayed()
    composeTestRule.onNodeWithText("Telegram : @testuser").assertIsDisplayed()
  }
}
