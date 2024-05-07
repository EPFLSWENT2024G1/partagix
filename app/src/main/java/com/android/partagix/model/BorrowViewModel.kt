package com.android.partagix.model

import androidx.lifecycle.ViewModel
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import java.sql.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

val emptyLoan = Loan("", "", "", "", Date(0), Date(0), "", "", "", "", LoanState.PENDING)

class BorrowViewModel(db: Database = Database(), item: Item) : ViewModel() {

  private val database = db

  private val _uiState = MutableStateFlow(emptyLoan)
  val uiState: StateFlow<Loan> = _uiState

  private val _item = item

  init {
    // Set the loaner id to the current logged user
    _uiState.value = emptyLoan
    database.getCurrentUser { user -> _uiState.value = _uiState.value.copy(idLoaner = user.id) }

    // Set the loan request to pending
    _uiState.value = _uiState.value.copy(state = LoanState.PENDING)

    // Set the item id in the loan
    _uiState.value = _uiState.value.copy(idItem = item.id)

    // Set the owner id in the loan to the owner of the item
    _uiState.value = _uiState.value.copy(idOwner = _item.idUser)
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
