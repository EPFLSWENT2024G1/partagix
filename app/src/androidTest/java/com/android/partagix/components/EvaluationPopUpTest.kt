package com.android.partagix.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.Database
import com.android.partagix.model.EvaluationViewModel
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.ui.components.EvaluationPopUp
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.util.Date
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EvaluationPopUpTest {
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
  val loan3 =
      Loan(
          "",
          "idOwner3",
          "idLoaner3",
          "item3",
          Date(),
          Date(),
          "3.0",
          "3.0",
          "",
          "",
          LoanState.FINISHED)

  var onSuccessLoan: (List<Loan>) -> Unit = {}

  @Before
  fun setUp() {
    db = mockk<Database>()
    every { db.getLoans(any()) } answers
        { invocation ->
          onSuccessLoan = invocation.invocation.args[0] as (List<Loan>) -> Unit
          onSuccessLoan(listOf(loan1, loan2, loan3))
        }
    every { db.setReview(any(), any(), any(), any()) } answers {}
    evaluationViewModel = EvaluationViewModel(loan1, db)
  }

  /** Test if the content is displayed and works. */
  @Test
  fun contentIsDisplayedAndWorks() {
    composeTestRule.setContent {
      EvaluationPopUp(loan = loan1, userId = "", viewModel = evaluationViewModel)
    }
    onComposeScreen<EvaluationPopUp>(composeTestRule) {
      rateText { assertIsDisplayed() }
      rateStars { assertIsDisplayed() }
      validateButton { assertIsDisplayed() }
      commentText { assertIsDisplayed() }
      commentField { assertIsDisplayed() }
      commentButton { assertIsDisplayed() }
      closeButton { assertIsDisplayed() }

      commentButton { assertIsNotEnabled() }
      commentField { performTextReplacement("test") }
      commentButton { assertIsEnabled() }
      commentButton { performClick() }
      commentField { assertTextEquals("test") }

      validateButton { assertIsNotEnabled() }
      onNode { hasTestTag("star2") }.performClick()
      validateButton { assertIsEnabled() }
      validateButton { performClick() }
      closeButton { performClick() }
    }
    composeTestRule.onNodeWithTag("evaluationPopUp").assertDoesNotExist()
  }

  @Test
  fun evaluationWorksForNoPreviousEvaluation() {
    composeTestRule.setContent {
      EvaluationPopUp(loan = loan1, userId = "idOwner1", viewModel = evaluationViewModel)
    }
    onComposeScreen<EvaluationPopUp>(composeTestRule) {
      onNode { hasTestTag("star4") }.performClick()
      validateButton { assertIsEnabled() }
      validateButton { performClick() }
      commentField { performTextReplacement("comment") }
      commentButton { assertIsEnabled() }
      commentButton { performClick() }
      alreadyEvaluated { assertIsDisplayed() }
      closeButton { performClick() }
    }
    composeTestRule.onNodeWithTag("evaluationPopUp").assertDoesNotExist()
    coVerify { evaluationViewModel.reviewLoan(loan1, 5.0, "comment", "idOwner1") }
  }

  @Test
  fun evaluationWorksForAlreadyCommentedAndRated() {
    composeTestRule.setContent {
      EvaluationPopUp(loan = loan2, userId = "idOwner2", viewModel = evaluationViewModel)
    }
    onComposeScreen<EvaluationPopUp>(composeTestRule) {
      rateStars { assertHasNoClickAction() }
      Thread.sleep(1000)
      validateButton { assertIsNotEnabled() }
      commentField { assertTextEquals("commented") }
      commentButton { assertIsNotEnabled() }
      alreadyEvaluated { assertIsDisplayed() }
      closeButton { performClick() }
    }
    composeTestRule.onNodeWithTag("evaluationPopUp").assertDoesNotExist()
  }

  @Test
  fun evaluationWorksForAlreadyRated() {
    composeTestRule.setContent {
      EvaluationPopUp(loan = loan3, userId = "idLoaner3", viewModel = evaluationViewModel)
    }
    onComposeScreen<EvaluationPopUp>(composeTestRule) {
      rateStars { assertHasNoClickAction() }
      validateButton { assertIsNotEnabled() }
      commentField { performTextReplacement("comment") }
      commentButton { assertIsEnabled() }
      commentButton { performClick() }
      alreadyEvaluated { assertIsDisplayed() }
      closeButton { performClick() }
    }
    composeTestRule.onNodeWithTag("evaluationPopUp").assertDoesNotExist()
    coVerify(exactly = 1) { evaluationViewModel.reviewLoan(loan3, 3.0, "comment", "idLoaner3") }
  }
}
