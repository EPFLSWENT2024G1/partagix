package com.android.partagix.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object Route {
  const val INVENTORY = "Inventory"
}

data class TopLevelDestination(
    val route: String,
    val icon: ImageVector,
    val textId: Int,
)

class NavigationActions(private val navController: NavHostController) {

  fun navigateTo(destination: TopLevelDestination) {
    navController.navigate(destination.route) {

      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      // on the back stack as users select items

      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      // Avoid multiple copies of the same destination when
      // reselecting the same item
      launchSingleTop = true
      // Restore state when reselecting a previously selected item
      restoreState = true
    }
  }

  fun goBack() {
    navigateTo(TOP_LEVEL_DESTINATIONS.first())
  }
}

val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(route = Route.INVENTORY, icon = Icons.Filled.Menu, textId = 1),
    )
