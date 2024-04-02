package com.android.partagix.ui.screens

import Item
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.ui.BottomNavigationBar
import com.android.partagix.ui.ItemUi
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.navigation.TopLevelDestination

@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit
) {
  val uiState by inventoryViewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
      topBar = {// TO_DO
          },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.INVENTORY,
            navigateToTopLevelDestination = navigateToTopLevelDestination)
      },
      floatingActionButton = {
          /*FloatingActionButton(
              onClick = {
                  /*navigationActions.logNavigationStack()
                  navigationActions.navigateTo(Route.CREATE_TODO)
                  navigationActions.logNavigationStack()*/
              }) {
              Icon(Icons.Default.Create, contentDescription = "Create")
          }    TO_DO*/
      }) { innerPadding ->
      Log.w(TAG, "Inventory: called")
      if (uiState.items.isEmpty()){
          Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
              Text(
                  text = "There is no items in the inventory.",
                  modifier = Modifier.align(Alignment.Center))
          }

      } else {
          LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = innerPadding) {
              items(uiState.items.size) { index ->
                  val items = uiState.items.get(index)
                  Box(
                      modifier =
                      Modifier.fillMaxSize().clickable {
                          Log.w(TAG, "veut changer sa tache")
                          /*navigationActions.logNavigationStack()
                          navigationActions.navigateTo(Route.EDIT_TODO + "/${toDo.uid}")
                          navigationActions.logNavigationStack()*/
                      }) {
                      ItemUi(
                          // remplacer par des items avec les bonnes valeurs
                          uiState.items.get(index))
                  }
              }
          }
      }
      /*Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
      Text(
          text = "There is ${uiState.items.size} items in the inventory.",
          modifier = Modifier.align(Alignment.Center))
    }*/
  }
}
