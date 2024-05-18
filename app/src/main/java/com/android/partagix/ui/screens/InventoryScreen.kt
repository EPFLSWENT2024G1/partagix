@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.partagix.ui.screens

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.emptyConst.emptyItem
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.ItemListColumn
import com.android.partagix.ui.components.TopSearchBar
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

/**
 * InventoryScreen is a composable that displays the inventory screen of the user.
 *
 * @param inventoryViewModel a view model to get the inventory.
 * @param navigationActions a class to navigate to different screens.
 * @param modifier a Modifier to apply to this layout.
 */
@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel,
    navigationActions: NavigationActions,
    manageLoanViewModel: ManageLoanViewModel,
    itemViewModel: ItemViewModel,
    modifier: Modifier = Modifier,
) {
  val uiState by inventoryViewModel.uiState.collectAsStateWithLifecycle()
  var incomingRequests by remember { mutableIntStateOf(0) }
  var outgoingRequests by remember { mutableIntStateOf(0) }

  manageLoanViewModel.getInComingRequestCount { incomingRequests = it }
  manageLoanViewModel.getOutGoingRequestCount { outgoingRequests = it }

  Scaffold(
      modifier = modifier.testTag("inventoryScreen"),
      topBar = {
        TopSearchBar(
            filter = { inventoryViewModel.filterItems(it) },
            query = uiState.query,
            modifier = modifier.testTag("inventoryScreenSearchBarBack"))
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.INVENTORY,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = modifier.testTag("inventoryScreenBottomNavBar"))
      },
      floatingActionButton = {
        FloatingActionButton(
            modifier = modifier.size(60.dp).testTag("inventoryScreenFab"),
            shape = FloatingActionButtonDefaults.largeShape,
            containerColor = MaterialTheme.colorScheme.primary,
            onClick = {
              val i = emptyItem
              itemViewModel.updateUiItem(i)
              navigationActions.navigateTo(Route.CREATE_ITEM)
            }) {
              Icon(
                  Icons.Default.Add,
                  modifier = Modifier.size(40.dp),
                  tint = MaterialTheme.colorScheme.onPrimary,
                  contentDescription = "Create")
            }
      }) { innerPadding ->
        Log.w(TAG, "com.android.partagix.model.inventory.Inventory: called")
        if (uiState.items.isEmpty()) {
          Box(
              modifier =
                  modifier
                      .padding(innerPadding)
                      .fillMaxSize()
                      .testTag("inventoryScreenNoItemBox")) {
                Text(
                    text =
                        "There is no items in your inventory, click on the + button to add your first item",
                    textAlign = TextAlign.Center,
                    modifier =
                        modifier.align(Alignment.Center).testTag("inventoryScreenNoItemText"))
              }
        } else {
          Column(modifier = modifier.padding(innerPadding).fillMaxSize()) {
            Row(
                modifier = modifier.fillMaxWidth().padding(16.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(
                      text = "Loan requests",
                      style =
                          TextStyle(
                              fontSize = 18.sp,
                              fontWeight = FontWeight(1000),
                              color = Color(0xFF000000),
                          ))
                  Text(
                      text = "See Old",
                      fontSize = 12.sp,
                      modifier =
                          Modifier.clickable { navigationActions.navigateTo(Route.FINISHED_LOANS) })
                }
            Row(
                modifier =
                    Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp).fillMaxSize(0.1f),
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
            ) {
              Button(
                  modifier = modifier.fillMaxWidth(0.49f),
                  colors = buttonColors(Color.White),
                  border = BorderStroke(width = 1.dp, color = Color.Black),
                  shape = MaterialTheme.shapes.small,
                  onClick = {
                    manageLoanViewModel.getLoanRequests(
                        isOutgoing = false,
                        onSuccess = { navigationActions.navigateTo(Route.MANAGE_LOAN_REQUEST) })
                  }) {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center) {
                          Icon(
                              Icons.Filled.Download,
                              tint = Color.Black,
                              contentDescription = "incoming requests",
                              modifier = Modifier.align(Alignment.CenterHorizontally))
                          Text(
                              text = "Incoming Requests ($incomingRequests)",
                              color = Color.Black,
                              style = TextStyle(fontSize = 10.sp),
                              modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                  }
              Button(
                  modifier = modifier.fillMaxWidth(),
                  colors = buttonColors(Color.White),
                  border = BorderStroke(width = 1.dp, color = Color.Black),
                  shape = MaterialTheme.shapes.small,
                  onClick = {
                    manageLoanViewModel.getLoanRequests(
                        isOutgoing = true,
                        onSuccess = { navigationActions.navigateTo(Route.MANAGE_OUTGOING_LOAN) })
                  }) {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center) {
                          Icon(
                              Icons.Filled.Upload,
                              tint = Color.Black,
                              contentDescription = "outgoing requests",
                              modifier = Modifier.align(Alignment.CenterHorizontally))
                          Text(
                              text = "Outgoing Requests ($outgoingRequests)",
                              color = Color.Black,
                              style = TextStyle(fontSize = 10.sp),
                              modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                  }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              ItemListColumn(
                  list = uiState.borrowedItems,
                  users = uiState.usersBor,
                  loan = uiState.loanBor,
                  title = "Borrowed items",
                  corner = uiState.borrowedItems.size.toString(),
                  onClick = {
                    itemViewModel.updateUiItem(it)
                    navigationActions.navigateTo(Route.VIEW_ITEM)
                  },
                  onClickCorner = {},
                  isCornerClickable = false,
                  isClickable = false,
                  isOutgoing = false,
                  isExpandable = false,
                  navigationActions = navigationActions,
                  manageLoanViewModel = manageLoanViewModel,
                  modifier =
                      Modifier.padding(8.dp, 0.dp, 8.dp, 0.dp)
                          .fillMaxHeight(0.4f)
                          .testTag("inventoryScreenBorrowedItemList"))

              Spacer(modifier = Modifier.height(8.dp))
              ItemListColumn(
                  list = uiState.items,
                  users = uiState.users,
                  loan = uiState.loan,
                  title = "Inventory item",
                  corner = uiState.items.size.toString(),
                  onClick = {
                    itemViewModel.updateUiItem(it)
                    navigationActions.navigateTo(Route.VIEW_ITEM)
                  },
                  onClickCorner = {},
                  isCornerClickable = false,
                  isClickable = true,
                  isExpandable = false,
                  isOutgoing = false,
                  navigationActions = navigationActions,
                  manageLoanViewModel = manageLoanViewModel,
                  modifier =
                      Modifier.padding(8.dp, 0.dp, 8.dp, 0.dp).testTag("inventoryScreenItemList"))
            }
          }
        }
      }
}
