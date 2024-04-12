@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.partagix.ui.screens

import android.location.Location
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.ItemViewModel
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
    modifier: Modifier = Modifier,
) {
  val uiState by inventoryViewModel.uiState.collectAsStateWithLifecycle()
  inventoryViewModel.getInventory()
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
            onClick = {
              val i = Item("", Category("", ""), "", "", Visibility.PUBLIC, 1, Location(""))
              itemViewModel.updateUiState(i)
              navigationActions.navigateTo(Route.CREATE_ITEM)
            },
            modifier = modifier.testTag("inventoryScreenFab"),
        ) {
          Icon(Icons.Default.Add, contentDescription = "Create")
        }
      }) { innerPadding ->
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
          Column(modifier = modifier.padding(innerPadding).fillMaxSize()) {
            ItemListColumn(
                List = uiState.borrowedItems,
                users = uiState.usersBor,
                loan = uiState.loanBor,
                Title = "Borrowed items",
                corner = uiState.borrowedItems.size.toString(),
                onClick = {
                  itemViewModel.updateUiState(it)
                  navigationActions.navigateTo(Route.VIEW_ITEM + "/${it.id}")
                },
                onClickCorner = { /*TODO*/},
                modifier = Modifier.height(220.dp))

            ItemListColumn(
                List = uiState.items,
                users = uiState.users,
                loan = uiState.loan,
                Title = "Inventory item",
                corner = uiState.items.size.toString(),
                onClick = {
                  itemViewModel.updateUiState(it)
                  navigationActions.navigateTo(Route.VIEW_ITEM + "/${it.id}")
                },
                onClickCorner = { /*TODO*/},
                // modifier = Modifier
            )
          }
        }
      }
}
