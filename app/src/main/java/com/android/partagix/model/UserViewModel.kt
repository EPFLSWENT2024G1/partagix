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
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.user.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(
    user: User = User("", "", "", "", Inventory("", emptyList())),
) : ViewModel() {

  // private val user = user
  private val database = Database()

  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(UserUIState(user))
  val uiState: StateFlow<UserUIState> = _uiState

  init {
    if (user.id == "") {
      setUserToCurrent()
    } else {
      database.getUser(user.id) { updateUIState(it) }
    }
  }

  private fun setUserToCurrent() {
    val userID = FirebaseAuth.getInstance().currentUser?.uid

    if (userID == null) {
      database.getUser("XogPd4oF1nYc6Rag6zhh") { updateUIState(it) }
    } else if (userID != "" &&
        false) { // TODO: remove false when logged in users are in the database
      database.getUser(userID) { updateUIState(it) }
    } else {
      database.getUser("XogPd4oF1nYc6Rag6zhh") { updateUIState(it) }
    }
  }

  private fun updateUIState(new: User) {
    _uiState.value =
        _uiState.value.copy(
            user = new,
        )
  }

  companion object {
    private const val TAG = "UserViewModel"
  }
}

data class UserUIState(val user: User)
