package com.android.partagix.model

import androidx.lifecycle.ViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.user.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(private val db: Database = Database()) : ViewModel() {

  private val _uiState =
      MutableStateFlow(HomeUIState(User("", "", "", "", Inventory("", emptyList()))))
  val uiState: StateFlow<HomeUIState> = _uiState

  init {
    updateUser()
  }

  fun updateUser(id: String? = Authentication.getUser()?.uid) {
    if (id != null) {
      db.getUser(id) { _uiState.value = _uiState.value.copy(user = it) }
    }
  }
}

data class HomeUIState(val user: User)
