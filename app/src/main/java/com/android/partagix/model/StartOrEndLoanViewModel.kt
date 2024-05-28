package com.android.partagix.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.partagix.model.emptyConst.emptyItem
import com.android.partagix.model.emptyConst.emptyLoan
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.notification.FirebaseMessagingService
import com.android.partagix.model.notification.Notification
import com.android.partagix.model.user.User
import com.android.partagix.ui.navigation.Route
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StartOrEndLoanViewModel(
    private val db: Database,
    private val notificationManager: FirebaseMessagingService
) : ViewModel() {

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
        )
    db.setLoan(newLoan)

    sendNotification(
        "started", "${Route.VIEW_ITEM}/${_uiState.value.item.id}", _uiState.value.lender)
  }

  fun onCancel() {
    val loan = _uiState.value.loan
    val newLoan =
        loan.copy(
            state = LoanState.CANCELLED,
            startDate = Date(),
        )
    db.setLoan(newLoan)
    sendNotification("cancelled", Route.INVENTORY, _uiState.value.lender)
  }

  fun onFinish() {
    val loan = _uiState.value.loan
    val newLoan =
        loan.copy(
            state = LoanState.FINISHED,
            endDate = Date(),
        )
    db.setLoan(newLoan)

    sendNotification("finished", Route.FINISHED_LOANS, _uiState.value.borrower)
  }

  private fun sendNotification(state: String, route: String, to: User) {
    val borrowerToken = to.fcmToken
    Log.d(TAG, "sendNotification: state=$state: $borrowerToken")

    if (borrowerToken != null) {
      val notification =
          Notification(
              title = "Loan $state",
              message = "Loan $state for ${_uiState.value.item.name}",
              type = Notification.Type.NEW_INCOMING_REQUEST,
              creationDate = Date(),
              navigationUrl = route,
          )

      notificationManager.sendNotification(notification, borrowerToken)
    }
  }

  companion object {
    private const val TAG = "StartOrEndLoanViewModel"
  }
}

data class StartOrEndLoanUIState(
    val loan: Loan,
    val item: Item,
    val borrower: User,
    val lender: User
)
