package com.android.partagix.model

import androidx.lifecycle.ViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import java.util.concurrent.CountDownLatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FinishedLoansViewModel(db: Database = Database(), latch: CountDownLatch = CountDownLatch(1)) :
    ViewModel() {
  private val database = db

  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(FinishedUIState(emptyList()))
  val uiState: StateFlow<FinishedUIState> = _uiState

  init {
    getFinishedLoan(latch)
  }

  fun getFinishedLoan(latch: CountDownLatch = CountDownLatch(1)) {
    val user = Authentication.getUser()

    if (user == null) {
      // TODO: Handle error
      latch.countDown()
      return
    } else {

      database.getLoans {
        it.filter { loan ->
              loan.state == LoanState.FINISHED &&
                  (loan.idOwner == user.uid || loan.idLoaner == user.uid)
            }
            .forEach { loan -> updateLoans(loan) }

        latch.countDown()
      }
    }
  }

  private fun updateLoans(new: Loan) {
    _uiState.value = _uiState.value.copy(loans = _uiState.value.loans.plus(new))
  }
}

data class FinishedUIState(val loans: List<Loan>)
