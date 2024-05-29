package com.android.partagix.model

import androidx.lifecycle.ViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.notification.FirebaseMessagingService
import com.android.partagix.model.notification.Notification
import com.android.partagix.model.user.User
import com.android.partagix.ui.navigation.Route
import java.util.concurrent.CountDownLatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ManageLoanViewModel(
    db: Database = Database(),
    latch: CountDownLatch = CountDownLatch(1),
    private val notificationManager: FirebaseMessagingService = FirebaseMessagingService()
) : ViewModel() {

  private val database = db

  // UI state exposed to the UI
  private val _uiState =
      MutableStateFlow(ManagerUIState(emptyList(), emptyList(), emptyList(), emptyList()))
  val uiState: StateFlow<ManagerUIState> = _uiState

  init {
    // getLoanRequests(latch)
  }

  /**
   * getInventory is a function that will update the uistate to have the items from your inventory
   * and to have the possible items you borrowed by checking your loans
   */
  fun getLoanRequests(isOutgoing: Boolean = false, onSuccess: () -> Unit = {}) {
    val user = Authentication.getUser()
    if (user == null) {
      // TODO: Handle error
      return
    } else {
      database.getItemsWithImages { list ->
        database.getUsers { usersList ->
          database.getLoans { it ->
            val loans = mutableListOf<Loan>()
            val items = mutableListOf<Item>()
            val users = mutableListOf<User>()
            val expended = uiState.value.expanded.toMutableList()
            it.filter { loan ->
                  val id =
                      if (isOutgoing) {
                        loan.idBorrower
                      } else {
                        loan.idLender
                      }
                  loan.state == LoanState.PENDING && id == user.uid
                }
                .forEach { loan ->
                  items.add(list.first { f -> f.id == loan.idItem })
                  val id =
                      if (isOutgoing) {
                        loan.idLender
                      } else {
                        loan.idBorrower
                      }
                  users.add(usersList.first { u -> u.id == id })
                  expended.add(false)
                  loans.add(loan)
                }
            update(items, users, loans, expended)
            onSuccess()
          }
        }
      }
    }
  }

  fun getCount(): Int {
    return uiState.value.loans.size
  }

  fun getUser(id: String, onSuccess: (User) -> Unit) {
    database.getUser(id, {}, onSuccess)
  }

  fun update(
      items: List<Item>,
      users: List<User>,
      loan: List<Loan>,
      expanded: List<Boolean>,
  ) {
    _uiState.value =
        _uiState.value.copy(items = items, users = users, loans = loan, expanded = expanded)
  }

  fun acceptLoan(loan: Loan, index: Int) {

    val loanDates = database.generateDatesBetween(loan.startDate, loan.endDate)
    val loansToDeclineWithIndex = mutableListOf<Pair<Loan, Int>>()
    var count = 0
    for (i in 0 until uiState.value.loans.size) {
      val otherLoan = uiState.value.loans[i]
      val otherLoanDates = database.generateDatesBetween(otherLoan.startDate, otherLoan.endDate)
      val intersection = loanDates.intersect(otherLoanDates.toSet())
      if (loan.idItem == otherLoan.idItem && loan.id != otherLoan.id && intersection.isNotEmpty()) {

        loansToDeclineWithIndex.add(Pair(otherLoan, i - count))
        count += 1
      }
    }

    for (loanToDecline in loansToDeclineWithIndex) {
      declineLoan(loanToDecline.first, loanToDecline.second)
    }

    database.setLoan(loan.copy(state = LoanState.ACCEPTED))

    sendNotification("accepted", Notification.Type.LOAN_ACCEPTED, loan.idBorrower)
    UiStateWithoutIndex(index)
  }

  fun declineLoan(loan: Loan, index: Int) {
    database.setLoan(loan.copy(state = LoanState.CANCELLED))
    UiStateWithoutIndex(index)

    sendNotification("declined", Notification.Type.LOAN_REJECTED, loan.idLender)
  }

  private fun sendNotification(state: String, type: Notification.Type, to: String) {
    val requester = uiState.value.users.find { it.id == to }

    if (requester != null) {
      sendMessage(state, type, requester.id)
    } else {
      database.getFCMToken(to) { token -> sendMessage(state, type, token) }
    }
  }

  fun updateExpanded(index: Int, expanded: Boolean) {
    val list = _uiState.value.expanded.toMutableList()
    list[index] = expanded
    _uiState.value = _uiState.value.copy(expanded = list)
  }

  private fun sendMessage(state: String, type: Notification.Type, to: String?) {
    if (to != null) {
      val notification =
          Notification(
              title = "Loan request $state",
              message = "Your loan request has been $state",
              type = type,
              navigationUrl = Route.INVENTORY)

      notificationManager.sendNotification(notification, to)
    }
  }

  private fun UiStateWithoutIndex(index: Int) {
    var newLoans: MutableStateFlow<ManagerUIState> =
        MutableStateFlow(ManagerUIState(emptyList(), emptyList(), emptyList(), emptyList()))
    for (i in 0 until uiState.value.loans.size) {
      if (i != index) {
        newLoans.value =
            newLoans.value.copy(
                items = newLoans.value.items.plus(uiState.value.items[i]),
                loans = newLoans.value.loans.plus(uiState.value.loans[i]),
                users = newLoans.value.users.plus(uiState.value.users[i]),
                expanded = newLoans.value.expanded.plus(uiState.value.expanded[i]))
      }
    }
    _uiState.value =
        _uiState.value.copy(
            items = newLoans.value.items,
            loans = newLoans.value.loans,
            users = newLoans.value.users,
            expanded = newLoans.value.expanded)
  }
}

data class ManagerUIState(
    val items: List<Item>,
    val users: List<User>,
    val loans: List<Loan>,
    val expanded: List<Boolean>,
)
