package com.android.partagix.model

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.filtering.Filtering
import com.android.partagix.model.item.Item
import com.android.partagix.model.user.User
import java.util.concurrent.CountDownLatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoanViewModel(
    private var availableLoans: List<LoanDetails> = emptyList(),
    private val db: Database = Database(),
    private val filtering: Filtering = Filtering(),
) : ViewModel() {
  private val _uiState = MutableStateFlow(LoanUIState(availableLoans))
  val uiState: StateFlow<LoanUIState> = _uiState

  private var filterState = FilterState()

  /**
   * Update the UI state with the available items for a loan, fetched from the database.
   *
   * An item is considered to be available for a loan if :
   * - The item is not owned by the current user
   * - The item is not already borrowed by someone
   * - The item's visibility is either PUBLIC, or FRIENDS if the current user is a friend of the
   *   item's owner
   *
   * Note: Does nothing if the current user is not authenticated.
   *
   * Returns the list of available items for a loan, and updates the UI state with it.
   */
  fun getAvailableLoans(latch: CountDownLatch = CountDownLatch(1)) {
    val user = Authentication.getUser()

    if (user == null) {
      // TODO: Handle error
      return
    } else {
      viewModelScope.launch {
        db.getAvailableItems(true) { newItems: List<Item> ->
          db.getUsers { users: List<User> ->
            val usersMap = users.associateBy { it.id }
            availableLoans =
                newItems.map { item ->
                  val u = usersMap[item.idUser]!!
                  LoanDetails(item, u)
                }
            applyFilters(uiState.value.filterState)
            update(availableLoans, filterState)
            latch.countDown()
          }
        }
      }
    }
  }

  /** Update the UI state with the given list of loans and an optional query. */
  private fun update(loans: List<LoanDetails>, filterState: FilterState? = null) {
    if (filterState == null) {
      _uiState.value = _uiState.value.copy(availableLoans = loans)
    } else {
      _uiState.value = _uiState.value.copy(availableLoans = loans, filterState = filterState)
    }
  }

  /**
   * Apply all the filters to the available items and update the UI state with the filtered items.
   */
  fun applyFilters(filterState: FilterState) {
    this.filterState = filterState
    var loans = availableLoans

    filterState.query?.let { query ->
      if (query.isNotEmpty()) {
        val filteredItems = filtering.filterItems(loans.map { it.item }, query)
        loans = loans.filter { it.item in filteredItems }
      }
    }

    filterState.atLeastQuantity?.let { quantity ->
      val filteredItems = filtering.filterItems(loans.map { it.item }, quantity)
      loans = loans.filter { it.item in filteredItems }
    }

    if (filterState.location != null && filterState.radius != null) {
      val filteredItems =
          filtering.filterItems(loans.map { it.item }, filterState.location, filterState.radius)
      loans = loans.filter { it.item in filteredItems }
    }

    _uiState.value = _uiState.value.copy(filterState = filterState)
    update(loans)
  }

  /**
   * Reset the filter with the given [filterAction] and update the UI state with the filtered items.
   */
  fun resetFilter(filterAction: FilterAction) {
    filterState =
        when (filterAction) {
          is FilterAction.ResetQuery -> filterState.copy(query = null)
          is FilterAction.ResetAtLeastQuantity -> filterState.copy(atLeastQuantity = null)
          is FilterAction.ResetLocation -> filterState.copy(location = null, radius = null)
        }
    applyFilters(filterState)
  }

  companion object {
    private const val TAG = "InventoryViewModel"
  }
}

data class LoanDetails(
    val item: Item,
    val user: User,
)

data class LoanUIState(
    val availableLoans: List<LoanDetails>,
    val filterState: FilterState = FilterState()
)

sealed class FilterAction {
  object ResetQuery : FilterAction()

  object ResetAtLeastQuantity : FilterAction()

  object ResetLocation : FilterAction()
}

data class FilterState(
    val query: String? = null,
    val atLeastQuantity: Int? = null,
    val location: Location? = null,
    val radius: Double? = null
)
