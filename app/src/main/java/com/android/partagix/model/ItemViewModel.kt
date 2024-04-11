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
import androidx.lifecycle.ViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.google.firebase.auth.FirebaseAuth
import com.google.type.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration

class ItemViewModel(
    item: Item = Item("", Category("", ""), "", "", "", Visibility.PUBLIC, 1, Location("")),
    id: String? = null
) : ViewModel() {

  private val database = Database()

  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(ItemUIState(item))
  val uiState: StateFlow<ItemUIState> = _uiState

  init {
    if (id != null) {
      database.getItem(id) { newItem -> updateUiState(newItem) }
    } else {
      updateUiState(item)
    }
    // TODO: set the author field as the User's name
  }

  /**
   * Get the category id (full category object) from the item's category name
   *
   * @param item an item with missing Category.id
   * @return the item with complete Category attribute, and an Error if categoryName is not found
   */
  private fun fillIdCategory(item: Item): Item {
    var idCategory = ""
    database.getIdCategory(item.category.name, { idCategory = it })
    return Item(
        _uiState.value.item.id,
        Category(idCategory, _uiState.value.item.category.name),
        _uiState.value.item.name,
        _uiState.value.item.description,
        _uiState.value.item.author,
        _uiState.value.item.visibility,
        _uiState.value.item.quantity,
        _uiState.value.item.location)
  }

  fun updateUiState(new: Item) {
    _uiState.value =
        _uiState.value.copy(
            item = new,
        )
  }

  fun saveWithUiState() {
    if (_uiState.value.item.id == "") {
      database.createItem(
          FirebaseAuth.getInstance().currentUser!!.uid, fillIdCategory(_uiState.value.item))
    } else {
      database.setItem(_uiState.value.item)
    }
  }
    fun findUser(uid :String): User {
        var user :User = User("", "", "", "", inventory = Inventory("", emptyList()))
        database.getUser(uid) {user = it }
        return user

    }

    fun findStatus (item : Item): LoanState {
        var state : LoanState = LoanState.PENDING
        database.getLoans { it.filter { it.idItem.equals(item.id)}
            .forEach { if(it.state == LoanState.ACCEPTED){
                state = LoanState.ACCEPTED
            } else {
                if(it.state == LoanState.PENDING){
                    state = LoanState.PENDING
                } else {
                    state = LoanState.FINISHED
                }
            }
            }
        }
        return state
    }
    fun findLoan(item : Item): Loan {
        var loan :List<Loan> = emptyList()
        var currentDate = java.util.Date()
        var nextLoan : Loan= Loan ("","","",currentDate,currentDate,"","","","",LoanState.CANCELLED)
        database.getLoans { it.filter { it.idItem.equals(item.id) && it.state.equals(LoanState.ACCEPTED)}
            .forEach { if (it.startDate.before(currentDate) && it.endDate.after(currentDate)){
                nextLoan = it
            } else {
                if (it.startDate.after(currentDate)){
                    if (nextLoan.startDate.after(it.startDate)){
                        nextLoan = it
                    }
                }
            }
            }
        }
        return nextLoan
    }
}

data class ItemUIState(val item: Item)
