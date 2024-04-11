package com.android.partagix.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.partagix.model.item.Item

@Composable
fun ItemListColumn(
    List: List<Item>,
    Title: String,
    corner: String,
    onClick: (Item) -> Unit,
    onClickCorner: () -> Unit,
    modifier: Modifier = Modifier
) {
  Log.w(Title, List.size.toString())
  Column(modifier = modifier) {
    Row {
      Text(
          text = Title,
          style =
              TextStyle(
                  fontSize = 18.sp,
                  fontWeight = FontWeight(500),
                  color = Color(0xFF000000),
              ),
          modifier = Modifier.width(120.dp))

      Text(
          text = corner,
          textAlign = TextAlign.Right,
          modifier = Modifier.clickable { onClickCorner() })
    }

    ItemList(itemList = List, onClick = onClick, modifier = Modifier.fillMaxSize())
  }
}
