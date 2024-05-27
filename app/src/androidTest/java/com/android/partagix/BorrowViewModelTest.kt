package com.android.partagix

import com.android.partagix.model.BorrowViewModel
import com.android.partagix.model.Database
import com.android.partagix.model.StorageV2
import com.android.partagix.model.emptyConst.emptyItem
import com.android.partagix.model.emptyConst.emptyLoan
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.notification.FirebaseMessagingService
import com.android.partagix.model.user.User
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

class BorrowViewModelTest {
  private lateinit var borrowViewModel: BorrowViewModel

  private val mockImageStorage = mockk<StorageV2>()
  private val mockDatabase = spyk(Database(imageStorage = mockImageStorage))
  private val mockNotificationManager = mockk<FirebaseMessagingService>()

  @Before
  fun setUp() {
    every { mockDatabase.createLoan(any(), any()) } just Runs

    every { mockNotificationManager.sendNotification(any(), any()) } just Runs

    borrowViewModel = BorrowViewModel(mockDatabase, mockNotificationManager)
  }

  @After
  fun tearDown() {
    clearAllMocks()
  }

  @Test
  fun updateLoanWorks() {
    val newLoan =
        Loan(
            "id",
            "idLender",
            "idBorrower",
            "item",
            java.sql.Date(0),
            java.sql.Date(0),
            "rating",
            "rating",
            "comment",
            "comment",
            LoanState.FINISHED)

    borrowViewModel.updateLoan(newLoan)

    assertEquals(newLoan, borrowViewModel.loanUiState.value)
    assertEquals(emptyItem, borrowViewModel.itemUiState.value)
    assertEquals(emptyUser, borrowViewModel.userUiState.value)

    Thread.sleep(5000)
  }

  @Test
  fun startBorrowWorks() {
    val owner = emptyUser.copy(id = "ownerId")
    val item = emptyItem.copy(id = "itemId", idUser = "ownerId")
    val borrower = emptyUser.copy(id = "borrowerId")
    val newLoan = emptyLoan.copy(idItem = item.id, idLender = owner.id, idBorrower = borrower.id)

    every { mockDatabase.getCurrentUser(any()) } answers
        {
          val callback = firstArg<(User) -> Unit>()
          callback(borrower)
        }

    borrowViewModel.startBorrow(item, owner)

    assertEquals(item, borrowViewModel.itemUiState.value)
    assertEquals(owner, borrowViewModel.userUiState.value)
    assertEquals(newLoan.id, borrowViewModel.loanUiState.value.id)
    assertEquals(newLoan.idItem, borrowViewModel.loanUiState.value.idItem)
    assertEquals(newLoan.idLender, borrowViewModel.loanUiState.value.idLender)
    assertEquals(newLoan.idBorrower, borrowViewModel.loanUiState.value.idBorrower)
    Thread.sleep(5000)
  }

  @Test
  fun createLoanWorks() {
    val token = "token"
    val user = emptyUser.copy(fcmToken = token)

    every { mockDatabase.getCurrentUser(any()) } answers
        {
          val callback = firstArg<(User) -> Unit>()
          callback(user)
        }

    val item = emptyItem.copy(id = "itemId")

    // Set the user in the UI state
    borrowViewModel.startBorrow(item, user)

    borrowViewModel.createLoan()

    verify {
      mockDatabase.createLoan(any(), any())
      mockNotificationManager.sendNotification(any(), any())
    }
    Thread.sleep(5000)
  }
}
