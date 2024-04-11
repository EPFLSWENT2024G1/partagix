package com.android.partagix.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.android.partagix.R

@Composable
fun Filter(selectedValue: (Float) -> Unit, modifier: Modifier = Modifier) {
  var sliderPosition by remember { mutableFloatStateOf(0f) }
  var showDialog by remember { mutableStateOf(false) }

  val defaultPaddingValues = ButtonDefaults.ContentPadding
  val startPadding = defaultPaddingValues.calculateStartPadding(LayoutDirection.Ltr)
  val topPadding = defaultPaddingValues.calculateTopPadding()
  val bottomPadding = defaultPaddingValues.calculateBottomPadding()

  OutlinedButton(
      onClick = { showDialog = true },
      shape = RoundedCornerShape(12.dp),
      contentPadding =
          PaddingValues(
              start = startPadding / 2,
              top = topPadding,
              end = 2.dp,
              bottom = bottomPadding,
          )) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
              text = "Distance",
              style =
                  TextStyle(
                      fontSize = 22.sp,
                      fontFamily = FontFamily(Font(R.font.roboto)),
                      fontWeight = FontWeight(500),
                      color = Color(0xFF464646),
                  ),
          )
          Icon(
              imageVector = Icons.Default.ArrowDropDown,
              contentDescription = "Display distance filter",
              modifier = modifier.size(32.dp),
          )
        }
      }

  if (showDialog) {
    Dialog(onDismissRequest = { showDialog = false }) {
      Card(
          modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp),
          shape = RoundedCornerShape(24.dp),
      ) {
        SliderFilter(
            minUnit = "0 km",
            maxUnit = "50 km",
            minValue = 0f,
            maxValue = 50f,
            sliderPosition = sliderPosition,
            onSliderChange = {
              sliderPosition = it
              selectedValue(it)
            },
            sliderTextValue = { "Up to ${String.format("%02d", it.toInt())} km" })
      }
    }
  }
}

@Composable
fun SliderFilter(
    modifier: Modifier = Modifier,
    minUnit: String,
    maxUnit: String,
    minValue: Float = 0f,
    maxValue: Float = 50f,
    sliderPosition: Float,
    onSliderChange: (Float) -> Unit,
    sliderTextValue: (Float) -> String,
) {
  Column(modifier = modifier.padding(16.dp)) {
    Row() {
      Text(
          text = minUnit,
          modifier = modifier.weight(1f),
          textAlign = TextAlign.Start,
          style =
              TextStyle(
                  fontSize = 28.sp,
                  fontFamily = FontFamily(Font(R.font.roboto)),
                  fontWeight = FontWeight(500),
                  color = Color(0xFF464646),
              ))
      Text(
          text = maxUnit,
          modifier = modifier.weight(1f),
          textAlign = TextAlign.End,
          style =
              TextStyle(
                  fontSize = 28.sp,
                  fontFamily = FontFamily(Font(R.font.roboto)),
                  fontWeight = FontWeight(500),
                  color = Color(0xFF464646),
              ))
    }
    Slider(
        value = sliderPosition,
        valueRange = minValue..maxValue,
        onValueChange = { onSliderChange(it) },
        colors =
            SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = Color(0xFFFFFFFF),
            ),
        modifier = modifier.fillMaxWidth())
    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
      Text(
          text = sliderTextValue(sliderPosition),
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Start,
          style =
              TextStyle(
                  fontSize = 28.sp,
                  fontFamily = FontFamily(Font(R.font.roboto)),
                  fontWeight = FontWeight(500),
                  color = Color(0xFF464646),
              ))
    }
  }
}

@Preview
@Composable
fun FilterPreview() {
  Filter(selectedValue = {})
}
