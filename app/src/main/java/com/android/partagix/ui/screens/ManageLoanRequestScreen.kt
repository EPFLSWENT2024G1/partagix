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
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.ItemListColumn
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

@Composable
fun ManageLoanRequest(
    manageLoanViewModel: ManageLoanViewModel,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier
) {
  val uiState by manageLoanViewModel.uiState.collectAsStateWithLifecycle()
  Scaffold(
      modifier = modifier,
      topBar = {
        /* TODO: Add a top bar with just the name of partagix*/
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.HOME,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = modifier.testTag("manageScreenBottomNavBar"))
      }) { innerPadding ->
        if (uiState.items.isEmpty()) {
          Box(
              modifier =
                  modifier.padding(innerPadding).fillMaxSize().testTag("manageScreenNoItemBox")) {
                Text(
                    text = "There is no loan request.",
                    modifier = modifier.align(Alignment.Center).testTag("manageScreenNoItemText"))
              }
        } else {
          ItemListColumn(
              list = uiState.items,
              users = uiState.users,
              loan = uiState.loans,
              title = "Borrowing requests",
              corner = uiState.items.size.toString(),
              isCornerClickable = false,
              expandable = true,
              expanded = uiState.expanded,
              onClick = { /* isnt usefull for this column */},
              onClickCorner = { /* isnt usefull for this column */},
              modifier = Modifier.padding(innerPadding).testTag("manageLoanScreenItemListColumn"))
        }
      }
}
