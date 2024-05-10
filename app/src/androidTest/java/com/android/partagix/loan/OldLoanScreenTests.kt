package com.android.partagix.loan

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.Database
import com.android.partagix.model.EvaluationViewModel
import com.android.partagix.model.FinishedLoansViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.screens.OldLoansScreen
import com.android.partagix.ui.screens.ExpandableCard
import com.android.partagix.ui.screens.OldLoansScreen
import com.google.firebase.auth.FirebaseUser
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import java.util.Date
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OldLoanScreenTests {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var finishedLoansViewModel: FinishedLoansViewModel
  lateinit var evaluationViewModel: EvaluationViewModel
  lateinit var db: Database
  lateinit var mockUser: FirebaseUser
  val loan1 =
      Loan(
          "",
          "idOwner1",
          "idLoaner1",
          "item1",
          Date(),
          Date(),
          "",
          "5.0",
          "",
          "commented",
          LoanState.FINISHED)

  val item1 =
      Item(
          "item1",
          Category("GpWpDVqb1ep8gm2rb1WL", "Others"),
          "Object",
          "description",
          Visibility.PRIVATE,
          0,
          Location(""))

  var onSuccessLoan: (List<Loan>) -> Unit = {}

  @Before
  fun setup() {
    db = mockk<Database>()
    every { db.getLoans(any()) } answers
        { invocation ->
          onSuccessLoan = invocation.invocation.args[0] as (List<Loan>) -> Unit
          onSuccessLoan(listOf(loan1))
        }

    every { db.getItem(any(), any()) } answers
        {
          val id = invocation.args[0] as String
          val onSuccess: (Item) -> Unit = invocation.args[1] as (Item) -> Unit
          onSuccess(item1)
        }

    mockUser = mockk<FirebaseUser>()
    mockkObject(Authentication)
    every { Authentication.getUser() } returns mockUser
    every { mockUser.uid } returns "idOwner1"

    finishedLoansViewModel = FinishedLoansViewModel(db)
    finishedLoansViewModel.getFinishedLoan()

    evaluationViewModel = EvaluationViewModel(db = db)
  }

  @Test
  fun testOldLoanScreen() {
    composeTestRule.setContent {
      OldLoansScreen(
          finishedLoansViewModel = finishedLoansViewModel,
          itemViewModel = mockk(),
          evaluationViewModel = evaluationViewModel,
          navigationActions = mockk())
    }
    onComposeScreen<OldLoansScreen>(composeTestRule) {
      noOldLoans { assertIsNotDisplayed() }
      expandableCard { assertIsDisplayed() }
      composeTestRule.onNodeWithText("Object").assertIsDisplayed()
      expandableCard { performClick() }
      composeTestRule.onNodeWithText("Infos").assertIsDisplayed()
      composeTestRule.onNodeWithText("Infos").assertHasClickAction()
      composeTestRule.onNodeWithText("Evaluate").assertIsDisplayed()
      composeTestRule.onNodeWithText("Evaluate").assertHasClickAction()
    }
  }

  @Test
  fun expendableCard() {
    composeTestRule.setContent {
      var open by remember { mutableStateOf(false) }
      ExpandableCard(
          loan = loan1,
          item = item1,
          navigationActions = mockk(),
          itemViewModel = mockk(),
          evaluationViewModel = evaluationViewModel,
          open = open,
          setOpen = { open = it })
    }
    composeTestRule.onNodeWithText("Object").performClick()
    composeTestRule.onNodeWithText("Evaluate").assertHasClickAction()
    composeTestRule.onNodeWithText("Infos").assertHasClickAction()
  }
}
