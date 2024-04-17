package com.android.partagix.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.partagix.model.Database
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.auth.SignInResultListener
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.BootScreen
import com.android.partagix.ui.screens.HomeScreen
import com.android.partagix.ui.screens.InventoryCreateOrEditItem
import com.android.partagix.ui.screens.InventoryScreen
import com.android.partagix.ui.screens.InventoryViewItem
import com.android.partagix.ui.screens.LoanScreen
import com.android.partagix.ui.screens.LoginScreen
import com.android.partagix.ui.screens.ViewAccount
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class App(
  private val activity: MainActivity,
  private val auth: Authentication? = null,
  private val db: Database = Database()

) : ComponentActivity(), SignInResultListener {

  private var authentication: Authentication = auth ?: Authentication(activity, this)

  private lateinit var navigationActions: NavigationActions
  private lateinit var fusedLocationClient: FusedLocationProviderClient

  // private val inventoryViewModel: InventoryViewModel by viewModels()

  private val itemViewModel = ItemViewModel(db = db)

  @Composable
  fun Create() {

  }

  override fun onSignInSuccess(user: FirebaseUser?) {

  }

  override fun onSignInFailure(errorCode: Int) {

  }

  @Composable
  private fun ComposeNavigationSetup() {

  }

  @Composable
  fun ComposeMainContent(
      modifier: Modifier = Modifier,
      navController: NavHostController,
      selectedDestination: String,
      onDrawerClicked: () -> Unit = {},
  ) {

  }

  private fun checkLocationPermissions(retries: Int = 3): Boolean {
    return true
  }

  @SuppressLint("MissingPermission")
  @Composable
  private fun ComposeNavigationHost(
      navController: NavHostController,
      modifier: Modifier = Modifier
  ) {

  }

  companion object {
    private const val TAG = "App"
  }
}
