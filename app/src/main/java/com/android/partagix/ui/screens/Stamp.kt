package com.android.partagix.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.android.partagix.model.stampDimension.StampDimension
import com.android.partagix.ui.navigation.NavigationActions

/**  */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Stamp(
    stampViewModel: StampViewModel,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier,
) {

  Scaffold(
      modifier = modifier.testTag("").fillMaxWidth(),
      topBar = {
        TopAppBar(
            title = { Text("Export QR code stamps") },
            modifier = modifier.fillMaxWidth(),
            navigationIcon = {
              IconButton(onClick = { navigationActions.goBack() }) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
              }
            })
      },
  ) {

    /*
    DropDown of stamp sizes "XXcm x XXcm (XX per A4 page)"
    "additional text on the stamp (optional), max XX characters"
    Generate and Download stamps
    */

    var uiDimension by remember { mutableStateOf(StampDimension.MEDIUM) }
    var uiLabel by remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(it).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
          Column(modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            Spacer(modifier = modifier.height(16.dp))

            Text(text = "Dimension of stamps", modifier = modifier.fillMaxWidth())
            Box(modifier = modifier.fillMaxWidth()) {
              /*uiDimension =
              StampDimension.valueOf(
                  DropDown("Dimensions", StampDimensions)) // todo return the right field */
              uiDimension = StampDimension.MEDIUM
            }

            Spacer(modifier = modifier.height(8.dp))

            Text(text = "Label on stamp", modifier = modifier.fillMaxWidth(0.3f))
            OutlinedTextField(
                value = uiLabel, // todo
                onValueChange = { uiLabel = it }, // todo limit to 40 characters
                label = { Text("(optional) max. 40 characters") },
                modifier = modifier.fillMaxWidth(),
                readOnly = false)

            Spacer(modifier = modifier.height(32.dp))

            Row(modifier = modifier.fillMaxWidth()) {
              Button(
                  onClick = {
                    stampViewModel.generateQRCodeAndSave("ZQWESXRDCFTVGY42", uiLabel, uiLabel)
                  },
                  content = { Text("Download stamps") },
                  modifier = modifier.fillMaxWidth())
            }
          }
        }
  }
}