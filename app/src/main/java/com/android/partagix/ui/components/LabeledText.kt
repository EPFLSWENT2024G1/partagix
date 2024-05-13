package com.android.partagix.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun LabeledText(modifier: Modifier = Modifier, label: String = "label", text: String = "text") {
  Box(modifier = modifier.padding(8.dp).testTag("labeledText")) {
    Column(modifier = modifier.testTag("mainColumn")) {
      Text(
          modifier = modifier.testTag("label"),
          text = label,
          style = TextStyle(color = Color.Gray),
          fontSize = 10.sp)
      Spacer(modifier = modifier.height(4.dp))
      Text(
          text = text,
          style = TextStyle(color = Color.Black),
          modifier = modifier.padding(6.dp, 0.dp, 0.dp, 0.dp).testTag("text"),
      )
    }
  }
}

@Composable
fun LabeledText(modifier: Modifier = Modifier, label: String = "label", text: String = "text",
                leadingIcon: @Composable (() -> Unit)) {
  val contentWithColor: @Composable () -> Unit = @Composable {
    CompositionLocalProvider(
      LocalContentColor provides Color.Gray,
      content = leadingIcon
    )
  }

  Box(modifier = modifier.padding(8.dp).testTag("labeledText")) {
    Column(modifier = modifier.testTag("mainColumn")) {
      Text(
        modifier = modifier.testTag("label"),
        text = label,
        style = TextStyle(color = Color.Gray),
        fontSize = 10.sp)
      Spacer(modifier = modifier.height(4.dp))
      Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        contentWithColor()

      Text(
        text = text,
        style = TextStyle(color = Color.Black),
        modifier = modifier.padding(6.dp, 0.dp, 0.dp, 0.dp).testTag("text"),
      )
    }}
  }
}