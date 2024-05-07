package com.android.partagix.model

import androidx.lifecycle.ViewModel
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import java.sql.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

val emptyLoan =
    Loan(
        "id",
        "id_owner",
        "id_loaner",
        "id_item",
        Date(0),
        Date(0),
        "r",
        "r",
        "c",
        "c",
        LoanState.PENDING)

class BorrowViewModel(db: Database = Database(), loan: Loan = emptyLoan) : ViewModel() {

  private val database = db

  private val _uiState = MutableStateFlow(loan)
  val uiState: StateFlow<Loan> = _uiState

  init {
    _uiState.value = emptyLoan
  }

  /**
   * Update the loan state
   *
   * @param new the new loan to update the UI state with
   */
  fun updateLoan(new: Loan) {
    _uiState.value =
        _uiState.value.copy(
            id = new.id,
            idOwner = new.idOwner,
            idLoaner = new.idLoaner,
            idItem = new.idItem,
            startDate = new.startDate,
            endDate = new.endDate,
            reviewOwner = new.reviewOwner,
            reviewLoaner = new.reviewLoaner,
            commentOwner = new.commentOwner,
            commentLoaner = new.commentLoaner,
            state = new.state)
  }

  /** Save the loan in the database aka 'create the loan' */
  fun createLoan() {
    database.createLoan(_uiState.value) { newLoan -> updateLoan(newLoan) }
  }
}
