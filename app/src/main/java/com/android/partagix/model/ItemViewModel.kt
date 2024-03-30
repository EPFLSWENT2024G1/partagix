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

class ItemViewModel(item: Item) : ViewModel() {

  private val database = Database()

  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(ItemUIState(item))
  val uiState: StateFlow<ItemUIState> = _uiState

  init {
  }

  fun getItem(itemId : String) {
    viewModelScope.launch {
      database.getItems {
        for (i in it) {
          if (i.id == itemId) {
            update(i)
          }
        }
      }
    }
  }

  fun update(new: Item) {
    _uiState.value =
        _uiState.value.copy(
            item = new,
        )
  }

  fun createItem() {

    viewModelScope.launch {

      val newItem =
          Item(
              "", // no itemId exists at this moment, it will be generated and overwritten by the database
              _uiState.value.item.category,
              _uiState.value.item.name,
              _uiState.value.item.description)

      database.createItem("Yp5cetHh3nLGMsjYY4q9" /* todo get the correct userId */, newItem)
    }
  }

  fun saveItem() {

    viewModelScope.launch { database.setItem(_uiState.value.item) }
  }
}

data class ItemUIState(val item: Item)
