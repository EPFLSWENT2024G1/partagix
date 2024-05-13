package com.android.partagix

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.Database
import com.android.partagix.model.EvaluationViewModel
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.util.Date
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EvaluationViewModelTests {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var evaluationViewModel: EvaluationViewModel
  lateinit var db: Database
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
  val loan2 =
      Loan(
          "",
          "idOwner2",
          "idLoaner2",
          "item2",
          Date(),
          Date(),
          "2.0",
          "2.0",
          "commented",
          "commented",
          LoanState.FINISHED)

  var onSuccessLoan: (List<Loan>) -> Unit = {}

  @Before
  fun setUp() {
    db = mockk<Database>()
    every { db.getLoans(any()) } answers
        { invocation ->
          onSuccessLoan = invocation.invocation.args[0] as (List<Loan>) -> Unit
          onSuccessLoan(listOf(loan1, loan2))
        }
    every { db.setReview(any(), any(), any(), any()) } answers {}
    evaluationViewModel = EvaluationViewModel(loan1, db)
  }

  @Test
  fun updateAndReviewLoanTests() {
    assert(evaluationViewModel.uiState.value.loan == loan1)
    evaluationViewModel.updateUIState(loan2)
    assert(evaluationViewModel.uiState.value.loan == loan2)
    evaluationViewModel.reviewLoan(loan1, 5.0, "commented", "idOwner1")
    coVerify { db.setReview(loan1.id, "idOwner1", 5.0, "commented") }
  }
}
