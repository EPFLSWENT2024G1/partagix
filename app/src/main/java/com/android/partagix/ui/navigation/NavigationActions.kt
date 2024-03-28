package com.android.partagix.ui.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object Route {
  const val LOGIN = "Login"
  const val HOME = "Home"
  const val INVENTORY = "Inventory"
}

data class TopLevelDestination(
    val route: String,
    val icon: ImageVector,
    val textId: Int,
)

class NavigationActions(private val navController: NavHostController) {

  fun navigateTo(destination: TopLevelDestination) {
    navigateTo(destination.route)
  }

  fun navigateTo(route: String) {
    navController.navigate(route) {
      launchSingleTop = true
      restoreState = true
    }
  }

  fun goBack() {
    navController.popBackStack()
  }

  @SuppressLint("RestrictedApi")
  fun logNavigationStack() {
    val stack = navController.currentBackStack.value

    for (i in stack.indices) {
      val entry = stack[i]
      Log.d(TAG, "Entry $i: ${entry.destination.route}")
    }
  }

  companion object {
    private const val TAG = "NavigationActions"
  }
}

val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(route = Route.INVENTORY, icon = Icons.Filled.Menu, textId = 1),
    )
