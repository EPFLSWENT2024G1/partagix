/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.partagix.model

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.google.firebase.auth.FirebaseAuth
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(items: List<Item> = emptyList(), db: Database = Database()) : ViewModel() {

  private val database = db
  private var fetchedList: List<Item> = emptyList()
  private var fetchedBorrowed: List<Item> = emptyList()

  // UI state exposed to the UI
  private val _uiState =
      MutableStateFlow(
          InventoryUIState(
              items,
              "",
              items,
              emptyList(),
              emptyList(),
              emptyList(),
              emptyList(),
          ))
  val uiState: StateFlow<InventoryUIState> = _uiState

  init {
    getInventory()
  }

  private fun getItems() {
    viewModelScope.launch {
      database.getItems { update(it, it, emptyList(), emptyList(), emptyList(), emptyList()) }
    }
  }

  /**
   * getInventory is a function that will update the uistate to have the items from your inventory
   * and to have the possible items you borrowed by checking your loans
   */
  fun getInventory() {
    val user = FirebaseAuth.getInstance().currentUser
    viewModelScope.launch {
      if (user == null) {
        database.getUserInventory(/*user.uid*/ " sdfasdf") {
          updateInv(it.items)
          getusers(it.items, ::updateUsers)
          findtime(it.items, ::updateLoan)
        }
        database.getLoans {
          it.filter { it.idLoaner.equals(user) }
              .forEach { loan ->
                database.getItems { items: List<Item> ->
                  updateBor(items.filter { it.id.equals(loan.idItem) })
                  getusers(items.filter { it.id.equals(loan.idItem) }, ::updateUsersBor)
                  findtime(items.filter { it.id.equals(loan.idItem) }, ::updateLoanBor)
                }
              }
        }
      } else {
        database.getItems {
          updateBor(it)
          getusers(it, ::updateUsersBor)
          findtime(it, ::updateLoanBor)
          updateInv(it)
          getusers(it, ::updateUsers)
          findtime(it, ::updateLoan)
        }
        println("----- error user unknown")
      }
    }
  }

  /**
   * update is a function that will update the uistate with the new items, borrowed items, users,
   *
   * @param newInv the new items to update the inventory
   * @param newBor the new borrowed items to update the inventory
   * @param user the new users to update the inventory
   * @param userBor the new users borrowed to update the inventory
   * @param newLoanBor the new loans borrowed to update the inventory
   * @param newloan the new loans to update the inventory
   */
  private fun update(
      newInv: List<Item>,
      newBor: List<Item>,
      user: List<User>,
      userBor: List<User>,
      newLoanBor: List<Loan>,
      newloan: List<Loan>
  ) {
    _uiState.value =
        _uiState.value.copy(
            borrowedItems = newInv,
            items = newBor,
            users = user,
            usersBor = userBor,
            loanBor = newLoanBor,
            loan = newloan)
    fetchedBorrowed = newBor
    fetchedList = newInv
  }

  private fun updateInv(new: List<Item>) {
    _uiState.value = _uiState.value.copy(items = new)
    fetchedList = new
  }

  private fun updateBor(new: List<Item>) {
    _uiState.value = _uiState.value.copy(borrowedItems = new)
    fetchedBorrowed = new
  }

  private fun updateUsers(new: User) {
    _uiState.value = _uiState.value.copy(users = uiState.value.users.plus(new))
  }

  private fun updateUsersBor(new: User) {
    _uiState.value = _uiState.value.copy(usersBor = uiState.value.usersBor.plus(new))
  }

  private fun updateLoanBor(new: Loan) {
    _uiState.value = _uiState.value.copy(loanBor = uiState.value.loanBor.plus(new))
  }

  private fun updateLoan(new: Loan) {
    _uiState.value = _uiState.value.copy(loan = uiState.value.loan.plus(new))
  }

  fun updateItem(new: Item) {
    val items: List<Item> = _uiState.value.items.map { if (it.id == new.id) new else it }
    val borrowedItems: List<Item> =
        _uiState.value.borrowedItems.map { if (it.id == new.id) new else it }
    _uiState.value = _uiState.value.copy(items = items, borrowedItems = borrowedItems)
  }
  /**
   * getusers is a function that will update the user list with the users that are in the list
   *
   * @param list the list of items to find the users
   * @param update a function to update the user list
   */
  fun getusers(list: List<Item>, update: (User) -> Unit) {
    val users = mutableListOf<User>()
    list.forEach { database.getUser(it.idUser) { user -> update(user) } }
  }

  /**
   * findtime is a function that will update the loan list with the loans that are in the list
   *
   * @param items the list of items to find the loans
   * @param update a function to update the loan list
   */
  fun findtime(items: List<Item>, update: (Loan) -> Unit) {
    database.getLoans { loan ->
      items.forEach { item ->
        val list =
            loan
                .filter { it.idItem.equals(item.id) && it.state.equals(LoanState.ACCEPTED) }
                .sortedBy { it.startDate }
        update(
            if (list.isEmpty()) {
              Loan("", "", "", Date(), Date(), "", "", "", "", LoanState.CANCELLED)
            } else {
              loan
                  .filter { it.idItem.equals(item.id) && it.state.equals(LoanState.ACCEPTED) }
                  .sortedBy { it.startDate }
                  .first()
            })
      }
    }
  }

  /**
   * Filter items based on the query
   *
   * @param query the query to filter the items
   */
  fun filterItems(query: String) {
    val currentState = _uiState.value
    val list =
        fetchedList.filter {
          it.name.contains(query, ignoreCase = true) ||
              it.description.contains(query, ignoreCase = true) ||
              it.category.toString().contains(query, ignoreCase = true) ||
              it.visibility.toString().contains(query, ignoreCase = true) ||
              it.quantity.toString().contains(query, ignoreCase = true)
        }
    val listBorrowed =
        fetchedBorrowed.filter {
          it.name.contains(query, ignoreCase = true) ||
              it.description.contains(query, ignoreCase = true) ||
              it.category.toString().contains(query, ignoreCase = true) ||
              it.visibility.toString().contains(query, ignoreCase = true) ||
              it.quantity.toString().contains(query, ignoreCase = true)
        }

    _uiState.value = currentState.copy(query = query, items = list, borrowedItems = listBorrowed)
  }

  fun filterItems(atLeastQuantity: Int) {
    val currentState = _uiState.value
    val list = fetchedList.filter { it.quantity >= atLeastQuantity }
    _uiState.value = currentState.copy(items = list)
  }

  /**
   * Filter items based on the current position and the radius
   *
   * @param currentPosition the current position of the user
   * @param radius the radius to filter the items (in meters)
   */
  fun filterItems(currentPosition: Location, radius: Double) {
    val currentState = _uiState.value
    fetchedList.forEach {
      Log.d(
          TAG,
          "item: ${it.name}, distance: ${it.location.distanceTo(currentPosition)}, current: $currentPosition, item: ${it.location}")
    }
    val list = fetchedList.filter { it.location.distanceTo(currentPosition) <= (radius * 1000) }
    _uiState.value = currentState.copy(items = list)
  }

  companion object {
    private const val TAG = "InventoryViewModel"
  }
}

data class InventoryUIState(
    val items: List<Item>,
    val query: String,
    val borrowedItems: List<Item>,
    val users: List<User>,
    val usersBor: List<User>,
    val loanBor: List<Loan>,
    val loan: List<Loan>
)
