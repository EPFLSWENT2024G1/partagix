package com.android.partagix.model

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.filtering.Filtering
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.visibility.Visibility
import java.util.concurrent.CountDownLatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoanViewModel(
    private var availableItems: List<Item> = emptyList(),
    private val db: Database = Database(),
    private val filtering: Filtering = Filtering(),
) : ViewModel() {
  private val _uiState = MutableStateFlow(LoanUIState(availableItems))
  val uiState: StateFlow<LoanUIState> = _uiState

  private var filterState = FilterState()

  init {
    getAvailableLoans()
  }

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
  fun getAvailableLoans(
      onSuccess: (List<Item>) -> Unit = {},
      latch: CountDownLatch = CountDownLatch(1)
  ) {
    val user = Authentication.getUser()

    if (user == null) {
      // TODO: Handle error
      return
    } else {
      viewModelScope.launch {
        db.getLoans { loans: List<Loan> ->
          db.getItems { itemList: List<Item> ->
            val newItems =
                itemList.filter { item ->
                  // item is not owned by the current user
                  item.idUser != user.uid &&
                      // item is not already borrowed by someone
                      loans.all { it.idItem != item.id } &&
                      // item's visibility is either PUBLIC, or FRIENDS if the current user is a
                      // friend of the item's owner
                      item.visibility == Visibility.PUBLIC // TODO: check also with FRIENDS
                }

            availableItems = newItems
            update(newItems)
            onSuccess(newItems)
            latch.countDown()
          }
        }
      }
    }
  }

  /** Update the UI state with the given list of items and an optional query. */
  private fun update(items: List<Item>, filterState: FilterState? = null) {
    if (filterState == null) {
      _uiState.value = _uiState.value.copy(availableItems = items)
    } else {
      _uiState.value = _uiState.value.copy(availableItems = items, filterState = filterState)
    }
  }

  fun applyFilters(filterState: FilterState) {
    this.filterState = filterState
    var items = availableItems

    filterState.query?.let { query ->
      if (query.isNotEmpty()) {
        items = filtering.filterItems(items, query)
      }
    }
    filterState.atLeastQuantity?.let { quantity -> items = filtering.filterItems(items, quantity) }
    if (filterState.location != null && filterState.radius != null) {
      items = filtering.filterItems(items, filterState.location, filterState.radius)
    }

    update(items)
  }

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

data class LoanUIState(
    val availableItems: List<Item>,
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
