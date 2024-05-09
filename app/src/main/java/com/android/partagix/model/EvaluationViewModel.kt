package com.android.partagix.model

import androidx.lifecycle.ViewModel
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EvaluationViewModel(
    loan: Loan = Loan("", "", "", "", Date(), Date(), "", "", "", "", LoanState.FINISHED),
    db: Database = Database()
) : ViewModel() {
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
