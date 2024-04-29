package com.android.partagix.model

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.visibility.Visibility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoanViewModel(
    private val availableItems: List<Item> = emptyList(),
    private val db: Database = Database()
) : ViewModel() {
  private val _uiState = MutableStateFlow(LoanUIState(availableItems))
  val uiState: StateFlow<LoanUIState> = _uiState

  init {
    getAvailableLoans()
  }

  /**
   * Update the UI state with the available items for a loan.
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
  private fun getAvailableLoans(onSuccess: (List<Item>) -> Unit = {}) {
    val user = Authentication.getUser()

    if (user == null) {
      // TODO: Handle error
      return
    } else {
      viewModelScope.launch {
        db.getLoans { loans: List<Loan> ->
          db.getItems { itemList: List<Item> ->
            update(
                itemList.filter { item ->
                  // item is not owned by the current user
                  item.idUser != user.uid &&
                      // item is not already borrowed by someone
                      loans.all { it.idItem != item.id } &&
                      // item's visibility is either PUBLIC, or FRIENDS if the current user is a
                      // friend of the item's owner
                      item.visibility == Visibility.PUBLIC // TODO: check also with FRIENDS
                })
          }
        }
      }
    }
  }

  private fun update(items: List<Item>, query: String? = null) {
    if (query == null) {
      _uiState.value = _uiState.value.copy(availableItems = items)
    } else {
      _uiState.value = _uiState.value.copy(availableItems = items, query = query)
    }
  }

  /**
   * Filter items based on the query
   *
   * @param query the query to filter the items
   */
  fun filterItems(query: String) {
    val list =
        availableItems.filter {
          it.name.contains(query, ignoreCase = true) ||
              it.description.contains(query, ignoreCase = true) ||
              it.category.toString().contains(query, ignoreCase = true) ||
              it.visibility.toString().contains(query, ignoreCase = true) ||
              it.quantity.toString().contains(query, ignoreCase = true)
        }

    update(list, query)
  }

  fun filterItems(atLeastQuantity: Int) {
    val list = availableItems.filter { it.quantity >= atLeastQuantity }
    update(list)
  }

  /**
   * Filter items based on the current position and the radius
   *
   * @param currentPosition the current position of the user
   * @param radius the radius to filter the items (in meters)
   */
  fun filterItems(currentPosition: Location, radius: Double) {
    val list = availableItems.filter { it.location.distanceTo(currentPosition) <= (radius * 1000) }
    update(list)
  }

  companion object {
    private const val TAG = "InventoryViewModel"
  }
}

data class LoanUIState(
    val availableItems: List<Item>,
    val query: String = "",
)
