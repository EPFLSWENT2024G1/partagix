package com.android.partagix.model

import androidx.lifecycle.ViewModel
import com.android.partagix.model.emptyConst.emptyItem
import com.android.partagix.model.emptyConst.emptyLoan
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class StartOrEndLoanViewModel(private val db: Database) : ViewModel() {

  private val _uiState = MutableStateFlow(StartLoanUIState(emptyLoan, emptyItem, emptyUser, emptyUser))
  val uiState: StateFlow<StartLoanUIState> = _uiState

  fun updateLoan(new: Loan) {
    _uiState.value =
        _uiState.value.copy(
            loan = new,
        )
  }
  fun updateItem(new: Item) {
    _uiState.value =
        _uiState.value.copy(
            item = new,
        )
  }
  fun updateBorrower(new: User) {
    _uiState.value =
        _uiState.value.copy(
            borrower = new,
        )
  }
  fun updateLender(new: User) {
    _uiState.value =
        _uiState.value.copy(
            lender = new,
        )
  }

  fun onStart(){
    val loan = _uiState.value.loan
    val item = _uiState.value.item
    val borrower = _uiState.value.borrower
    val lender = _uiState.value.lender
    val newLoan = loan.copy(
      state = LoanState.ONGOING,
      startDate = Date(),
      endDate = Date(),
    )
    db.setLoan(newLoan)
    //db.setItem(item.copy(state = ItemState.BORROWED))
    //db.updateUser(borrower.copy(loanId = newLoan.id))
    //db.updateUser(lender.copy(loanId = newLoan.id))
  }

  fun onCancel(){

  }

  fun onFinish(){
    val loan = _uiState.value.loan
    val newLoan = loan.copy(
      state = LoanState.FINISHED,
      endDate = Date(),
    )
    db.setLoan(newLoan)
  }

  fun getInfos(itemId: String, userId: String, state: LoanState){
    db.getLoans { loans ->
      val loan = loans.find { it.idItem == itemId && it.state == state }
      println("---- loan $loan")
      if (loan != null) {
        db.getItem(itemId) { item ->
          db.getUser(loan.idBorrower) { borrower ->
            db.getUser(loan.idLender) { lender ->
              _uiState.value = StartLoanUIState(loan, item, borrower, lender)
            }
          }
        }
      }
    }
  }
}

data class StartLoanUIState(val loan: Loan, val item: Item, val borrower: User, val lender: User)

