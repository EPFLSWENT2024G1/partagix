package com.android.partagix.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

@Composable
fun HomeScreen(navigationActions: NavigationActions, modifier: Modifier = Modifier) {
  Scaffold(
      modifier = modifier.testTag("homeScreen"),
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.INVENTORY,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = modifier.testTag("homeScreenBottomNavBar"))
      }) { innerPadding ->
        Text(text = "Home", modifier = Modifier.padding(innerPadding).testTag("homeScreenMainContent"))
      }
}
