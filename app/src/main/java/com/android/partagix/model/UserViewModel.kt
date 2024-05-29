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
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.user.User
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(
    user: User = User("", "", "", "", Inventory("", emptyList()), email = ""),
    db: Database = Database(),
    private val imageStorage: StorageV2 = StorageV2()
) : ViewModel() {

  // private val user = user
  private val database = db

  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(UserUIState(user))
  val uiState: StateFlow<UserUIState> = _uiState

  init {
    if (user.id == "") {
      setUserToCurrent()
    } else {
      database.getUserWithImage(user.id) { updateUIState(it) }
    }
  }

  fun setUserToCurrent() {
    val userID = Authentication.getUser()?.uid
    if (userID != null) {
      database.getUserWithImage(userID) { updateUIState(it) }
    } else {
      database.getUserWithImage("XogPd4oF1nYc6Rag6zhh") { updateUIState(it) }
    }
  }

  private fun updateUIState(new: User) {
    _uiState.value =
        _uiState.value.copy(
            user = new,
        )
    getComments()
  }

  fun setUser(user: User) {
    updateUIState(user)
  }

  fun updateLocation(location: Location) {
    _uiState.value =
        _uiState.value.copy(
            location = location,
        )
  }

  /**
   * Update the user in the database and update the UI state when done
   *
   * @param user the user to update (with the new values)
   */
  fun updateUser(user: User) {
    database.updateUser(user) { updateUIState(it) }
  }

  /**
   * Get the user id of the logged user
   *
   * @return the user id of the logged user
   */
  fun getLoggedUserId(): String? {
    return FirebaseAuth.getInstance().currentUser?.uid
  }

  /** Get the user's comments from the database and update the UI state when done */
  fun getComments() {
    database.getComments(_uiState.value.user.id) { comments ->
      _uiState.value =
          _uiState.value.copy(
              comments = comments,
          )
    }
  }

  fun updateImage(imageName: String, onSuccess: (localFile: File) -> Unit) {
    imageStorage.getImageFromFirebaseStorage(imageName) { onSuccess(it) }
  }

  fun uploadImage(imageUri: Uri, imageName: String, onSuccess: () -> Unit) {
    imageStorage.uploadImageToFirebaseStorage(imageUri, imageName = imageName) { onSuccess() }
  }

  companion object {
    private const val TAG = "UserViewModel"
  }
}

/**
 * UI state for the user
 *
 * @param user the user
 * @param location the location of the user
 * @param comments the comments of the user, in the form: (comment's author, message)
 */
data class UserUIState(
    val user: User,
    val location: Location? = null,
    val comments: List<Pair<User, String>> = emptyList()
)
