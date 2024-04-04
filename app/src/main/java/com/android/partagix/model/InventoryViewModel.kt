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

import Item
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(items: List<Item> = emptyList()) : ViewModel() {

  private val database = Database()
  private var fetchedList: List<Item> = emptyList()

  // UI state exposed to the UI
  private val _uiState =
      MutableStateFlow(
          InventoryUIState(
              items, ""
          ))
  val uiState: StateFlow<InventoryUIState> = _uiState

  init {
    getItems()
    getInventory()
  }

  private fun getItems() {
    viewModelScope.launch { database.getItems { update(it) } }
  }

   fun getInventory() {
         val user = FirebaseAuth.getInstance().currentUser?.uid
    viewModelScope.launch {
        if (user == null) {
            println("yooooooooooooo")
            database.getUserInventory("gWaakUl8tejpBcqPqn1n") { update(it.items) }
        } else {
            println("----- error user unknown dslabfuiladsbvuil")
        }
    }
   }

  private fun update(new: List<Item>) {

    _uiState.value =
        _uiState.value.copy(
            items = new,
        )
    fetchedList = new
  }

  fun filterItems (query: String){
      val currentState = _uiState.value
      val list =
          fetchedList.filter {
              it.id.contains(query, ignoreCase = true) ||
                      it.name.contains(query, ignoreCase = true) ||
                      it.description.contains(query, ignoreCase = true) ||
                      it.category.toString().contains(query, ignoreCase = true)
                      //formatDate(it.dueDate).contains(query, ignoreCase = true) ||
                      //it.loaned?.contains(query, ignoreCase = true) ||
                      //it.quantity?.contains(query, ignoreCase = true)
          }

      _uiState.value = currentState.copy(query = query, items = list)
  }
}

data class InventoryUIState(val items: List<Item>, val query: String)
