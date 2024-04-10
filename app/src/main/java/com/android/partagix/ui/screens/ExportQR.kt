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
import com.android.partagix.ui.components.DropDown
import com.android.partagix.ui.components.StampDimensions
import com.android.partagix.ui.navigation.NavigationActions

/**  */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportQR(
    qrViewModel: QRViewModel,
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

    var uiDimension by remember { mutableStateOf(Dimension.MEDIUM) }
    var uiLabel by remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(it).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
          Column(modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            Row(modifier = modifier.fillMaxWidth()) {
              Text(text = "Dimension of stamps", modifier = modifier.fillMaxWidth(0.3f))
              Box(modifier = modifier.fillMaxWidth()) {
                uiDimension = DropDown("StampDimension", StampDimensions) // todo
                /*Visibility.valueOf(DropDown("Visibility", VisibilityItems).uppercase())*/
              }
            }

            Spacer(modifier = modifier.height(8.dp))

            Row(modifier = modifier.fillMaxWidth()) {
              Text(text = "Stamp label", modifier = modifier.fillMaxWidth(0.3f))
              OutlinedTextField(
                  value = uiLabel, // todo
                  onValueChange = { uiLabel = it }, // todo
                  label = { Text("max. 40 characters") },
                  // TODO:  limit to 40 characters
                  modifier = modifier.fillMaxWidth(),
                  readOnly = false)
            }

            Spacer(modifier = modifier.height(8.dp))

            Row(modifier = modifier.fillMaxWidth()) {
              Button(
                  onClick = { /*TODO*/
                    navigationActions.goBack()
                  },
                  content = { Text("Download stamps") },
                  modifier = modifier.fillMaxWidth())
            }
          }
        }
  }
}
