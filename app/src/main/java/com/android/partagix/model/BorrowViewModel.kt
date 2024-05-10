package com.android.partagix.model

import android.location.Location
import androidx.lifecycle.ViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.visibility.Visibility
import java.sql.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

val emptyLoan = Loan("", "", "", "", Date(0), Date(0), "", "", "", "", LoanState.PENDING)
val emptyItem = Item("", Category("", ""), "", "", Visibility.PUBLIC, 0, Location(""))

class BorrowViewModel(db: Database = Database()) : ViewModel() {

  private val database = db

  private val _loanUiState = MutableStateFlow(emptyLoan)
  val loanUiState: StateFlow<Loan> = _loanUiState

  private var _itemUiState = MutableStateFlow(emptyItem)
  val itemUiState: StateFlow<Item> = _itemUiState

  /**
   * Set the item to borrow and start a new borrow request
   *
   * @param item the item to borrow
   */
  fun resetBorrow(item: Item) {
    // Set the item to borrow
    _itemUiState.value = item

    // Set the loaner id to the current logged user
    _loanUiState.value = emptyLoan
    database.getCurrentUser { user ->
      _loanUiState.value = _loanUiState.value.copy(idLoaner = user.id)
    }

    // Set the loan request to pending
    _loanUiState.value = _loanUiState.value.copy(state = LoanState.PENDING)

    // Set the item id in the loan
    _loanUiState.value = _loanUiState.value.copy(idItem = item.id)

    // Set the owner id in the loan to the owner of the item
    _loanUiState.value = _loanUiState.value.copy(idOwner = _itemUiState.value.id)
  }

  /**
   * Update the loan state
   *
   * @param new the new loan to update the UI state with
   */
  fun updateLoan(new: Loan) {
    _loanUiState.value =
        _loanUiState.value.copy(
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
    database.createLoan(_loanUiState.value) { newLoan -> updateLoan(newLoan) }
  }
}
