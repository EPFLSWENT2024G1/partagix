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
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.LoanViewModel
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
    loanViewModel: LoanViewModel,
    itemViewModel: ItemViewModel,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
) {

  val loansUIState = loanViewModel.uiState.collectAsState()
  var items = loansUIState.value.availableItems

  val userUiState = userViewModel.uiState.collectAsState()
  var currentLocation = userUiState.value.location

  var cameraPositionState by remember { mutableStateOf(CameraPositionState()) }

  if (currentLocation != null) {
    Log.d(TAG, "currentLocation: $currentLocation")
    val point = LatLng(currentLocation.latitude, currentLocation.longitude)
    cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(point, 14f)
    }
  }

  LaunchedEffect(key1 = loansUIState) { items = loansUIState.value.availableItems }
  LaunchedEffect(key1 = userUiState) { currentLocation = userUiState.value.location }

  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val mapPadding = screenHeight * 0.1f

  Scaffold(
      modifier = modifier.testTag("makeLoanRequestScreen"),
      topBar = {
        TopSearchBar(
            filter = { loanViewModel.filterItems(it) },
            query = loansUIState.value.query,
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
                          .background(
                              Color.White, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                          .border(
                              width = 1.dp,
                              color = Color(0xFF464646),
                              shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                          .padding(PaddingValues(top = 10.dp, bottom = 10.dp))) {
                    ItemList(
                        itemList = items,
                        onClick = {
                          itemViewModel.updateUiState(it)
                          navigationActions.navigateTo(Route.VIEW_ITEM)
                        },
                        stickyHeader = {
                          FlowRow(
                              horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                              verticalArrangement =
                                  Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
                              modifier =
                                  modifier
                                      .background(Color.White)
                                      .padding(
                                          PaddingValues(
                                              start = 10.dp, end = 10.dp, bottom = 10.dp))) {
                                Filter(
                                    title = "Distance",
                                    selectedValue = {
                                      if (currentLocation != null) {
                                        if (it.toDouble() > 0.0) {
                                          loanViewModel.filterItems(
                                              currentPosition = currentLocation!!,
                                              radius = it.toDouble())
                                        } else {
                                          loanViewModel.releaseFilters()
                                        }
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
                                      if (it.toInt() > 1) {
                                        loanViewModel.filterItems(atLeastQuantity = it.toInt())
                                      } else {
                                        loanViewModel.releaseFilters()
                                      }
                                    },
                                    unit = "items",
                                    minUnit = "1",
                                    maxUnit = "100",
                                    minValue = 1f,
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
