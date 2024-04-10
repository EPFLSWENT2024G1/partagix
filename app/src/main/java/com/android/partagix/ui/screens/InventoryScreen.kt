@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.partagix.ui.screens

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.ItemList
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.navigation.TopLevelDestination

@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
  val uiState by inventoryViewModel.uiState.collectAsStateWithLifecycle()
  val keyboardController = LocalSoftwareKeyboardController.current
  var active by remember { mutableStateOf(false) }

  inventoryViewModel.getInventory()
  Scaffold(
      modifier = modifier.testTag("inventoryScreen"),
      topBar = {
        SearchBar(
            query = uiState.query,
            onQueryChange = { inventoryViewModel.filterItems(it) },
            onSearch = { inventoryViewModel.filterItems(it) },
            active = false,
            onActiveChange = { active = it },
            modifier = modifier.fillMaxWidth().padding(20.dp).testTag("inventoryScreenSearchBar"),
            placeholder = { Text("Search a Task") },
            leadingIcon = {
              if (!active) {
                Icon(Icons.Default.Menu, contentDescription = "Search")
              } else {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Search",
                    modifier =
                        modifier.clickable {
                          inventoryViewModel.filterItems("")

                          keyboardController?.hide()
                        }.testTag("inventoryScreenSearchBarBack"))
              }
            },
            trailingIcon = {
              Icon(
                  Icons.Default.Search,
                  contentDescription = "Search",
                  modifier = modifier.clickable { keyboardController?.hide() }.testTag("inventoryScreenSearchBarSearch"))
            }) {
              Text("Search a Task")
            }
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.INVENTORY,
            navigateToTopLevelDestination = navigateToTopLevelDestination,
            modifier = modifier.testTag("inventoryScreenBottomNavBar"))
      },
      floatingActionButton = {
        FloatingActionButton(
            modifier = modifier.testTag("inventoryScreenFab"),
            onClick = {
              /*navigationActions.navigateTo(Route.CREATE_TODO)*/
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
          ItemList(
              itemList = uiState.items,
              onClick = { Log.d(TAG, "Item clicked") },
              modifier = modifier.padding(innerPadding).testTag("inventoryScreenItemList"))
        }
      }
}
