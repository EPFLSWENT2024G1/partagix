package com.android.partagix.ui.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController

object Route {
  const val BOOT = "Boot"
  const val LOGIN = "Login"
  const val HOME = "Home"
  const val INVENTORY = "Inventory"
  const val QR_SCAN = "QrScan"
  const val VIEW_ITEM = "ViewItem"
  const val LOAN = "Loan"
  const val ACCOUNT = "Account"
  const val EDIT_ACCOUNT = "EditAccount"
  const val CREATE_ITEM = "CreateItem"
  const val EDIT_ITEM = "EditItem"
  const val MANAGE_LOAN_REQUEST = "ManageLoanRequest"
  const val FINISHED_LOANS = "FinishedLoans"
  const val STAMP = "Stamp"
  const val STARTLOAN = "StartLoan"
  const val ENDLOAN = "EndLoan"
}

data class TopLevelDestination(
    val route: String,
    val icon: ImageVector,
    val textId: Int,
)

class NavigationActions(private val navController: NavHostController) {

  /**
   * Navigate to a top level destination.
   *
   * @param destination the destination to navigate to.
   */
  fun navigateTo(destination: TopLevelDestination) {
    navigateTo(destination.route)
  }

  /**
   * Navigate to a route.
   *
   * @param route the route to navigate to.
   */
  fun navigateTo(route: String) {
    navController.navigate(route) {
      launchSingleTop = true
      restoreState = true
    }
  }

  /** Navigate back to the previous screen. */
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
        TopLevelDestination(route = Route.HOME, icon = Icons.Filled.Home, textId = 1),
        TopLevelDestination(route = Route.LOAN, icon = Icons.Filled.SupervisorAccount, textId = 2),
        TopLevelDestination(route = Route.INVENTORY, icon = Icons.Filled.Menu, textId = 3),
        TopLevelDestination(route = Route.ACCOUNT, icon = Icons.Filled.AccountCircle, textId = 4),
    )
