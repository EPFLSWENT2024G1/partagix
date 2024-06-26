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
import com.android.partagix.utils.displayedDateFormat
import java.sql.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BorrowViewModel(
    db: Database = Database(),
    private val notificationManager: FirebaseMessagingService
) : ViewModel() {

  private val database = db

  private val _loanUiState = MutableStateFlow(emptyLoan)
  val loanUiState: StateFlow<Loan> = _loanUiState

  private var _itemUiState = MutableStateFlow(emptyItem)
  val itemUiState: StateFlow<Item> = _itemUiState

  private var _userUiState = MutableStateFlow(emptyUser)
  val userUiState: StateFlow<User> = _userUiState

  private var _itemAvailability = MutableStateFlow(false)
  val itemAvailability: StateFlow<Boolean> = _itemAvailability
  /**
   * Set the item to borrow and start a new borrow request
   *
   * @param item the item to borrow
   */
  fun startBorrow(item: Item, owner: User) {
    Log.d(TAG, "startBorrow: $owner with token ${owner.fcmToken}")

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
    _loanUiState.value = _loanUiState.value.copy(idLender = _itemUiState.value.idUser)

    // Set the start and end dates to now
    _loanUiState.value = _loanUiState.value.copy(startDate = Date(System.currentTimeMillis()))
    _loanUiState.value = _loanUiState.value.copy(endDate = Date(System.currentTimeMillis()))

    // Set the owner User to have the username
    _userUiState.value = owner

    _itemAvailability.value = false
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

  fun updateItemAvailability(availability: Boolean) {
    _itemAvailability.value = availability
  }

  /** Save the loan in the database aka 'create the loan' */
  fun createLoan(onSuccess: () -> Unit) {
    Log.d(TAG, "createLoan: ")
    var available = true
    val loan = _loanUiState.value
    database.getItemUnavailability(_itemUiState.value.id) {
      database.generateDatesBetween(loan.startDate, loan.endDate).forEach { date ->
        if (it.contains(date)) {
          Log.d(TAG, "createLoan: Date $date is unavailable")
          available = false
          updateItemAvailability(true)
        }
      }
      if (loan.startDate.after(loan.endDate)) {
        Log.d(TAG, "createLoan: Start date is after end date")
        available = false
        updateItemAvailability(true)
      }
      if (available) {
        database.createLoan(loan) { newLoan -> updateLoan(newLoan) }

        // Send notification if the user has enabled notifications
        val ownerToken = _userUiState.value.fcmToken
        Log.d(TAG, "onStart: $ownerToken")

        if (ownerToken != null) {
          val item = _itemUiState.value
          val notification =
              Notification(
                  title = "New incoming request",
                  message =
                      "You have a new incoming request for your item: ${item.name}," +
                          " from ${displayedDateFormat( loan.startDate)}" +
                          " to ${displayedDateFormat( loan.endDate)}",
                  type = Notification.Type.NEW_INCOMING_REQUEST,
                  creationDate = Date(System.currentTimeMillis()),
                  navigationUrl = Route.MANAGE_LOAN_REQUEST,
              )

          notificationManager.sendNotification(notification, ownerToken)
        }
        onSuccess()
      }
    }
  }

  companion object {
    private const val TAG = "BorrowViewModel"
  }
}
