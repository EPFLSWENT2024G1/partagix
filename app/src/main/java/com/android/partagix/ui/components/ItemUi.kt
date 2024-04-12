package com.android.partagix.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.android.partagix.R
import com.android.partagix.model.item.Item

/**
 * Composable function to display an item, in a rectangle form.
 *
 * @param item an Item instance to display.
 */
@Composable
fun ItemUi(item: Item) {

  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth().padding(PaddingValues(start = 10.dp, end = 10.dp))) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            modifier =
                Modifier.fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)) {
              Image(
                  painter = painterResource(id = R.drawable.mutliprise),
                  contentDescription = item.name,
                  contentScale = ContentScale.FillBounds)
              Column(modifier = Modifier.weight(weight = 1f)) {
                Text(
                    text = item.name,
                    // color = Color(0xff49454f),
                    lineHeight = 1.33.em,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.fillMaxWidth())
                //    .wrapContentHeight(align = Alignment.CenterVertically))
                Text(
                    text = "elec(categorie)",
                    // color = Color(0xff49454f),
                    lineHeight = 1.43.em,
                    style = TextStyle(fontSize = 14.sp, letterSpacing = 0.25.sp),
                    modifier = Modifier.fillMaxWidth())
              }
              Column(
                  // horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                  modifier = Modifier.requiredHeight(height = 64.dp)) {
                    Text(
                        text = "x3", // item.number
                        textAlign = TextAlign.End,
                        lineHeight = 1.45.em,
                        style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "3 leased", // item.leased
                        textAlign = TextAlign.End,
                        lineHeight = 1.2.em)
                    /*Icon(
                    painter = painterResource(id = R.drawable.arrow_right),
                    contentDescription = "Icons/arrow_right_24px",
                    tint = Color(0xff41484d))*/
                  }
            }
        Horizontalfullwidth()
      }
  // BuildingBlocksstatelayer1Enabled()

}

@Composable
fun Horizontalfullwidth(modifier: Modifier = Modifier) {
  Column(verticalArrangement = Arrangement.Center, modifier = modifier.fillMaxWidth()) {
    Divider(color = Color(0xffcac4d0), modifier = Modifier.fillMaxWidth())
  }
}

/*
@Composable
fun BuildingBlocksstatelayer1Enabled(modifier: Modifier = Modifier) {
  Box(modifier = modifier.fillMaxSize().padding(bottom = 0.0000152587890625.dp))
}

@Preview
@Composable
fun ItemUiPreview() {
  ItemUi(
      Item(
          "1",
          Category("1", "name"),
          "name",
          "description",
          Visibility.PUBLIC,
          1,
          android.location.Location("")))
}

 */
