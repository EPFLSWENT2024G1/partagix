package com.android.partagix.ui

import android.graphics.Paint.Align
import android.text.Layout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview(showBackground = true)
@Composable
fun CategoryDropDown() {
    val expend = remember { mutableStateOf(false) }
    val selectedCategory = remember { mutableStateOf("Select a Category") }
    OutlinedButton(
        onClick = { expend.value = true },
        content = { Text(selectedCategory.value) },
        modifier = Modifier
            .fillMaxWidth())

    if (expend.value) {
        DropdownMenu(
            expanded = expend.value,
            onDismissRequest = { expend.value = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            DropDownItems.forEach {
                DropdownMenuItem(
                    onClick = {
                        selectedCategory.value = it
                        expend.value = false
                    },
                    text = { Text(it)})

            }
        }
    }
}

val DropDownItems = listOf(
    "Category 1",
    "Category 2",
    "Category 3",
    "Category 4",
    "Category 5"
)
