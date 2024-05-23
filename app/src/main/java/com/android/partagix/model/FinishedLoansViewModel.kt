package com.android.partagix.model

import android.location.Location
import androidx.lifecycle.ViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.visibility.Visibility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FinishedLoansViewModel(db: Database = Database()) : ViewModel() {
  private val database = db

  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(FinishedUIState(emptyList()))
  val uiState: StateFlow<FinishedUIState> = _uiState
  private val _uiItem =
      MutableStateFlow(
          Item(
              "",
              Category("GpWpDVqb1ep8gm2rb1WL", "Others"),
              "",
              "",
              Visibility.PRIVATE,
              0,
              Location("")))
  val uiItem: StateFlow<Item> = _uiItem

  init {
    getFinishedLoan()
  }

  fun getFinishedLoan() {
    
    val user = Authentication.getUser()

    if (user == null) {
      _uiState.value = _uiState.value.copy(loans = emptyList())
      // TODO: Handle error
      return
    } else {
      database.getLoans { loans ->
        val list = mutableListOf<Pair<Loan, Item>>()
        loans
            .filter { loan ->
              loan.state == LoanState.FINISHED &&
                  (loan.idLender == user.uid || loan.idBorrower == user.uid)
            }
            .forEach { loan ->
              database.getItem(loan.idItem) { item ->
                list.add(Pair(loan, item))
                updateLoans(list)
              }
            }
      }
    }
  }

  fun updateLoan(loan: Loan) {
    val index = _uiState.value.loans.indexOfFirst { it.first.id == loan.id }
    val item = _uiState.value.loans[index].second
    if (index != -1) {
      val list = _uiState.value.loans.toMutableList()
      list[index] = Pair(loan, item)
      _uiState.value = _uiState.value.copy(loans = list)
    }
  }

  private fun updateLoans(list: List<Pair<Loan, Item>>) {
    _uiState.value = _uiState.value.copy(loans = list)
  }
}

data class FinishedUIState(val loans: List<Pair<Loan, Item>>)
