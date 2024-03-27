package com.android.partagix.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object Route {
  const val OVERVIEW = "Overview"
  const val MAP = "Map"
  const val CREATE = "Create"
  const val EDIT = "Edit"
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
        TopLevelDestination(route = Route.OVERVIEW, icon = Icons.Filled.Menu, textId = 1),
        TopLevelDestination(route = Route.MAP, icon = Icons.Filled.LocationOn, textId = 2))

val TODO_SCREENS =
    listOf(
        TopLevelDestination(route = Route.CREATE, icon = Icons.Filled.AccountCircle, textId = 3),
        TopLevelDestination(route = Route.EDIT, icon = Icons.Filled.List, textId = 4))
