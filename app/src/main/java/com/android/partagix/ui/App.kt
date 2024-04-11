package com.android.partagix.ui

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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.auth.SignInResultListener
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.BootScreen
import com.android.partagix.ui.screens.HomeScreen
import com.android.partagix.ui.screens.InventoryCreateItem
import com.android.partagix.ui.screens.InventoryScreen
import com.android.partagix.ui.screens.LoginScreen
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class App(activity: MainActivity) : ComponentActivity(), SignInResultListener {
  private var authentication: Authentication = Authentication(activity, this)

  private lateinit var navigationActions: NavigationActions

  // private val inventoryViewModel: InventoryViewModel by viewModels()
  private val inventoryViewModel = InventoryViewModel()

  @Composable
  fun Create() {
    ComposeNavigationSetup()
    InventoryScreen(inventoryViewModel = inventoryViewModel, navigationActions = navigationActions)
    // -----------------------a changer
    // Initially, navigate to the boot screen
    // navigationActions.navigateTo(Route.VIEW_ITEM + "/4MsBEw8bkLagBkWYy3nc")
    navigationActions.navigateTo(Route.BOOT)
  }

  override fun onSignInSuccess(user: FirebaseUser?) {
    navigationActions.navigateTo(Route.HOME)
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination = navBackStackEntry?.destination?.route ?: Route.INVENTORY

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
              Modifier.fillMaxSize().background(MaterialTheme.colorScheme.inverseOnSurface)) {
            ComposeNavigationHost(
                navController = navController,
                modifier = Modifier.weight(1f),
            )
          }
    }
  }

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
      composable(Route.BORROW) { /*BorrowScreen()*/}
      composable(Route.INVENTORY) {
        InventoryScreen(
            inventoryViewModel = inventoryViewModel, navigationActions = navigationActions)
      }
      composable(Route.ACCOUNT) { /*AccountScreen()*/}
      composable(
          Route.VIEW_ITEM + "/{itemId}",
          arguments = listOf(navArgument("itemId") { type = NavType.StringType })) {
            val itemId = it.arguments?.getString("itemId")
            InventoryViewItem(navigationActions, ItemViewModel(id = itemId))
          }
      composable(Route.INVENTORY_CREATE_ITEM) {
        InventoryCreateItem(
            itemViewModel = ItemViewModel(),
            navigationActions,
        )
      }
    }
  }

  companion object {
    private const val TAG = "App"
  }
}
