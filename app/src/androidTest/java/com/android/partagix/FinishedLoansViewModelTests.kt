package com.android.partagix

import android.location.Location
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.Database
import com.android.partagix.model.FinishedLoansViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.category.Category
import com.android.partagix.model.emptyConst.emptyItem
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.visibility.Visibility
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import java.util.Date
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FinishedLoansViewModelTests {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var db: Database
  lateinit var finishedLoansViewModel: FinishedLoansViewModel
  lateinit var mockUser: FirebaseUser

  val loan1 =
      Loan(
          "",
          "Luke",
          "Cedric",
          "item1",
          Date(),
          Date(),
          "",
          "5.0",
          "",
          "commented",
          LoanState.FINISHED)
  val loan2 =
      Loan(
          "",
          "Cedric",
          "Luke",
          "item2",
          Date(),
          Date(),
          "2.0",
          "2.0",
          "commented",
          "commented",
          LoanState.FINISHED)

  val loan3 =
      Loan(
          "",
          "NotCedric",
          "Luke",
          "item3",
          Date(),
          Date(),
          "",
          "3.0",
          "commented",
          "commented",
          LoanState.FINISHED)

  val loan4 =
      Loan(
          "",
          "Luke",
          "NotCedric",
          "item3",
          Date(),
          Date(),
          "",
          "3.0",
          "commented",
          "commented",
          LoanState.FINISHED)
  val loan5 =
      Loan(
          "",
          "Cedric",
          "Luke",
          "item3",
          Date(),
          Date(),
          "",
          "3.0",
          "commented",
          "commented",
          LoanState.ACCEPTED)
  val item =
      Item(
          "item1",
          Category("GpWpDVqb1ep8gm2rb1WL", "Others"),
          "",
          "",
          Visibility.PRIVATE,
          0,
          Location(""))

  var onSuccessLoan: (List<Loan>) -> Unit = {}

  @Before
  fun setUp() {
    db = mockk<Database>()
    every { db.getLoans(any()) } answers
        { invocation ->
          onSuccessLoan = invocation.invocation.args[0] as (List<Loan>) -> Unit
          onSuccessLoan(listOf(loan1, loan2, loan3, loan4, loan5))
        }

    every { db.getItem(any(), any()) } answers
        { invocation ->
          val id = invocation.invocation.args[0] as String
          val onSuccess = invocation.invocation.args[1] as (Item) -> Unit
          onSuccess(emptyItem)
        }

    finishedLoansViewModel = FinishedLoansViewModel(db)

    mockUser = mockk<FirebaseUser>()
    mockkObject(Authentication)
  }

  @Test
  fun getFinishedLoanUserTests() {
    every { Authentication.getUser() } returns mockUser
    every { mockUser.uid } returns "Cedric"
    // check there is no loan before calling getFinishedLoan
    assert(finishedLoansViewModel.uiState.value.loans.isEmpty())
    finishedLoansViewModel.getFinishedLoan()
    // check that the correct loans are fetched
    assert(finishedLoansViewModel.uiState.value.loans.size == 2)
    assert(
        finishedLoansViewModel.uiState.value.loans.containsAll(
            listOf(Pair(loan1, emptyItem), Pair(loan2, emptyItem))))
  }

  @Test
  fun getFinishedLoanNoUserTests() {
    every { Authentication.getUser() } returns null
    finishedLoansViewModel.getFinishedLoan()
    // check that the loans are empty when there is no user
    assert(finishedLoansViewModel.uiState.value.loans.isEmpty())
  }
}
