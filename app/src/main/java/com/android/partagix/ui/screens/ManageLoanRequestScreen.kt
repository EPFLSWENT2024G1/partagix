package com.android.partagix.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.ItemListColumn
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route


@Composable
fun ManageLoanRequest(inventoryViewModel: InventoryViewModel,
                      navigationActions: NavigationActions,
                      modifier: Modifier = Modifier) {
    val uiState by inventoryViewModel.uiState.collectAsStateWithLifecycle()
    inventoryViewModel.getInventory()
    Scaffold (
        modifier = modifier,
        topBar = {
            /* TODO: Add a top bar with just the name of partagix*/
        },
        bottomBar = {
             BottomNavigationBar(
                 selectedDestination = Route.INVENTORY,
                 navigateToTopLevelDestination = navigationActions::navigateTo,
                 modifier = modifier.testTag("inventoryScreenBottomNavBar"))
        }) { innerPadding ->
        ItemListColumn(
            list = uiState.items,
            users = uiState.users,
            loan = uiState.loan,
            title = "Borrowing requests",
            corner = uiState.items.size.toString(),
            isCornerClickable = false ,
            onClick = {/* TODO: scroll down just like in the home screen */},
            onClickCorner = {},
            modifier = Modifier.padding(innerPadding))

    }



}
