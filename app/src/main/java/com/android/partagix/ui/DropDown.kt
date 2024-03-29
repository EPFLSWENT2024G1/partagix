package com.android.partagix.ui

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
import androidx.compose.ui.unit.dp

/**
 * DropDown composable to show a dropdown menu
 *
 * @param label the label display when calling the dropdown
 * @param list the list of items to show in the dropdown, ex : CategoryItems, VisibilityItems...
 */
@Composable
fun DropDown(label: String, list: List<String>) {
  Box(modifier = Modifier.fillMaxWidth()) {
    val expend = remember { mutableStateOf(false) }
    val selectedCategory = remember { mutableStateOf(label) }
    Column(modifier = Modifier.fillMaxWidth()) {
      OutlinedButton(
          onClick = { expend.value = true },
          content = { Text(selectedCategory.value) },
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
                  selectedCategory.value = it
                  expend.value = false
                },
                text = { Text(it) })
          }
        }
      }
    }
  }
}

val CategoryItems =
    listOf(
        "Category 1",
        "Category 2",
        "Category 3",
        "Category 4",
        "Category 5",
        "Category 6",
        "Category 7",
        "Category 8",
        "Category 9",
        "Category 10",
    )

val VisibilityItems = listOf("Public", "Friends", "Private")
