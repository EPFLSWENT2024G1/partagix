package com.android.partagix.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.android.partagix.ui.components.DropDown
import com.android.partagix.ui.components.StampDimensions
import com.android.partagix.ui.navigation.NavigationActions

/**  */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Stamp(
    modifier: Modifier = Modifier,
    stampViewModel: StampViewModel,
    navigationActions: NavigationActions,
) {
  val MAX_LABEL_LENGTH = 40

  Scaffold(
      modifier = modifier.testTag("").fillMaxWidth().testTag("stampScreen"),
      topBar = {
        TopAppBar(
            modifier = modifier.fillMaxWidth().testTag("topAppBar"),
            title = { Text("Export QR code stamps", modifier = Modifier.testTag("title")) },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("backButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null)
                  }
            })
      }) {
        var uiDetailedDimension by remember { mutableStateOf("") }
        var uiLabel by remember { mutableStateOf("") }

        Column(
            modifier =
                modifier
                    .padding(it)
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 8.dp)
                    .testTag("mainContent"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top) {
              Text(
                  text = "Dimension of stamps",
                  modifier = modifier.fillMaxWidth().padding(top = 24.dp).testTag("dimensionLabel"))
              Box(modifier = modifier.fillMaxWidth().testTag("dimensionBox")) {
                uiDetailedDimension = DropDown("Dimensions", StampDimensions)
              }

              Text(
                  text = "Label on stamps",
                  modifier = modifier.fillMaxWidth().padding(top = 16.dp).testTag("labelLabel"))
              OutlinedTextField(
                  modifier = modifier.fillMaxWidth().testTag("labelTextField"),
                  value = uiLabel,
                  onValueChange = { if (it.length <= MAX_LABEL_LENGTH) uiLabel = it },
                  label = { Text("(optional) max. 40 characters") },
                  readOnly = false)

              Spacer(modifier = modifier.height(24.dp))

              Row(modifier = modifier.fillMaxWidth().testTag("downloadRow")) {
                Button(
                    modifier = modifier.fillMaxWidth().testTag("downloadButton"),
                    onClick = {
                      stampViewModel.generateQRCodeAndSave(
                          "ZQWESXRDCFTVGY42", uiLabel, uiDetailedDimension)
                      navigationActions.goBack()
                    },
                    content = { Text("Download stamps") })
              }
            }
      }
}
