package com.android.partagix.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.Database
import com.android.partagix.model.EvaluationViewModel
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.notification.FirebaseMessagingService
import com.android.partagix.screens.EvaluationPopUp
import com.android.partagix.ui.components.EvaluationPopUp
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.util.Date
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EvaluationPopUpTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var mockEvaluationViewModel: EvaluationViewModel
  lateinit var db: Database

  @RelaxedMockK lateinit var mockNotificationManager: FirebaseMessagingService

  val loanEmptyCommentAndRating =
      Loan(
          "",
          "idLender1",
          "idBorrower1",
          "item1",
          Date(),
          Date(),
          "",
          "",
          "",
          "",
          LoanState.FINISHED)
  val loanAlreadyRate =
      Loan(
          "",
          "idLender2",
          "idBorrower2",
          "item2",
          Date(),
          Date(),
          "2.0",
          "2.0",
          "commented",
          "commented",
          LoanState.FINISHED)
  val loanRatedButNoComment =
      Loan(
          "",
          "idLender3",
          "idBorrower3",
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
          onSuccessLoan(listOf(loanEmptyCommentAndRating, loanAlreadyRate, loanRatedButNoComment))
        }
    every { db.setReview(any(), any(), any(), any()) } answers {}

    mockNotificationManager = mockk()

    mockEvaluationViewModel =
        spyk(EvaluationViewModel(loanEmptyCommentAndRating, db, mockNotificationManager))

    every { mockEvaluationViewModel.reviewLoan(any(), any(), any(), any()) } just Runs
  }

  /** Test if the content is displayed and works. */
  @Test
  fun contentIsDisplayedAndWorks() {
    composeTestRule.setContent {
      EvaluationPopUp(
          loan = loanEmptyCommentAndRating, userId = "", viewModel = mockEvaluationViewModel)
    }
    onComposeScreen<EvaluationPopUp>(composeTestRule) {
      rateText { assertIsDisplayed() }
      rateStars { assertIsDisplayed() }
      commentText { assertIsDisplayed() }
      commentField { assertIsDisplayed() }
      evaluateButton { assertIsDisplayed() }
      closeButton { assertIsDisplayed() }

      evaluateButton { assertIsNotEnabled() }
      commentField { performTextReplacement("test") }
      commentField { assertTextEquals("test") }
      onNode { hasTestTag("star2") }.performClick()
      closeButton { performClick() }
    }
    composeTestRule.onNodeWithTag("evaluationPopUp").assertDoesNotExist()
  }

  @Test
  fun evaluationWorksForNoPreviousEvaluation() {
    composeTestRule.setContent {
      EvaluationPopUp(
          loan = loanEmptyCommentAndRating,
          userId = "idLender1",
          viewModel = mockEvaluationViewModel)
    }
    onComposeScreen<EvaluationPopUp>(composeTestRule) {
      onNode { hasTestTag("star4") }.performClick()
      evaluateButton { assertHasClickAction() }
      commentField { performTextReplacement("comment") }
      evaluateButton { assertHasClickAction() }
      evaluateButton { performClick() }
    }
    composeTestRule.onNodeWithTag("evaluationPopUp").assertDoesNotExist()
    coVerify {
      mockEvaluationViewModel.reviewLoan(loanEmptyCommentAndRating, 5.0, "comment", "idBorrower1")
    }
  }

  @Test
  fun evaluationWorksForAlreadyCommentedAndRated() {
    composeTestRule.setContent {
      EvaluationPopUp(
          loan = loanAlreadyRate, userId = "idLender2", viewModel = mockEvaluationViewModel)
    }
    onComposeScreen<EvaluationPopUp>(composeTestRule) {
      composeTestRule.onNodeWithTag("star2").assertHasClickAction()
      commentField { assertTextEquals("commented") }
      evaluateButton { assertIsEnabled() }
      onNode { hasTestTag("star4") }.performClick()
      commentField { performTextReplacement("comment") }
      closeButton { performClick() }
    }
    composeTestRule.onNodeWithTag("evaluationPopUp").assertDoesNotExist()
  }

  @Test
  fun evaluationWorksForAlreadyRated() {
    composeTestRule.setContent {
      EvaluationPopUp(
          loan = loanRatedButNoComment, userId = "idBorrower3", viewModel = mockEvaluationViewModel)
    }
    onComposeScreen<EvaluationPopUp>(composeTestRule) {
      composeTestRule.onNodeWithTag("star2").assertHasClickAction()
      composeTestRule.onNodeWithTag("star4").performClick()
      evaluateButton { assertHasClickAction() }
      commentField { performTextReplacement("comment") }
      evaluateButton { performClick() }
    }
    composeTestRule.onNodeWithTag("evaluationPopUp").assertDoesNotExist()
    verify(exactly = 1) {
      mockEvaluationViewModel.reviewLoan(loanRatedButNoComment, 5.0, "comment", "idLender3")
    }
  }
}
