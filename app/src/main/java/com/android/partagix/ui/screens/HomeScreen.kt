package com.android.partagix.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.QrCodeScanner
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.HomeViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.ItemListColumn
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

private const val quickAccessText = "Quick access"

private const val findItemButtonName = "Find item to borrow"

private const val quickScanButtonName = "Quick scan"

private const val findItemIventoryName = "Find item in inventory"

private const val newBorrowingRequestsText = "New borrowing requests"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    manageLoanViewModel: ManageLoanViewModel,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier
) {
  val uiState by manageLoanViewModel.uiState.collectAsStateWithLifecycle()
  Scaffold(
      modifier = modifier.testTag("homeScreen"),
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("homeScreenTopAppBar"),
            title = { Text(text = "Welcome back, ${"User" /* TODO get actual user name */}") },
            actions = {
              IconButton(onClick = { /* TODO go to notification screen */}) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
              }
            })
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.HOME,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = Modifier.testTag("homeScreenBottomNavBar"))
      }) { innerPadding ->
        Column(
            modifier =
                Modifier.fillMaxSize().padding(innerPadding).testTag("homeScreenMainContent")) {
              HorizontalDivider(modifier = Modifier.fillMaxWidth())
              Text(
                  text = quickAccessText,
                  modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp),
                  style = MaterialTheme.typography.titleLarge)
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween) {
                    BigButton(
                        logo = Icons.Default.PersonSearch,
                        text = findItemButtonName,
                        onClick = {},
                        modifier = Modifier.weight(1f).testTag("homeScreenFirstBigButton"))
                    Spacer(modifier = Modifier.width(8.dp))
                    BigButton(
                        logo = Icons.Default.QrCodeScanner,
                        text = quickScanButtonName,
                        onClick = {},
                        modifier = Modifier.weight(1f).testTag("homeScreenSecondBigButton"))
                    Spacer(modifier = Modifier.width(8.dp))
                    BigButton(
                        logo = Icons.Default.ImageSearch,
                        text = findItemIventoryName,
                        onClick = {},
                        modifier = Modifier.weight(1f).testTag("homeScreenThirdBigButton"))
                  }
              Text(
                  text = newBorrowingRequestsText,
                  modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp),
                  style = MaterialTheme.typography.titleLarge)
              ItemListColumn(
                  list = uiState.items, // TODO replace this with the actual list of borrowing
                  // requests
                  users = uiState.users,
                  loan = uiState.loans,
                  title = "",
                  corner = "see all",
                  onClick = { /* useless on this list */},
                  onClickCorner = { navigationActions.navigateTo(Route.MANAGE_LOAN_REQUEST) },
                  isCornerClickable = true,
                  expandable = true,
                  expanded = uiState.expanded,
                  manageLoanViewModel = manageLoanViewModel,
                  modifier = Modifier.testTag("homeScreenItemList"))
            }
      }
}

@Composable
fun BigButton(logo: ImageVector, text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Box(
      modifier =
          modifier
              // .weight(1f) // Répartit l'espace horizontal disponible équitablement
              .aspectRatio(1f) // Garde le bouton carré
              .size(70.dp) // Vous pouvez ajuster la taille en fonction de vos besoins
              .border(
                  width = 1.dp,
                  color = Color.Black,
                  shape = RoundedCornerShape(8.dp)) // Add a rounded borer the button
              .clickable(onClick = onClick),
      contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(imageVector = logo, contentDescription = null, modifier = Modifier.size(32.dp))
          Spacer(modifier = Modifier.height(8.dp))
          Text(
              text = text,
              style = MaterialTheme.typography.bodyMedium,
              color = Color.Black, // Vous pouvez ajuster la couleur en fonction de vos besoins
              textAlign = TextAlign.Center)
        }
      }
}
