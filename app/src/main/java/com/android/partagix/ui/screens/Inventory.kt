package com.android.partagix.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.ui.BottomNavigationBar
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.navigation.TopLevelDestination

@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit
) {
  val uiState by inventoryViewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
      modifier = Modifier.semantics { testTag = "OverviewScreen" },
      topBar = {},
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.OVERVIEW,
            navigateToTopLevelDestination = navigateToTopLevelDestination)
      },
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
      Text(
          text = "There is ${uiState.items.size} items in the inventory.",
          modifier = Modifier.align(Alignment.Center))
    }
  }
}
