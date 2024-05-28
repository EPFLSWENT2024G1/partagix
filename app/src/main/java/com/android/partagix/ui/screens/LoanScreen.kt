package com.android.partagix.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.partagix.R
import com.android.partagix.model.FilterAction
import com.android.partagix.model.FilterState
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.LoanViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.UserViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.Filter
import com.android.partagix.ui.components.ItemList
import com.android.partagix.ui.components.TopSearchBar
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
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
    manageLoanViewModel: ManageLoanViewModel = ManageLoanViewModel(),
    isMapLoadingOptimized: Boolean = true,
) {

  val loansUIState = loanViewModel.uiState.collectAsState()
  var loans = loansUIState.value.availableLoans
  Log.d(TAG, "loans: $loans")

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

  LaunchedEffect(key1 = loansUIState) {
    loans = loansUIState.value.availableLoans
    Log.d(TAG, "loans2: $loans")
  }
  LaunchedEffect(key1 = userUiState) { currentLocation = userUiState.value.location }

  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val mapPadding = screenHeight * 0.1f
  var mapLoaded by remember { mutableStateOf(!isMapLoadingOptimized) }

  val context = LocalContext.current
  val isDarkTheme = isSystemInDarkTheme()
  val style = if (isDarkTheme) R.raw.style_dark else R.raw.style_light

  Scaffold(
      modifier = modifier.testTag("makeLoanRequestScreen"),
      topBar = {
        TopSearchBar(
            filter = { loanViewModel.applyFilters(FilterState(query = it)) },
            query = loansUIState.value.filterState.query ?: "",
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
                  properties =
                      MapProperties(
                          isMyLocationEnabled = true,
                          isIndoorEnabled = true,
                          mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, style)),
                  modifier = modifier.testTag("LoanScreenMaps"),
                  onMapLoaded = { mapLoaded = true }) {
                    if (mapLoaded) {
                      loans.forEach { loan ->
                        val item = loan.item
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
                              MaterialTheme.colorScheme.background,
                              RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                          .border(
                              width = 1.dp,
                              color = Color(0xFF464646),
                              shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                          .padding(PaddingValues(10.dp))) {
                    if (mapLoaded) {
                      ItemList(
                          itemList = loans.map { it.item },
                          users = loans.map { it.user },
                          loan = emptyList(),
                          onItemClick = {
                            itemViewModel.updateUiItem(it)
                            navigationActions.navigateTo(Route.VIEW_OTHERS_ITEM)
                          },
                          manageLoanViewModel = manageLoanViewModel,
                          stickyHeader = {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                                verticalArrangement =
                                    Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
                                modifier =
                                    modifier
                                        .background(MaterialTheme.colorScheme.background)
                                        .padding(PaddingValues(bottom = 10.dp))) {
                                  Filter(
                                      title = "Distance",
                                      selectedValue = {
                                        if (currentLocation != null) {
                                          loanViewModel.applyFilters(
                                              FilterState(
                                                  location = currentLocation!!,
                                                  radius = it.toDouble(),
                                                  query = loansUIState.value.filterState.query,
                                                  atLeastQuantity =
                                                      loansUIState.value.filterState
                                                          .atLeastQuantity))
                                        }
                                      },
                                      unit = "km",
                                      minUnit = "1",
                                      maxUnit = "50",
                                      minValue = 1f,
                                      maxValue = 50f,
                                      sliderTextValue = {
                                        "Up to ${String.format("%02d", it.toInt())} km"
                                      },
                                      onReset = {
                                        loanViewModel.resetFilter(FilterAction.ResetLocation)
                                      },
                                      value =
                                          loansUIState.value.filterState.radius?.toFloat() ?: 0f,
                                      modifier =
                                          modifier
                                              .fillMaxWidth(.3f)
                                              .testTag("LoanScreenDistanceFilter"))
                                  Filter(
                                      title = "Quantity",
                                      selectedValue = {
                                        loanViewModel.applyFilters(
                                            FilterState(
                                                location = currentLocation,
                                                radius = loansUIState.value.filterState.radius,
                                                query = loansUIState.value.filterState.query,
                                                atLeastQuantity = it.toInt()))
                                      },
                                      unit = "items",
                                      minUnit = "1",
                                      maxUnit = "100",
                                      minValue = 1f,
                                      maxValue = 100f,
                                      sliderTextValue = {
                                        "At least ${String.format("%02d", it.toInt())} items"
                                      },
                                      onReset = {
                                        loanViewModel.resetFilter(FilterAction.ResetAtLeastQuantity)
                                      },
                                      value =
                                          loansUIState.value.filterState.atLeastQuantity?.toFloat()
                                              ?: 0f,
                                      modifier =
                                          modifier.fillMaxWidth(.3f).testTag("LoanScreenQtyFilter"))
                                }
                          },
                          modifier = Modifier.fillMaxSize().testTag("LoanScreenItemListView"),
                          isExpandable = false,
                          isOutgoing = false,
                          wasExpanded = emptyList(),
                          navigationActions = navigationActions)
                    }
                  }
            }
      }
}
