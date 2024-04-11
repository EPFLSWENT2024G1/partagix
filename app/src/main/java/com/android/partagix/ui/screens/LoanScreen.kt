package com.android.partagix.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.partagix.model.InventoryViewModel
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
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private const val TAG = "LoanScreen"

@Composable
fun LoanScreen(
    navigationActions: NavigationActions,
    inventoryViewModel: InventoryViewModel,
    modifier: Modifier = Modifier
) {

  val uiState = inventoryViewModel.uiState.collectAsState()
  var items = uiState.value.items

  // Simulate a large list of items
  for (i in 0..10) {
    items = items.plus(items)
  }

  var cameraPositionState by remember { mutableStateOf<CameraPositionState>(CameraPositionState()) }

  if (items.isNotEmpty()) {
    val firstItemLocation = items.first().location
    val point = LatLng(firstItemLocation.latitude, firstItemLocation.longitude)
    cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(point, 14f)
    }
  }

  LaunchedEffect(key1 = uiState) { items = uiState.value.items }

  Scaffold(
      modifier = modifier.testTag("makeLoanRequestScreen"),
      topBar = { TopSearchBar(filter = { inventoryViewModel.filterItems(it) }) },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.LOAN,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = modifier)
      }) { innerPadding ->
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = modifier.fillMaxWidth().fillMaxHeight(.5f)) {
              GoogleMap(
                  contentPadding = PaddingValues(bottom = 60.dp),
                  cameraPositionState = cameraPositionState) {
                    items.forEach { item ->
                      Marker(
                          state =
                              MarkerState(
                                  position =
                                      LatLng(item.location.latitude, item.location.longitude)),
                          title = item.name,
                          snippet = item.description,
                          onClick = {
                            // TODO
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
                          .fillMaxHeight(.6f)
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
                        onClick = { navigationActions.navigateTo("${Route.VIEW_ITEM}/${it.id}") },
                        stickyHeader = {
                          Row(
                              modifier =
                                  modifier
                                      .padding(PaddingValues(bottom = 25.dp))
                                      .background(Color.White)) {
                                Filter(
                                    title = "Distance",
                                    selectedValue = {
                                      inventoryViewModel.filterItems(it.toLong().toString())
                                    },
                                    minUnit = "0 km",
                                    maxUnit = "50 km",
                                    minValue = 0f,
                                    maxValue = 50f,
                                    sliderTextValue = {
                                      "Up to ${String.format("%02d", it.toInt())} km"
                                    },
                                )
                                Spacer(modifier = modifier.padding(10.dp))
                                Filter(
                                    title = "Availability",
                                    selectedValue = { value ->
                                      Log.d(TAG, "  Selected  value: $value")
                                    },
                                    disabled = true // todo: complete (not done yet)
                                    )
                                Spacer(modifier = modifier.padding(10.dp))
                                Filter(
                                    title = "Quantity",
                                    selectedValue = {
                                      inventoryViewModel.filterItems(it.toLong().toString())
                                    },
                                    minUnit = "0",
                                    maxUnit = "100",
                                    minValue = 0f,
                                    maxValue = 100f,
                                    sliderTextValue = {
                                      "At least ${String.format("%02d", it.toInt())} items"
                                    },
                                )
                              }
                        },
                        modifier = modifier)
                  }
            }
      }
}
