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
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ItemViewModel(
    item: Item = Item("", Category("", ""), "", "", Visibility.PUBLIC, 1, Location("")),
    id: String? = null,
    db: Database = Database(),
    private val onItemSaved: (Item) -> Unit = {},
    private val onItemCreated: (Item) -> Unit = {},
    user: User = User("", "", "", "", Inventory("", emptyList()))
) : ViewModel() {

  private val database = db

  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(ItemUIState(item, user))
  val uiState: StateFlow<ItemUIState> = _uiState

  init {
    if (id != null) {
      database.getItem(id) { newItem -> updateUiItem(newItem) }
    } else {
      updateUiItem(item)
    }
    // TODO: set the author field as the User's name
  }

  /**
   * Update the UI state with a new item
   *
   * @param new the new item to update the UI state with
   */
  fun updateUiItem(new: Item) {
    _uiState.value =
        _uiState.value.copy(
            item = new,
        )
  }

  fun updateUiUser(new: User) {
    _uiState.value =
        _uiState.value.copy(
            user = new,
        )
  }

  /** Save the item with the current UI state in the database */
  fun save(new: Item) {
    if (new.id == "") {
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
      onItemCreated(new)
    } else {
      updateUiItem(new)
      onItemSaved(new)
      database.setItem(new)
    }
  }

  fun getUser() {
    println("----- getUser")
    database.getUser(uiState.value.item.idUser) { user -> updateUiUser(user) }
  }

  companion object {
    private const val TAG = "ItemViewModel"
  }
}

data class ItemUIState(val item: Item, val user: User)
