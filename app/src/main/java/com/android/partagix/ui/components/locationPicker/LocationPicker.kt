package com.android.partagix.ui.components.locationPicker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import com.android.partagix.model.location.Location
import kotlinx.coroutines.delay

@Composable
fun LocationPicker(
    location: String,
    loc: Location?,
    onTextChanged: (String) -> Unit,
    onLocationLookup: (String) -> Unit
) {
  var focused by remember { mutableStateOf(false) }
  TextField(
      value = location,
      singleLine = true,
      onValueChange = { onTextChanged(it) },
      label = { Text(text = "Location", color = MaterialTheme.colorScheme.onBackground) },
      modifier =
          Modifier.fillMaxWidth().testTag("addressField").onFocusChanged {
            focused = it.isFocused
            if (!focused) {
              onTextChanged(loc?.locationName ?: location)
            }
          },
      placeholder = { Text(text = "Enter an address") },
      textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
      supportingText = {
        if (loc == null && location.isNotEmpty()) {
          Text(text = "Location not found", color = MaterialTheme.colorScheme.error)
        } else if (loc != null) {
          Text(
              text = loc.locationName,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.testTag("locationField"))
        }
      },
      leadingIcon = {
        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier)
      })

  LaunchedEffect(
      key1 = location,
      block = {
        delay(500)
        onLocationLookup(location)
      })
}
