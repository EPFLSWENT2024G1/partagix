package com.android.partagix.model

import androidx.lifecycle.ViewModel
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.user.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(
  private val db: Database = Database()
) : ViewModel() {

  private val _uiState =
    MutableStateFlow(
      HomeUIState(
        User(
          "",
          "",
          "",
          "",
          Inventory("", emptyList())
        )
      )
    )
  val uiState: StateFlow<HomeUIState> = _uiState

  init {
    updateUser(Firebase.auth.currentUser?.uid)
  }
  fun updateUser(id: String?) {
    if (id != null) {
      db.getUser(id) {
        _uiState.value = _uiState.value.copy(user = it)
      }
    }
  }
}

data class HomeUIState(
  val user: User
)