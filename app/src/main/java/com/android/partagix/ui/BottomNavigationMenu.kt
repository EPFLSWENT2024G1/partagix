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
fun BottomNavigationBar(
    selectedDestination: String,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit
) {
  NavigationBar(modifier = Modifier.fillMaxWidth()) {
    TOP_LEVEL_DESTINATIONS.forEach { destination ->
      NavigationBarItem(
          selected = selectedDestination == destination.route,
          onClick = {
            navigateToTopLevelDestination(destination)
          },
          icon = { Icon(imageVector = destination.icon, contentDescription = null) })
    }
  }
}

@Preview(showBackground = true)
@Composable
fun previewScaffold() {
  val navController = rememberNavController()
  val navigate = NavigationActions::navigateTo
}
