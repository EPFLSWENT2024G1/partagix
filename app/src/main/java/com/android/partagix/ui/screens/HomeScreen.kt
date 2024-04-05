package com.android.partagix.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

@Composable
fun HomeScreen(navigationActions: NavigationActions) {
  Column {
    Text("Home Screen")
    Button(onClick = { navigationActions.navigateTo(Route.INVENTORY) }) { Text("Go to Inventory") }
  }
}
