package com.android.partagix.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.partagix.model.emptyConst.emptyLoan
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.notification.FirebaseMessagingService
import com.android.partagix.model.notification.Notification
import com.android.partagix.model.user.User
import com.android.partagix.ui.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EvaluationViewModel(
    loan: Loan = emptyLoan,
    db: Database = Database(),
    private val notificationManager: FirebaseMessagingService
) : ViewModel() {
  private val database = db

  private val _uiState = MutableStateFlow(EvaluationUIState(loan))
  val uiState: StateFlow<EvaluationUIState> = _uiState

  init {
    database.getLoans { loans ->
      val l = loans.find { it.id == loan.id }
      if (l != null) {
        updateUIState(l)
      } else {
        Log.e("EvaluationViewModel", "No loan found with id ${loan.id}")
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

    val notification =
        Notification(
            title = "New User Review",
            message = "You have just been reviewed. Check it out!",
            type = Notification.Type.USER_REVIEW,
            navigationUrl = Route.ACCOUNT)

    database.getUser(userId) { notificationManager.sendNotification(notification, it.fcmToken) }
  }

  fun getUser(userId: String, onSuccess: (User) -> Unit, onError: () -> Unit) {
    database.getUser(userId, onError, onSuccess)
  }
}

data class EvaluationUIState(val loan: Loan)
