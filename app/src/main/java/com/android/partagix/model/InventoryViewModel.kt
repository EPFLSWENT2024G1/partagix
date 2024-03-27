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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(items: List<Item> = emptyList()) : ViewModel() {

  private val database = Database()

  // UI state exposed to the UI
  private val _uiState =
      MutableStateFlow(
          InventoryUIState(
              items,
          ))
  val uiState: StateFlow<InventoryUIState> = _uiState

  init {
    getItems()
    getInventory()
  }

  private fun getItems() {
    viewModelScope.launch { database.getItems { update(it) } }
  }

  private fun getInventory() {
    viewModelScope.launch {
      database.getUserInventory("Yp5cetHh3nLGMsjYY4q9") { println("----- ${it.items.size}") }
    }
  }

  private fun update(new: List<Item>) {

    _uiState.value =
        _uiState.value.copy(
            items = new,
        )
  }
}

data class InventoryUIState(val items: List<Item>)
