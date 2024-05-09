package com.android.partagix.model

import androidx.lifecycle.ViewModel
import com.android.partagix.model.emptyConst.emptyItem
import com.android.partagix.model.emptyConst.emptyLoan
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StartOrEndLoanViewModel(private val db: Database) : ViewModel() {

  private val _uiState =
      MutableStateFlow(StartOrEndLoanUIState(emptyLoan, emptyItem, emptyUser, emptyUser))
  val uiState: StateFlow<StartOrEndLoanUIState> = _uiState

  fun update(new: StartOrEndLoanUIState) {
    _uiState.value = new
  }

  fun onStart() {
    val loan = _uiState.value.loan
    val newLoan =
        loan.copy(
            state = LoanState.ONGOING,
            startDate = Date(),
            endDate = Date(),
        )
    db.setLoan(newLoan)
  }

  fun onCancel() {}

  fun onFinish() {
    val loan = _uiState.value.loan
    val newLoan =
        loan.copy(
            state = LoanState.FINISHED,
            endDate = Date(),
        )
    db.setLoan(newLoan)
  }
}

data class StartOrEndLoanUIState(
    val loan: Loan,
    val item: Item,
    val borrower: User,
    val lender: User
)
