package com.android.partagix.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.partagix.ui.navigation.TopLevelDestination

@Composable
fun BottomNavigationBar(
    selectedDestination: String,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
  NavigationBar(modifier = modifier.fillMaxWidth()) {
    TOP_LEVEL_DESTINATIONS.forEach { destination ->
      NavigationBarItem(
          modifier = Modifier.testTag("bottomNavBarItem-${destination.route}"),
          selected = selectedDestination == destination.route,
          onClick = { navigateToTopLevelDestination(destination) },
          icon = { Icon(imageVector = destination.icon, contentDescription = null) })
    }
  }
}

@Preview(showBackground = true)
@Composable
fun previewScaffold() {
  val navController = rememberNavController()
  val navigate = NavigationActions(navController)
  BottomNavigationBar(
      selectedDestination = "Home",
      navigateToTopLevelDestination = { dest -> navigate.navigateTo(dest) })
}