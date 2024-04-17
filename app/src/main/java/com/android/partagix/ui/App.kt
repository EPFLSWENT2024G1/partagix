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

  private var authentication: Authentication = Authentication(activity, this)

  private lateinit var navigationActions: NavigationActions
  private lateinit var fusedLocationClient: FusedLocationProviderClient

  // private val inventoryViewModel: InventoryViewModel by viewModels()
  private val inventoryViewModel = InventoryViewModel(db = db)
  private val itemViewModel = ItemViewModel(db = db)
  private val userViewModel = UserViewModel(db = db)

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
    if (retries == 0) {
      return false
    }

    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED) {
      Log.d(TAG, "checkLocationPermissions: requesting permissions")
      ActivityCompat.requestPermissions(
          activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

      return checkLocationPermissions(retries - 1)
    } else {
      Log.d(TAG, "checkLocationPermissions: permissions granted")
      return true
    }
  }

  @SuppressLint("MissingPermission")
  @Composable
  private fun ComposeNavigationHost(
      navController: NavHostController,
      modifier: Modifier = Modifier
  ) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.INVENTORY,
    ) {
      composable(Route.BOOT) { BootScreen(authentication, navigationActions, modifier) }
      composable(Route.LOGIN) { LoginScreen(authentication, modifier) }
      composable(Route.HOME) { HomeScreen(navigationActions) }
      composable(Route.LOAN) {
        if (checkLocationPermissions()) {
          fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
              Log.d(TAG, "onCreate: location=$location")
              userViewModel.updateLocation(location)
            }
          }
          LoanScreen(
              navigationActions = navigationActions,
              inventoryViewModel = inventoryViewModel,
              userViewModel = userViewModel,
              modifier = modifier)
        } else {
          HomeScreen(navigationActions)
        }
      }

      composable(Route.INVENTORY) {
        InventoryScreen(
            inventoryViewModel = inventoryViewModel,
            navigationActions = navigationActions,
            itemViewModel = itemViewModel)
      }
      composable(
          Route.ACCOUNT,
      ) {
        println("navigated to account screen")
        ViewAccount(navigationActions = navigationActions, userViewModel = UserViewModel())
      }
      composable(
          Route.VIEW_ITEM + "/{itemId}",
          arguments = listOf(navArgument("itemId") { type = NavType.StringType })) {
            // val itemId = it.arguments?.getString("itemId")
            InventoryViewItem(navigationActions, itemViewModel)
          }
      composable(
          Route.CREATE_ITEM,
      /*arguments = listOf(navArgument("itemId") { type = NavType.StringType })*/ ) {
        InventoryCreateOrEditItem(itemViewModel, navigationActions, mode = "create")
      }
      composable(
          Route.EDIT_ITEM + "/{itemId}",
          arguments = listOf(navArgument("itemId") { type = NavType.StringType })) {
            // val itemId = it.arguments?.getString("itemId")
            InventoryCreateOrEditItem(itemViewModel, navigationActions, mode = "edit")
          }
    }
  }

  companion object {
    private const val TAG = "App"
  }
}
