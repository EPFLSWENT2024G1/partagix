package com.android.partagix.model

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.filtering.Filtering
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.visibility.Visibility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoanViewModel(
    private val availableItems: List<Item> = emptyList(),
    private val filteredItems: List<Item> = emptyList(),
    private val db: Database = Database(),
    private val filtering: Filtering = Filtering(),
) : ViewModel() {
  private val _uiState = MutableStateFlow(LoanUIState(filteredItems))
  val uiState: StateFlow<LoanUIState> = _uiState

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
  fun getAvailableLoans(onSuccess: (List<Item>) -> Unit = {}) {
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

            update(newItems)
            onSuccess(newItems)
          }
        }
      }
    }
  }

  private fun availableItems(items: List<Item>) {
    availableItems = availableItems
//    _uiState.value = _uiState.value.copy(filteredItems = items)
  }

  /** Update the UI state with the given list of items and an optional query. */
  private fun update(items: List<Item>, query: String? = null) {
    if (query == null) {
      _uiState.value = _uiState.value.copy(filteredItems = items)
    } else {
      _uiState.value = _uiState.value.copy(filteredItems = items, query = query)
    }
  }

  /**
   * Filter the available items for a loan based on the given query and update the UI state with the
   * filtered items.
   *
   * The query is used to filter the items as described in [Filtering.filterItems].
   */
  fun filterItems(query: String) {
    val list = filtering.filterItems(availableItems, query)
    update(list, query)
  }

  /**
   * Filter the available items for a loan based on the given quantity and update the UI state with
   * the filtered items.
   *
   * The items are filtered based on the quantity as described in [Filtering.filterItems].
   */
  fun filterItems(atLeastQuantity: Int) {
    val list = filtering.filterItems(availableItems, atLeastQuantity)
    update(list)
  }

  /**
   * Filter the available items for a loan based on the given current position and radius and update
   * the UI state with the filtered items.
   *
   * The items are filtered based on the current position and radius as described in
   * [Filtering.filterItems].
   */
  fun filterItems(currentPosition: Location, radius: Double) {
    val list = filtering.filterItems(availableItems, currentPosition, radius)
    update(list)
  }

  /**
   * Release the filters applied to the available items and update the UI state with the UI state
   */
  fun releaseFilters() {
    getAvailableLoans()
  }

  companion object {
    private const val TAG = "InventoryViewModel"
  }
}

data class LoanUIState(
  val filteredItems: List<Item>,
  val query: String = "",
)
