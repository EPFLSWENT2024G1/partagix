package com.android.partagix.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.partagix.model.stampDimension.StampDimension
import com.android.partagix.model.visibility.Visibility

/**
 * DropDown composable to show a dropdown menu
 *
 * @param label the label display when calling the dropdown
 * @param list the list of items to show in the dropdown, ex : CategoryItems, VisibilityItems...
 * @return the selected value by the user
 */
@Composable
fun DropDown(label: String, list: List<String>): String {
  val expend = remember { mutableStateOf(false) }
  val selectedValue = remember { mutableStateOf(label) }
  Box(modifier = Modifier.fillMaxWidth()) {
    Column(modifier = Modifier.fillMaxWidth()) {
      OutlinedButton(
          onClick = { expend.value = true },
          content = { Text(selectedValue.value) },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10))

      if (expend.value) {
        DropdownMenu(
            expanded = expend.value,
            onDismissRequest = { expend.value = false },
            modifier = Modifier.fillMaxWidth().requiredSizeIn(maxHeight = 250.dp),
            scrollState = rememberScrollState(),
        ) {
          list.forEach {
            DropdownMenuItem(
                onClick = {
                  selectedValue.value = it
                  expend.value = false
                },
                text = { Text(it) },
                modifier = Modifier.testTag(it))
          }
        }
      }
    }
  }
  return selectedValue.value
}

val CategoryItems = // todo : get the list from the database. Current implementation is a hardcoded
                    // list
    listOf(
        "Sound Systems",
        "Lighting",
        "Kitchen Utilities",
        "Textiles",
        "Books",
        "Furniture",
        "Electronics",
        "Tools",
        "Gardening",
        "Others",
    )

val VisibilityItems = Visibility.values().map { it.visibilityLabel }

val StampDimensions = StampDimension.values().map { it.detailedDimension }
