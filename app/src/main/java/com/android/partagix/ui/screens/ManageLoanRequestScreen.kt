package com.android.partagix.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.ItemListColumn
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageLoanRequest(
    manageLoanViewModel: ManageLoanViewModel,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier,
    expandable: Boolean = false,
) {
  val uiState by manageLoanViewModel.uiState.collectAsStateWithLifecycle()
  Scaffold(
      modifier = modifier,
      topBar = {
        TopAppBar(
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("backButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.width(48.dp))
                  }
            },
            modifier = Modifier.testTag("homeScreenTopAppBar"),
            title = { Text(text = "Partagix") },
        )
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.HOME,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = modifier.testTag("manageScreenBottomNavBar"))
      }) { innerPadding ->
        if (uiState.items.isEmpty()) {
          Column(modifier = modifier.fillMaxSize().padding(innerPadding)) {
            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            Box(
                modifier =
                    modifier.padding(innerPadding).fillMaxSize().testTag("manageScreenNoItemBox")) {
                  Text(
                      text = "There is no loan request.",
                      modifier = modifier.align(Alignment.Center).testTag("manageScreenNoItemText"))
                }
          }
        } else {
          Column(
              verticalArrangement = Arrangement.Center,
              modifier =
                  modifier.fillMaxSize().padding(innerPadding).testTag("manageScreenMainContent")) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(6.dp))

                ItemListColumn(
                    list = uiState.items,
                    users = uiState.users,
                    loan = uiState.loans,
                    title = "Incoming requests",
                    corner = "History",
                    isCornerClickable = true,
                    isExpandable = true,
                    expandState = expandable,
                    wasExpanded = uiState.expanded,
                    onClick = { navigationActions.navigateTo(Route.FINISHED_LOANS) },
                    onClickCorner = { navigationActions.navigateTo(Route.FINISHED_LOANS) },
                    manageLoanViewModel = manageLoanViewModel,
                    isClickable = false,
                    isOutgoing = false,
                    modifier =
                        Modifier.padding(horizontal = 10.dp)
                            .testTag("manageLoanScreenItemListColumn"))
              }
        }
      }
}
