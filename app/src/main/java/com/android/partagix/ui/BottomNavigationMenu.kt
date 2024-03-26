package com.android.partagix.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.partagix.ui.navigation.TopLevelDestination

@Composable
fun BottomNavigationMenu(
    onTabSelect: (String) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String
) {}

/*

@Composable
fun MyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    println("----- We are inside nav host")

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.INBOX,
    ) {
        composable(Route.INBOX) {
            val navigate = NavigationActions::navigateTo
            ScaffoldExample(
                selectedDestination = "Inbox",
                navigateToTopLevelDestination = { navigate }
            )
        }
        composable(Route.DM) {
            EmptyComingSoon()
        }
        composable(Route.ARTICLES) {
            EmptyComingSoon()
        }
        composable(Route.GROUPS) {
            EmptyComingSoon()
        }
    }
}
 */

@Composable
fun BottomNavigationBar(
    selectedDestination: String,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit
) {
  NavigationBar(modifier = Modifier.fillMaxWidth()) {
    TOP_LEVEL_DESTINATIONS.forEach { destination ->
      NavigationBarItem(
          selected = selectedDestination == destination.route,
          onClick = {
            println("----- This is a click on " + destination.route)
            navigateToTopLevelDestination(destination)
          },
          icon = { Icon(imageVector = destination.icon, contentDescription = null) })
    }
    /*
       NavigationBarItem(
           selected = selectedDestination == destination.route,
           onClick = {
               //print("This is a click")
               //navigateToTopLevelDestination(destination)
           },
           icon = {
               androidx.compose.material3.Icon(
                   imageVector = destination.icon,
                   contentDescription = stringResource(id = destination.textId)
               )
           }
       )
    */

  }
}

@Preview(showBackground = true)
@Composable
fun previewScaffold() {
  val navController = rememberNavController()
  val navigate = NavigationActions::navigateTo
}
