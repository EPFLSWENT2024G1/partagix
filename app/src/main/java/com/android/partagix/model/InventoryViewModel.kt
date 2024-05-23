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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.partagix.model.filtering.Filtering
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.util.Date
import java.util.concurrent.CountDownLatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * InventoryViewModel is a ViewModel that will handle the inventory of the user
 *
 * @param items the list of items to display
 * @param db the database to get the items
 * @param firebaseAuth the firebaseauth instance to get the current user
 *     @param latch a countdownlatch to wait for the database to finish
 */
class InventoryViewModel(
    items: List<Item> = emptyList(),
    db: Database = Database(),
    firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    latch: CountDownLatch = CountDownLatch(1)
) : ViewModel() {

  private val database = db
  private var fetchedList: List<Item> = emptyList()
  private var fetchedBorrowed: List<Item> = emptyList()

  private val filtering = Filtering()

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
    // getInventory(firebaseAuth = firebaseAuth, latch = latch)
    // This is already done during the navigation
  }

  /**
   * getInventory is a function that will update the uistate to have the items from your inventory
   * and to have the possible items you borrowed by checking your loans
   *
   * @param latch a countdownlatch to wait for the database to finish
   * @param firebaseAuth the firebaseauth instance to get the current user
   */
  fun getInventory(
      latch: CountDownLatch = CountDownLatch(1),
      firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
  ) {
    viewModelScope.launch {
      val newLoanList = mutableListOf<Loan>()
      val newLoanBorList = mutableListOf<Loan>()
      val currentUser = firebaseAuth.currentUser
      if (currentUser != null) {
        database.getItemsWithImages { items: List<Item> ->
          database.getLoans { loansList ->
            findTime(items.filter { it.idUser.equals(currentUser.uid) }, loansList) {
              newLoanList.add(it)
            }
            val lenderUsersIds = mutableListOf<String>()
            val itemsBor = mutableListOf<Item>()
            loansList
                .filter {
                  it.idBorrower.equals(currentUser.uid) &&
                      (it.state == LoanState.ACCEPTED || it.state == LoanState.ONGOING)
                }
                .forEach { loan ->
                  lenderUsersIds.add(loan.idLender)
                  val itemsBorHere = items.filter { item -> item.id == loan.idItem }
                  itemsBor.add(itemsBorHere[0])
                  findTime(items.filter { it.id.equals(loan.idItem) }, loansList) {
                    newLoanBorList.add(it)
                  }
                }

            val lenderUsers = mutableListOf<User>()
            database.getUsers { users ->
              lenderUsersIds.forEach { lenderId ->
                lenderUsers.add(users.filter { it.id.equals(lenderId) }[0])
              }
              updateBorrows(itemsBor, newLoanBorList, lenderUsers)
              updateUser(
                  users.filter { it.id.equals(currentUser.uid) }[0],
                  items.filter { it.idUser.equals(currentUser.uid) }.size)
              newLoanList.forEach { loan -> updateLoan(loan) }
              updateInv(items.filter { it.idUser.equals(currentUser.uid) })
            }
          }
        }
      } else {
        val emptyItems = emptyList<Item>()
        updateBor(emptyItems)
        getUsers(emptyItems) {
          updateUsersBor(emptyList())
          updateUser(it, 0)
        }
        findTime(emptyItems, emptyList(), ::updateLoanBor)
        updateInv(emptyItems)
        findTime(emptyItems, emptyList(), ::updateLoan)
      }
      latch.countDown()
    }
  }

  fun updateBorrows(
      borrowedItems: List<Item>,
      loanBor: List<Loan>,
      usersBor: List<User>,
  ) {
    fetchedBorrowed = borrowedItems
    _uiState.value =
        _uiState.value.copy(borrowedItems = borrowedItems, loanBor = loanBor, usersBor = usersBor)
  }
  /**
   * updateInv is a function that will update the uiState's inventory with the new items
   *
   * @param new the new items to update the inventory
   */
  fun updateInv(new: List<Item>) {
    _uiState.value = _uiState.value.copy(items = new)
    fetchedList = new
  }

  /**
   * updateBor is a function that will update the uiState's borrowed items with the new items
   *
   * @param new the new items to update the borrowed items
   */
  fun updateBor(new: List<Item>) {
    _uiState.value = _uiState.value.copy(borrowedItems = new)
    fetchedBorrowed = new
  }

  /**
   * updateUser is a function that will update the uiState's user list
   *
   * @param new the new user to update the user list
   */
  fun updateUser(new: User, count: Int) {
    val list = mutableListOf<User>()
    for (i in 0 until count) {
      list.add(new)
    }
    _uiState.value = _uiState.value.copy(users = list)
  }

  /**
   * updateUsersBor is a function that will update the uiState's user borrowed list
   *
   * @param new the new user to update the user borrowed list
   */
  fun updateUsersBor(new: List<User>) {
    _uiState.value = _uiState.value.copy(usersBor = new)
  }

  /**
   * updateLoanBor is a function that will update the uiState's loan borrowed list
   *
   * @param new the new loan to update the loan borrowed list
   */
  fun updateLoanBor(new: Loan) {
    _uiState.value = _uiState.value.copy(loanBor = uiState.value.loanBor.plus(new))
  }

  /**
   * updateLoan is a function that will update the uiState's loan list
   *
   * @param new the new loan to update the loan list
   */
  fun updateLoan(new: Loan) {
    _uiState.value = _uiState.value.copy(loan = uiState.value.loan.plus(new))
  }

  /**
   * updateItem is a function that will update the item's uiState with the new item
   *
   * @param new the new item to update the item list
   */
  fun updateItem(new: Item) {
    val items: List<Item> = _uiState.value.items.map { if (it.id == new.id) new else it }
    val borrowedItems: List<Item> =
        _uiState.value.borrowedItems.map { if (it.id == new.id) new else it }
    _uiState.value = _uiState.value.copy(items = items, borrowedItems = borrowedItems)
  }

  /**
   * createItem is a function that will update the item list with the new item
   *
   * @param new the new item to update the item list
   */
  fun createItem(new: Item) {
    _uiState.value =
        _uiState.value.copy(
            items = _uiState.value.items.plus(new),
        )
  }

  /**
   * getusers is a function that will update the user list with the users that are in the list
   *
   * @param list the list of items to find the users
   * @param update a function to update the user list
   */
  fun getUsers(list: List<Item>, update: (User) -> Unit) {
    if (list.isEmpty()) {
      update(User("", "", "", "", Inventory("", emptyList()), File(""), ""))
      return
    }

    database.getUsers { users ->
      val toUpdate = mutableListOf<Boolean>()
      for (i in users.indices) {
        toUpdate.add(false)
        list.forEach { item ->
          if (users[i].id.equals(item.idUser)) {
            toUpdate[i] = true
          }
        }
      }
      for (i in users.indices) {
        if (toUpdate[i]) {
          update(users[i])
        }
      }
    }
  }

  /**
   * findtime is a function that will update the loan list with the loans that are in the list
   *
   * @param items the list of items to find the loans
   * @param update a function to update the loan list
   */
  fun findTime(items: List<Item>, loansList: List<Loan>, update: (Loan) -> Unit) {
    items.forEach { item ->
      val list =
          loansList
              .filter { it.idItem == item.id && it.state == LoanState.ACCEPTED }
              .sortedBy { it.startDate }
      update(
          if (list.isEmpty()) {
            Loan("", "", "", "", Date(), Date(), "", "", "", "", LoanState.CANCELLED)
          } else {
            loansList
                .filter { it.idItem == item.id && it.state == LoanState.ACCEPTED }
                .sortedBy { it.startDate }
                .first()
          })
    }
  }

  /**
   * Filter items based on the query
   *
   * @param query the query to filter the items
   */
  fun filterItems(query: String) {
    val currentState = _uiState.value
    val list = filtering.filterItems(fetchedList, query)
    val listBorrowed = filtering.filterItems(fetchedBorrowed, query)

    _uiState.value = currentState.copy(query = query, items = list, borrowedItems = listBorrowed)
  }

  /**
   * Filter items based on the query
   *
   * @param list the list of items to filter
   * @param query the query to filter the items
   * @return the list of items that match the query
   */
  fun filter(list: List<Item>, query: String): List<Item> {
    return list.filter {
      it.name.contains(query, ignoreCase = true) ||
          it.description.contains(query, ignoreCase = true) ||
          it.category.name.contains(query, ignoreCase = true) ||
          it.visibility.toString().contains(query, ignoreCase = true) ||
          it.quantity.toString().contains(query, ignoreCase = true)
    }
  }

  /**
   * Filter items based on the quantity available
   *
   * @param atLeastQuantity the quantity to filter the items
   */
  fun filterItems(atLeastQuantity: Int) {
    val currentState = _uiState.value
    val list = filtering.filterItems(fetchedList, atLeastQuantity)
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
