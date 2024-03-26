package com.android.partagix.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoryDropDown(category: String) {
  Box(modifier = Modifier.fillMaxWidth()) {
    val expend = remember { mutableStateOf(false) }
    val selectedCategory = remember { mutableStateOf(category) }
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
            modifier =
                Modifier.fillMaxWidth().height(250.dp).wrapContentSize(align = Alignment.Center),
            scrollState = rememberScrollState(),
        ) {
          DropDownItems.forEach {
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

val DropDownItems =
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
