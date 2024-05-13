package com.android.partagix.model

import androidx.lifecycle.ViewModel
import com.android.partagix.model.emptyConst.emptyItem
import com.android.partagix.model.emptyConst.emptyLoan
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import java.sql.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BorrowViewModel(db: Database = Database()) : ViewModel() {

  private val database = db

  private val _loanUiState = MutableStateFlow(emptyLoan)
  val loanUiState: StateFlow<Loan> = _loanUiState

  private var _itemUiState = MutableStateFlow(emptyItem)
  val itemUiState: StateFlow<Item> = _itemUiState

  private var _userUiState = MutableStateFlow(emptyUser)
  val userUiState: StateFlow<User> = _userUiState

  /**
   * Set the item to borrow and start a new borrow request
   *
   * @param item the item to borrow
   */
  fun startBorrow(item: Item, owner: User) {
    // Set the item to borrow
    _itemUiState.value = item

    // Set the loaner id to the current logged user
    _loanUiState.value = emptyLoan
    database.getCurrentUser { user ->
      _loanUiState.value = _loanUiState.value.copy(idBorrower = user.id)
    }

    // Set the loan request to pending
    _loanUiState.value = _loanUiState.value.copy(state = LoanState.PENDING)

    // Set the item id in the loan
    _loanUiState.value = _loanUiState.value.copy(idItem = item.id)

    // Set the owner id in the loan to the owner of the item
    _loanUiState.value = _loanUiState.value.copy(idLender = _itemUiState.value.id)

    // Set the start and end dates to now
    _loanUiState.value = _loanUiState.value.copy(startDate = Date(System.currentTimeMillis()))
    _loanUiState.value = _loanUiState.value.copy(endDate = Date(System.currentTimeMillis()))

    // Set the owner User to have the username
    _userUiState.value = owner
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
            idLender = new.idLender,
            idBorrower = new.idBorrower,
            idItem = new.idItem,
            startDate = new.startDate,
            endDate = new.endDate,
            reviewLender = new.reviewLender,
            reviewBorrower = new.reviewBorrower,
            commentLender = new.commentLender,
            commentBorrower = new.commentBorrower,
            state = new.state)
  }

  /** Save the loan in the database aka 'create the loan' */
  fun createLoan() {
    database.createLoan(_loanUiState.value) { newLoan -> updateLoan(newLoan) }
  }
}
