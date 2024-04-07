@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.partagix.ui.screens

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.R
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.Horizontalfullwidth
import com.android.partagix.ui.components.ItemList
import com.android.partagix.ui.components.TopSearchBar
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.partagix.ui.navigation.TopLevelDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
  val uiState by inventoryViewModel.uiState.collectAsStateWithLifecycle()
  inventoryViewModel.getInventory()
  Scaffold(
      modifier = modifier.testTag("inventoryScreen"),
      topBar = {
        TopSearchBar(inventoryViewModel = inventoryViewModel, uiState = uiState , modifier = modifier)
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.INVENTORY,
            navigateToTopLevelDestination = navigateToTopLevelDestination,
            modifier = modifier.testTag("inventoryScreenBottomNavBar"))
      },
      floatingActionButton = {
        FloatingActionButton(
            onClick = {
              // TO-DO go to create -----------------------------------------------
            }) {
              Icon(Icons.Default.Add, contentDescription = "Create")
            }
      }) { innerPadding ->
        Log.w(TAG, "com.android.partagix.model.inventory.Inventory: called")
        if (uiState.items.isEmpty()) {
          Box(modifier = modifier
              .padding(innerPadding)
              .fillMaxSize()) {
            Text(
                text = "There is no items in the inventory.",
                modifier =
                modifier
                    .align(Alignment.Center)
                    .testTag("inventoryScreenMainContentText"))
          }
        } else {
            Column {
                Text(text = "Borrowed items",
                    style = TextStyle(
                        fontSize = 18.sp,
                        //fontFamily = FontFamily(Font(R.font.inter)),
                        fontWeight = FontWeight(500),
                        color = Color(0xFF000000),

                        ),
                    modifier = modifier.padding(top = innerPadding.calculateTopPadding()).fillMaxWidth()
                )
                ItemList(itemList = uiState.borrowedItems,
                    onClick = {Log.w(TAG, "veut changer")},// TO-DO go to edit-------------------------
                    modifier = modifier.fillMaxWidth().height(210.dp)
                )
                Text(text = "My inventory",
                    style = TextStyle(
                        fontSize = 18.sp,
                        //fontFamily = FontFamily(Font(R.font.inter)),
                        fontWeight = FontWeight(500),
                        color = Color(0xFF000000),

                        )
                )
                
                ItemList(itemList = uiState.items,
                    onClick = {Log.w(TAG, "veut changer" )},// TO-DO go to edit-------------------------
                    modifier = modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())
                )
            }
        }
      }
}
