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
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
          "Yp5cetHh3nLGMsjYY4q9" /* todo get the correct userId */, _uiState.value.item)
    } else {
      database.setItem(_uiState.value.item)
    }
  }

  companion object {
    private const val TAG = "ItemViewModel"
  }
}

data class ItemUIState(val item: Item)
