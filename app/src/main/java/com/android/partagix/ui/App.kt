package com.android.partagix.ui

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.InventoryScreen
import kotlinx.coroutines.launch

@Composable
fun App(
    inventoryViewModel: InventoryViewModel,
) {

  NavigationWrapper(
      inventoryViewModel = inventoryViewModel,
  )
}

@Composable
private fun NavigationWrapper(
    inventoryViewModel: InventoryViewModel,
) {
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  val navController = rememberNavController()
  val navigationActions = remember(navController) { NavigationActions(navController) }
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val selectedDestination = navBackStackEntry?.destination?.route ?: Route.INVENTORY

  AppContent(
      navController = navController,
      selectedDestination = selectedDestination,
      navigationActions = navigationActions,
      inventoryViewModel = inventoryViewModel,
  ) {
    scope.launch { drawerState.open() }
  }
}

@Composable
fun AppContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    selectedDestination: String,
    navigationActions: NavigationActions,
    inventoryViewModel: InventoryViewModel,
    onDrawerClicked: () -> Unit = {},
) {
  Row(modifier = modifier.fillMaxSize()) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.inverseOnSurface)) {
          MyNavHost(
              navController = navController,
              modifier = Modifier.weight(1f),
              navigationActions = navigationActions,
              inventoryViewModel = inventoryViewModel,
          )
        }
  }
}

@Composable
private fun MyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigationActions: NavigationActions,
    inventoryViewModel: InventoryViewModel,
) {
  NavHost(
      modifier = modifier,
      navController = navController,
      startDestination = Route.INVENTORY,
  ) {
    composable(Route.INVENTORY) {
      InventoryScreen(
          inventoryViewModel = inventoryViewModel,
          navigateToTopLevelDestination = navigationActions::navigateTo)
    }
    composable(Route.ACCOUNT) { /*AccountScreen()*/}
    composable(Route.INVENTORY) { /*InventoryScreen()*/}
    composable(Route.BORROW) { /*BorrowScreen()*/}
  }
}
