@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.partagix.ui.screens

import android.content.ContentValues.TAG
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
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
    itemViewModel: ItemViewModel,
    manageLoanViewModel: ManageLoanViewModel,
    modifier: Modifier = Modifier,
) {
  val uiState by inventoryViewModel.uiState.collectAsStateWithLifecycle()

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
            modifier = modifier.testTag("inventoryScreenFab"),
            onClick = {
              val i = Item("", Category("", ""), "", "", Visibility.PUBLIC, 1, Location(""), "")
              itemViewModel.updateUiItem(i)
              navigationActions.navigateTo(Route.CREATE_ITEM)
            }) {
              Icon(Icons.Default.Add, contentDescription = "Create")
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
                    text = "There is no items in the inventory.",
                    modifier =
                        modifier.align(Alignment.Center).testTag("inventoryScreenNoItemText"))
              }
        } else {
          Column(
              modifier =
                  modifier
                      .padding(innerPadding)
                      .padding(start = 10.dp, end = 10.dp)
                      .fillMaxSize()) {
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
                    isExpandable = false,
                    isClickable = true,
                    modifier = Modifier.height(190.dp).testTag("inventoryScreenBorrowedItemList"),
                manageLoanViewModel = manageLoanViewModel,
            )

                Spacer(modifier = Modifier.height(14.dp))
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
                    isExpandable = false,
                    isClickable = true,
                    manageLoanViewModel = manageLoanViewModel,
                modifier = Modifier.testTag("inventoryScreenItemList"))
              }
        }
      }
}
