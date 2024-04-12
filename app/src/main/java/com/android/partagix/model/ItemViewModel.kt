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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ItemViewModel(
    item: Item = Item("", Category("", ""), "", "", Visibility.PUBLIC, 1, Location("")),
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
  private fun fillIdCategory(item: Item, onSuccess: (String) -> Unit) {
    var idCategory = ""
    database.getIdCategory(item.category.name, { idCategory = it })
    println("truc a ecrire: $idCategory")
    updateUiState(
        Item(
            _uiState.value.item.id,
            Category(idCategory, _uiState.value.item.category.name),
            _uiState.value.item.name,
            _uiState.value.item.description,
            _uiState.value.item.visibility,
            _uiState.value.item.quantity,
            _uiState.value.item.location))
  }

  /**
   * Update the UI state with a new item
   *
   * @param new the new item to update the UI state with
   */
  fun updateUiState(new: Item) {
    _uiState.value =
        _uiState.value.copy(
            item = new,
        )
  }

  /** Save the item with the current UI state in the database */
  fun save(new: Item) {
    if (_uiState.value.item.id == "") {
      database.getIdCategory(new.category.name) {
        database.createItem(
            FirebaseAuth.getInstance().currentUser!!.uid,
            Item(
                new.id,
                Category(it, new.category.name),
                new.name,
                new.description,
                new.visibility,
                new.quantity,
                new.location))
      }
    } else {
      updateUiState(new)
      database.setItem(new)
    }
  }

  companion object {
    private const val TAG = "ItemViewModel"
  }
}

data class ItemUIState(val item: Item)
