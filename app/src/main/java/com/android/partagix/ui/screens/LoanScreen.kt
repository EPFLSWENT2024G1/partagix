package com.android.partagix.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.UserViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.Filter
import com.android.partagix.ui.components.ItemList
import com.android.partagix.ui.components.TopSearchBar
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private const val TAG = "LoanScreen"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoanScreen(
    navigationActions: NavigationActions,
    inventoryViewModel: InventoryViewModel,
    itemViewModel: ItemViewModel,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
) {

  val inventoryUiState = inventoryViewModel.uiState.collectAsState()
  var items = inventoryUiState.value.items

  val userUiState = userViewModel.uiState.collectAsState()
  var currentLocation = userUiState.value.location

  // Simulate a large list of items
  for (i in 0..1) {
    items = items.plus(items)
  }

  var cameraPositionState by remember { mutableStateOf(CameraPositionState()) }

  if (currentLocation != null) {
    Log.d(TAG, "currentLocation: $currentLocation")
    val point = LatLng(currentLocation.latitude, currentLocation.longitude)
    cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(point, 14f)
    }
  }

  LaunchedEffect(key1 = inventoryUiState) { items = inventoryUiState.value.items }
  LaunchedEffect(key1 = userUiState) {
    currentLocation = userUiState.value.location
    Log.d(TAG, "!!! currentLocation: $currentLocation")
  }

  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val mapPadding = screenHeight * 0.1f

  Scaffold(
      modifier = modifier.testTag("makeLoanRequestScreen"),
      topBar = {
        TopSearchBar(
            filter = { inventoryViewModel.filterItems(it) },
            query = inventoryUiState.value.query,
            modifier = modifier.testTag("LoanScreenSearchBar"))
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.LOAN,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = modifier.testTag("LoanScreenBottomNavBar"))
      }) { innerPadding ->
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = modifier.fillMaxWidth().fillMaxHeight(.5f)) {
              GoogleMap(
                  contentPadding = PaddingValues(bottom = mapPadding),
                  cameraPositionState = cameraPositionState,
                  properties = MapProperties(isMyLocationEnabled = true, isIndoorEnabled = true),
                  modifier = modifier.testTag("LoanScreenMaps")) {
                    items.forEach { item ->
                      Marker(
                          state =
                              MarkerState(
                                  position =
                                      LatLng(item.location.latitude, item.location.longitude)),
                          title = item.name,
                          snippet = item.description,
                          onClick = {
                            // do nothing for now on click
                            true
                          })
                    }
                  }
            }
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.BottomCenter) {
              Box(
                  contentAlignment = Alignment.TopCenter,
                  modifier =
                      modifier
                          .fillMaxWidth()
                          .fillMaxHeight(.65f)
                          // .offset(y = (-30).dp)
                          .background(
                              Color.White, RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                          .border(
                              width = 1.dp,
                              color = Color(0xFF464646),
                              shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                          .padding(
                              PaddingValues(
                                  top = 30.dp, start = 20.dp, end = 20.dp, bottom = 30.dp))) {
                    ItemList(
                        itemList = items,
                        onClick = {
                          itemViewModel.updateUiItem(it)
                          navigationActions.navigateTo(Route.VIEW_ITEM)
                        },
                        stickyHeader = {
                          FlowRow(
                              horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                              verticalArrangement =
                                  Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                              modifier =
                                  modifier
                                      .background(Color.White)
                                      .padding(PaddingValues(bottom = 10.dp))) {
                                Filter(
                                    title = "Distance",
                                    selectedValue = {
                                      if (currentLocation != null) {
                                        inventoryViewModel.filterItems(
                                            currentPosition = currentLocation!!,
                                            radius = it.toDouble())
                                      }
                                    },
                                    unit = "km",
                                    minUnit = "0",
                                    maxUnit = "50",
                                    minValue = 0f,
                                    maxValue = 50f,
                                    sliderTextValue = {
                                      "Up to ${String.format("%02d", it.toInt())} km"
                                    },
                                    modifier =
                                        modifier
                                            .fillMaxWidth(.3f)
                                            .testTag("LoanScreenDistanceFilter"))
                                Filter(
                                    title = "Quantity",
                                    selectedValue = {
                                      inventoryViewModel.filterItems(atLeastQuantity = it.toInt())
                                    },
                                    unit = "items",
                                    minUnit = "0",
                                    maxUnit = "100",
                                    minValue = 0f,
                                    maxValue = 100f,
                                    sliderTextValue = {
                                      "At least ${String.format("%02d", it.toInt())} items"
                                    },
                                    modifier =
                                        modifier.fillMaxWidth(.3f).testTag("LoanScreenQtyFilter"))
                              }
                        },
                        modifier = modifier.testTag("LoanScreenItemListView"),
                        users = emptyList(),
                        loan = emptyList())
                  }
            }
      }
}
