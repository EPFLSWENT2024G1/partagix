package com.android.partagix.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun LabeledText(modifier: Modifier = Modifier, label: String = "label", text: String = "text") {
  Box(modifier = modifier.padding(8.dp).testTag("labeledText")) {
    Column(modifier = Modifier.testTag("mainColumn")) {
      Text(
          modifier = Modifier.testTag("label"),
          text = label,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onBackground,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
          text = text,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.padding(6.dp, 0.dp, 0.dp, 0.dp).fillMaxHeight().testTag("text"),
      )
    }
  }
}
