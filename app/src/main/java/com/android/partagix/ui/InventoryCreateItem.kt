package com.android.partagix.ui

import Category
import android.location.Location
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.partagix.model.ItemViewModel


@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryCreateItem() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create a new item") },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = { /*TODO: navigate back to inventory screen*/ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                })
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        val vm = ItemViewModel()
        var uiCategory by remember { mutableStateOf(Category("", "") )}
        var uiName by remember { mutableStateOf("") }
        var uiDescription by remember { mutableStateOf("") }
        var uiAuthor by remember { mutableStateOf("") } // TODO: get user's name
        var uiVisibility by remember { mutableStateOf(0) }
        var uiQuantity by remember { mutableStateOf(1) }
        var uiLocation by remember { mutableStateOf(Location("")) }

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(.4f)
                    ) {
                        MainImagePicker()
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        OutlinedTextField(
                            value = uiName,
                            onValueChange = {
                                            uiName = it
                            },
                            label = { Text("Object name") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = false
                        )

                        OutlinedTextField(
                            value = uiAuthor,
                            onValueChange = {
                                            uiAuthor = it
                            },
                            label = { Text("Author") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true // TODO: let this field be editable or not ??
                        )
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                OutlinedTextField(
                    value = uiDescription,
                    onValueChange = {
                                    uiDescription = it
                    },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                    readOnly = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(.5f)
                            .padding(end = 8.dp)
                    ) {
                        DropDown("Category", CategoryItems) // todo get the selected category
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        DropDown("Visibility", VisibilityItems) // todo get the selected visibility
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiQuantity.toString(),
                    onValueChange = {
                                    uiQuantity = it.toString().toInt()
                    }, // TODO: sanitize input to uint only
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiLocation.toString(), // TODO: get default or user's location
                    onValueChange = {
                        // TODO: update location
                    },
                    label = { Text("Where") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { /*TODO*/ },
                        content = { Text("Download QR code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        vm.saveWithUiState()
                        // TODO: navigate back to inventory screen
                    },
                    content = { Text("Create") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
