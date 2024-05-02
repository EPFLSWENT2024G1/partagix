package com.android.partagix.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.partagix.ui.navigation.NavigationActions

@Composable
fun QrScanScreen(navigationActions: NavigationActions, modifier: Modifier = Modifier) {
  Column {
    IconButton(
        modifier = Modifier.testTag("backButton"), onClick = { navigationActions.goBack() }) {
          Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
        }
    Text(modifier = Modifier.testTag("TODO tag"), text = "QrScanScreen")
  }
}
