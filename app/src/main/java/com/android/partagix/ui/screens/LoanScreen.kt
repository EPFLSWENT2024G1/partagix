package com.android.partagix.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.Filter
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

private const val TAG = "LoanScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanScreen(
    navigationActions: NavigationActions,
    inventoryViewModel: InventoryViewModel,
    modifier: Modifier = Modifier
) {
  val singapore = LatLng(1.35, 103.87)
  val cameraPositionState: CameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(singapore, 11f)
  }

  Scaffold(
      modifier = modifier.testTag("makeLoanRequestScreen"),
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.LOAN,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = modifier)
      }) { innerPadding ->
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = modifier
              .fillMaxWidth()
              .fillMaxHeight(.5f)) {
              GoogleMap(
                  contentPadding = PaddingValues(bottom = 125.dp),
                  cameraPositionState = cameraPositionState) {
                    /*uiState.value.todos.forEach { todo ->
                      Marker(
                        state =
                        MarkerState(
                          position = LatLng(todo.location.latitude, todo.location.longitude)
                        ),
                        title = todo.name,
                        snippet = todo.description,
                        onClick = {
                          // TODO
                          true
                        })
                    }*/
                  }
            }
        Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding),
            contentAlignment = Alignment.BottomCenter) {
              Box(
                  contentAlignment = Alignment.TopCenter,
                  modifier =
                  modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.6f)
                    // .offset(y = (-30).dp)
                    .background(
                      Color.White, RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                    )
                    .border(
                      width = 1.dp,
                      color = Color(0xFF464646),
                      shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                    )
                    .padding(
                      PaddingValues(
                        top = 30.dp, start = 20.dp, end = 20.dp, bottom = 30.dp
                      )
                    )) {
                    Column(
                        modifier = modifier.fillMaxSize()) {
                          Row() {
                            Filter(
                                selectedValue = { value -> Log.d(TAG, "Selected value: $value") })
                            Spacer(modifier = modifier.padding(10.dp))
                            Filter(
                                selectedValue = { value ->
                                  Log.d(TAG, "  Selected  value: $value")
                                })
                            Spacer(modifier = modifier.padding(10.dp))
                            Filter(
                                selectedValue = { value ->
                                  Log.d(TAG, "  Selected  value: $value")
                                })
                          }
                      Box(modifier = modifier.height(2.dp).padding(PaddingValues(top = 30.dp, bottom = 30.dp)).background(color = Color.Red))
                      Text(text = "Selected value ", modifier = modifier.padding(PaddingValues(start = 10.dp, top = 10.dp, bottom = 10.dp)))
                        }
                  }
            }
      }
}
