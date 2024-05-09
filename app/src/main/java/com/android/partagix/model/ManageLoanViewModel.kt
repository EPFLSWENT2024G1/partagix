package com.android.partagix.model

import androidx.lifecycle.ViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import java.util.concurrent.CountDownLatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ManageLoanViewModel(db: Database = Database(), latch: CountDownLatch = CountDownLatch(1)) :
    ViewModel() {

  private val database = db

  // UI state exposed to the UI
  private val _uiState =
      MutableStateFlow(ManagerUIState(emptyList(), emptyList(), emptyList(), emptyList()))
  val uiState: StateFlow<ManagerUIState> = _uiState

  init {
    getLoanRequests(latch)
  }

  /**
   * getInventory is a function that will update the uistate to have the items from your inventory
   * and to have the possible items you borrowed by checking your loans
   */
  fun getLoanRequests(
      latch: CountDownLatch = CountDownLatch(1),
      isOutgoing: Boolean = false,
      onSuccess: () -> Unit = {}
  ) {
    val user = Authentication.getUser()
    update(emptyList(), emptyList(), emptyList(), emptyList())

    if (user == null) {
      // TODO: Handle error
      latch.countDown()
      return
    } else {
      database.getItems { list ->
        database.getLoans {
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
                updateItems(list.first { it.id == loan.idItem })
                database.getUser(loan.idLender) { user -> updateUsers(user) }
                updateExpandedReset()
                updateLoans(loan)
              }
          latch.countDown()
          onSuccess()
        }
      }
    }
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

  private fun updateItems(new: Item) {
    _uiState.value = _uiState.value.copy(items = _uiState.value.items.plus(new))
  }

  /*private fun setupExpanded (size : Int) {
      val list = mutableListOf<Boolean>()
      for (i in 0 until size) {
          list.add(false)
      }
      _uiState.value = _uiState.value.copy(expanded = list)
  }*/
  private fun updateUsers(new: User) {
    _uiState.value = _uiState.value.copy(users = _uiState.value.users.plus(new))
  }

  private fun updateLoans(new: Loan) {
    _uiState.value = _uiState.value.copy(loans = _uiState.value.loans.plus(new))
  }

  private fun updateExpandedReset() {
    _uiState.value = _uiState.value.copy(expanded = _uiState.value.expanded.plus(false))
  }
  /*fun updateExpanded (index : Int) {
      val list = _uiState.value.expanded.toMutableList()
      list[index] = !list[index]
      _uiState.value = _uiState.value.copy(expanded = list)
  }*/

  fun acceptLoan(loan: Loan, index: Int) {
    UiStateWithoutIndex(index)
    database.setLoan(loan.copy(state = LoanState.ACCEPTED))
  }

  fun declineLoan(loan: Loan, index: Int) {
    database.setLoan(loan.copy(state = LoanState.CANCELLED))
    UiStateWithoutIndex(index)
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
