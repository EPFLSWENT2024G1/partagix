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
    private val _uiState =
        MutableStateFlow(
            ItemUIState(
                item
            )
        )
    val uiState: StateFlow<ItemUIState> = _uiState

    init {
        getItem()
    }

    private fun getItem() {
        viewModelScope.launch { database.getItems { update(it[0]) } }
        /* weird notation "it[0]" since the getter returns a list of Items
        and we want the unique Item that's contained in it
        -> upgrade idea : create a getter also for Item */
    }

    private fun update(new: Item) {

        _uiState.value =
            _uiState.value.copy(
                item = new
            )
    }
}

data class ItemUIState(val item: Item)