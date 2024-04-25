package com.android.partagix.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun LabeledText(label: String = "label", text: String = "text", modifier: Modifier = Modifier) {
  Box(modifier = modifier.padding(8.dp)) {
    Column(modifier = modifier) {
      Text(text = label, style = TextStyle(color = Color.Gray), fontSize = 10.sp)
      Spacer(modifier = modifier.height(4.dp))
      Text(
          text = text,
          style = TextStyle(color = Color.Black),
          modifier = modifier.padding(6.dp, 0.dp, 0.dp, 0.dp),
      )
    }
  }
}
