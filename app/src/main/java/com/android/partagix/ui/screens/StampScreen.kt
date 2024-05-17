package com.android.partagix.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.partagix.model.StampViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.DropDown
import com.android.partagix.ui.components.StampDimensions
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

const val MAX_LABEL_LENGTH = 20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StampScreen(
    modifier: Modifier = Modifier,
    stampViewModel: StampViewModel,
    itemID: String,
    navigationActions: NavigationActions,
) {
  Scaffold(
      modifier = modifier.fillMaxWidth().testTag("stampScreen"),
      topBar = {
        TopAppBar(
            modifier = modifier.fillMaxWidth().testTag("topAppBar"),
            title = { Text("Export QR code stamps", modifier = modifier.testTag("title")) },
            navigationIcon = {
              IconButton(
                  modifier = modifier.testTag("backButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null)
                  }
            })
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.STAMP,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = modifier.testTag("bottomBar"))
      }) {
        var uiDetailedDimension by remember { mutableStateOf("") }
        var uiLabel by remember { mutableStateOf("") }

        Column(
            modifier =
                modifier
                    .padding(it)
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 8.dp, vertical = 0.dp)
                    .testTag("mainContent"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top) {
              Box(
                  modifier =
                      modifier.fillMaxWidth().requiredHeight(40.dp).testTag("dimensionBox")) {
                    uiDetailedDimension = DropDown("Dimensions", StampDimensions)
                  }

              OutlinedTextField(
                  modifier = modifier.fillMaxWidth().testTag("labelTextField"),
                  value = uiLabel,
                  onValueChange = { if (it.length <= MAX_LABEL_LENGTH) uiLabel = it },
                  label = { Text("Label on stamps") },
                  placeholder = { Text(text = "(optional) max $MAX_LABEL_LENGTH characters") },
                  singleLine = true,
                  readOnly = false)

              Row(modifier = modifier.fillMaxWidth().padding(top = 20.dp).testTag("downloadRow")) {
                Button(
                    modifier = modifier.fillMaxWidth().testTag("downloadButton"),
                    onClick = {
                      stampViewModel.generateQRCodeAndSave(itemID, uiLabel, uiDetailedDimension)
                      navigationActions.goBack()
                    },
                    content = { Text("Download stamps") })
              }
            }
      }
}
