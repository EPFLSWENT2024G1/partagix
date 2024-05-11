package com.android.partagix.model

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
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

  /** Open a qr code scanner app or the Play Store to download it */
  fun openQrScanner() {
    // Open source qr code scanner app
    val packageName = "com.google.zxing.client.android"

    if (isAppInstalled(packageName)) {
      val intent = context.packageManager.getLaunchIntentForPackage(packageName)
      intent?.let { startActivity(context, it, null) }
    } else {
      // If the app is not installed -> open the Play Store to it
      try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        startActivity(context, intent, null)
      } catch (e: ActivityNotFoundException) {
        // If the Play Store is not available -> open the website to it
        val intent =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
        startActivity(context, intent, null)
      }
    }
  }

  /**
   * Check if an app is installed on the device
   *
   * @param packageName the package name of the app
   * @return true if the app is installed, false otherwise
   */
  private fun isAppInstalled(packageName: String): Boolean {
    return try {
      context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
      true
    } catch (e: PackageManager.NameNotFoundException) {
      false
    }
  }
}

data class HomeUIState(val user: User)
