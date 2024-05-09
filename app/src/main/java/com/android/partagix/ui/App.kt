package com.android.partagix.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.partagix.MainActivity
import com.android.partagix.model.Database
import com.android.partagix.model.HomeViewModel
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.LoanViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.StampViewModel
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.auth.SignInResultListener
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.user.User
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.BootScreen
import com.android.partagix.ui.screens.BorrowScreen
import com.android.partagix.ui.screens.EditAccount
import com.android.partagix.ui.screens.HomeScreen
import com.android.partagix.ui.screens.InventoryCreateOrEditItem
import com.android.partagix.ui.screens.InventoryScreen
import com.android.partagix.ui.screens.InventoryViewItemScreen
import com.android.partagix.ui.screens.LoanScreen
import com.android.partagix.ui.screens.LoginScreen
import com.android.partagix.ui.screens.ManageLoanRequest
import com.android.partagix.ui.screens.QrScanScreen
import com.android.partagix.ui.screens.StampScreen
import com.android.partagix.ui.screens.ViewAccount
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class App(
    private val activity: MainActivity,
    private val auth: Authentication? = null,
    private val db: Database = Database(),
) : SignInResultListener {

  private var authentication: Authentication = Authentication(activity, this)

  private var navigationActionsInitialized = false
  private lateinit var navigationActions: NavigationActions
  private lateinit var fusedLocationClient: FusedLocationProviderClient

  private val inventoryViewModel = InventoryViewModel(db = db)
  private val manageViewModel = ManageLoanViewModel(db = db)

  private val loanViewModel = LoanViewModel(db = db)
  private val itemViewModel =
      ItemViewModel(
          db = db,
          onItemSaved = { item -> inventoryViewModel.updateItem(item) },
          onItemCreated = { item -> inventoryViewModel.createItem(item) },
      )
  private val userViewModel = UserViewModel(db = db)

  @Composable
  fun Create() {

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    ComposeNavigationSetup()

    navigationActions.navigateTo(Route.BOOT)
  }

  fun navigateForTest(route: String) {
    navigationActions.navigateTo(route)
  }

  override fun onSignInSuccess(user: FirebaseUser?) {
    if (user != null) {
      val newUser =
          User(
              user.uid,
              user.displayName ?: "",
              user.email ?: "",
              "0",
              Inventory(user.uid, emptyList()))
      db.getUser(user.uid, { db.createUser(newUser) }, {})
    }
    // test that navigationActions has been initialized

    if (navigationActionsInitialized) {
      navigationActions.navigateTo(Route.HOME)
    }
    Log.d(TAG, "onSignInSuccess: user=$user")
  }

  override fun onSignInFailure(errorCode: Int) {
    // Go back to safe state and report error
    navigationActions.navigateTo(Route.BOOT)
    Log.e(TAG, "onSignInFailure: errorCode=$errorCode")
  }

  @Composable
  private fun ComposeNavigationSetup() {
    Log.d(TAG, "onComposeNavigationSetup: called")
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    navigationActions = remember(navController) { NavigationActions(navController) }

    navigationActionsInitialized = true
    val selectedDestination = Route.BOOT // This is not even used
    ComposeMainContent(
        navController = navController,
        selectedDestination = selectedDestination,
    ) {
      scope.launch { drawerState.open() }
    }
  }

  @Composable
  fun ComposeMainContent(
      modifier: Modifier = Modifier,
      navController: NavHostController,
      selectedDestination: String,
      onDrawerClicked: () -> Unit = {},
  ) {
    Row(modifier = modifier.fillMaxSize()) {
      Column(
          modifier =
          Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseOnSurface)) {
            ComposeNavigationHost(
                navController = navController,
                modifier = Modifier.weight(1f),
            )
          }
    }
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
      composable(Route.HOME) {
        inventoryViewModel.getInventory()
        loanViewModel.getAvailableLoans()

        HomeScreen(
            homeViewModel = HomeViewModel(),
            manageLoanViewModel = manageViewModel,
            navigationActions = navigationActions)
      }
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
              loanViewModel = loanViewModel,
              userViewModel = userViewModel,
              itemViewModel = itemViewModel,
              modifier = modifier)
        } else {
          inventoryViewModel.getInventory()
          HomeScreen(
              homeViewModel = HomeViewModel(),
              manageLoanViewModel = manageViewModel,
              navigationActions = navigationActions)
        }
      }
      composable(Route.BORROW) {
        BorrowScreen(navigationActions = navigationActions)
      }
      composable(Route.INVENTORY) {
        inventoryViewModel.getInventory()
        InventoryScreen(
            inventoryViewModel = inventoryViewModel,
            navigationActions = navigationActions,
            itemViewModel = itemViewModel)
      }

      composable(Route.QR_SCAN) { QrScanScreen(navigationActions) }

      composable(
          Route.ACCOUNT,
      ) {
        userViewModel.setUserToCurrent()
        ViewAccount(navigationActions = navigationActions, userViewModel = userViewModel)
      }

      composable(
          Route.EDIT_ACCOUNT,
      ) {
        EditAccount(navigationActions = navigationActions, userViewModel = UserViewModel())
      }

      composable(Route.VIEW_ITEM) {
        itemViewModel.getUser()
        InventoryViewItemScreen(navigationActions, itemViewModel)
      }

      composable(Route.CREATE_ITEM) {
        itemViewModel.getUser()
        InventoryCreateOrEditItem(itemViewModel, navigationActions, mode = "create")
      }
      composable(Route.EDIT_ITEM) {
        InventoryCreateOrEditItem(itemViewModel, navigationActions, mode = "edit")
      }
      composable(Route.MANAGE_LOAN_REQUEST) {
        ManageLoanRequest(
            manageLoanViewModel = manageViewModel, navigationActions = navigationActions)
      }
      composable(
          Route.STAMP + "/{itemId}",
          arguments = listOf(navArgument("itemId") { type = NavType.StringType })) {
            StampScreen(
                modifier = modifier,
                stampViewModel = StampViewModel(activity),
                itemID = it.arguments?.getString("itemId") ?: "",
                navigationActions = navigationActions)
          }
    }
  }

  companion object {
    private const val TAG = "App"
  }
}
