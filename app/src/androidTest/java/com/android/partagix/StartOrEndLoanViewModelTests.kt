package com.android.partagix

import com.android.partagix.model.Database
import com.android.partagix.model.StartOrEndLoanUIState
import com.android.partagix.model.StartOrEndLoanViewModel
import com.android.partagix.model.emptyConst.emptyItem
import com.android.partagix.model.emptyConst.emptyLoan
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class StartOrEndLoanViewModelTests {

  private lateinit var db: Database

  private lateinit var startOrEndLoanViewModel: StartOrEndLoanViewModel

  @Before
  fun setup() {
    db = mockk<Database>()
    startOrEndLoanViewModel = StartOrEndLoanViewModel(db = db)
  }

  @Test
  fun testUpdateUiState() {

    startOrEndLoanViewModel.update(
        StartOrEndLoanUIState(emptyLoan, emptyItem, emptyUser, emptyUser))
    assert(startOrEndLoanViewModel.uiState.value.loan == emptyLoan)
    assert(startOrEndLoanViewModel.uiState.value.item == emptyItem)
    assert(startOrEndLoanViewModel.uiState.value.borrower == emptyUser)
    assert(startOrEndLoanViewModel.uiState.value.lender == emptyUser)
  }

  @Test
  fun testOnStart() {

    every { db.setLoan(any()) } answers
        {
          val loan = firstArg<Loan>()
          assert(loan.state == LoanState.ONGOING)
        }
    startOrEndLoanViewModel.update(
        StartOrEndLoanUIState(emptyLoan, emptyItem, emptyUser, emptyUser))
    startOrEndLoanViewModel.onStart()
    coVerify { db.setLoan(any()) }
  }

  @Test
  fun testOnFinish() {

    every { db.setLoan(any()) } answers
        {
          val loan = firstArg<Loan>()
          assert(loan.state == LoanState.FINISHED)
        }
    startOrEndLoanViewModel.update(
        StartOrEndLoanUIState(emptyLoan, emptyItem, emptyUser, emptyUser))
    startOrEndLoanViewModel.onFinish()
    coVerify { db.setLoan(any()) }
  }
}
