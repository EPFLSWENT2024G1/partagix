package com.android.partagix.model

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.MediaStore
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import com.android.partagix.MainActivity
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.user.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(
    private val db: Database = Database(),
    @SuppressLint("StaticFieldLeak") private val context: MainActivity
) : ViewModel() {

  private val _uiState =
      MutableStateFlow(HomeUIState(User("", "", "", "", Inventory("", emptyList()))))
  val uiState: StateFlow<HomeUIState> = _uiState

  init {
    updateUser(Firebase.auth.currentUser?.uid)
  }

  fun updateUser(id: String?) {
    if (id != null) {
      db.getUser(id) { _uiState.value = _uiState.value.copy(user = it) }
    }
  }

  fun openCamera() {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    startActivityForResult(context, intent, 0, null)
  }
}

data class HomeUIState(val user: User)
