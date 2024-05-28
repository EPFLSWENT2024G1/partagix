package com.android.partagix

import com.android.partagix.model.Database
import com.android.partagix.model.StartOrEndLoanUIState
import com.android.partagix.model.StartOrEndLoanViewModel
import com.android.partagix.model.emptyConst.emptyItem
import com.android.partagix.model.emptyConst.emptyLoan
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.notification.FirebaseMessagingService
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class StartOrEndLoanViewModelTests {

  private lateinit var db: Database

  private lateinit var startOrEndLoanViewModel: StartOrEndLoanViewModel
  private val mockFirebaseMessagingService = mockk<FirebaseMessagingService>()

  @Before
  fun setup() {
    db = mockk<Database>()
    startOrEndLoanViewModel =
        StartOrEndLoanViewModel(db = db, notificationManager = mockFirebaseMessagingService)
  }

  @Test
  fun testUpdateUiState() {

    startOrEndLoanViewModel.update(
        StartOrEndLoanUIState(emptyLoan, emptyItem, emptyUser, emptyUser))
    assertEquals(
        emptyLoan,
        startOrEndLoanViewModel.uiState.value.loan,
    )
    assertEquals(emptyItem, startOrEndLoanViewModel.uiState.value.item)
    assertEquals(emptyUser, startOrEndLoanViewModel.uiState.value.borrower)
    assertEquals(emptyUser, startOrEndLoanViewModel.uiState.value.lender)
  }

  @Test
  fun testOnStartNullLenderToken() {

    every { db.setLoan(any()) } answers
        {
          val loan = firstArg<Loan>()
          assertEquals(LoanState.ONGOING, loan.state)
        }
    startOrEndLoanViewModel.update(
        StartOrEndLoanUIState(emptyLoan, emptyItem, emptyUser, emptyUser))
    startOrEndLoanViewModel.onStart()
    coVerify { db.setLoan(any()) }
  }

  @Test
  fun testOnStartValidLenderToken() {

    every { db.setLoan(any()) } answers
        {
          val loan = firstArg<Loan>()
          assertEquals(LoanState.ONGOING, loan.state)
        }

    every { mockFirebaseMessagingService.sendNotification(any(), any()) } just Runs

    val token = "token"
    val lender = emptyUser.copy(fcmToken = token)
    val item = emptyItem.copy(id = "id")

    startOrEndLoanViewModel.update(
        StartOrEndLoanUIState(emptyLoan, item = item, borrower = emptyUser, lender = lender))

    startOrEndLoanViewModel.onStart()

    coVerify { db.setLoan(any()) }
  }

  @Test
  fun testOnCancelNullLenderToken() {
    every { db.setLoan(any()) } answers
        {
          val loan = firstArg<Loan>()
          assertEquals(LoanState.CANCELLED, loan.state)
        }
    startOrEndLoanViewModel.update(
        StartOrEndLoanUIState(emptyLoan, emptyItem, emptyUser, emptyUser))
    startOrEndLoanViewModel.onCancel()
    coVerify { db.setLoan(any()) }
  }

  @Test
  fun testOnCancelValidLenderToken() {
    every { db.setLoan(any()) } answers
        {
          val loan = firstArg<Loan>()
          assertEquals(LoanState.CANCELLED, loan.state)
        }

    every { mockFirebaseMessagingService.sendNotification(any(), any()) } just Runs

    val token = "token"
    val lender = emptyUser.copy(fcmToken = token)
    val item = emptyItem.copy(id = "id")

    startOrEndLoanViewModel.update(
        StartOrEndLoanUIState(emptyLoan, item = item, borrower = emptyUser, lender = lender))

    startOrEndLoanViewModel.onCancel()

    coVerify { db.setLoan(any()) }
    coVerify {
      mockFirebaseMessagingService.sendNotification(
          match {
            it.title == "Loan cancelled" &&
                it.type == Notification.Type.NEW_INCOMING_REQUEST &&
                it.navigationUrl == "${Route.VIEW_ITEM}/${item.id}"
          },
          token)
    }
  }

  @Test
  fun testOnFinish() {

    every { db.setLoan(any()) } answers
        {
          val loan = firstArg<Loan>()
          assertEquals(LoanState.FINISHED, loan.state)
        }
    startOrEndLoanViewModel.update(
        StartOrEndLoanUIState(emptyLoan, emptyItem, emptyUser, emptyUser))
    startOrEndLoanViewModel.onFinish()
    coVerify { db.setLoan(any()) }
  }
}
