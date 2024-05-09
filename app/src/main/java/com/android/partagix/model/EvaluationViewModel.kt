package com.android.partagix.model

import com.android.partagix.model.loan.Loan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EvaluationViewModel(loan: Loan, db: Database = Database()) {
  private val database = db

  private val _uiState = MutableStateFlow(EvaluationUIState(loan))
  val uiState: StateFlow<EvaluationUIState> = _uiState

  init {
    database.getLoans { loans ->
      val l = loans.find { it.id == loan.id }
      if (l != null) {
        updateUIState(l)
      }
    }
  }

  fun updateUIState(new: Loan) {
    _uiState.value =
        _uiState.value.copy(
            loan = new,
        )
  }

  fun reviewLoan(loan: Loan, rating: Double, comment: String, userId: String) {
    database.setReview(loan.id, userId, rating, comment)
  }
}

data class EvaluationUIState(val loan: Loan)
