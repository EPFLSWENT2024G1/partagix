package com.android.partagix.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.ui.components.CategoryItems
import com.android.partagix.ui.components.DropDown
import com.android.partagix.ui.components.MainImagePicker
import com.android.partagix.ui.components.VisibilityItems
import com.android.partagix.ui.navigation.NavigationActions

/**
 * Screen to create a new item in user's inventory.
 *
 * @param itemViewModel an ItemViewModel which handles functionality.
 * @param navigationActions a NavigationActions instance to navigate between screens.
 * @param modifier Modifier to apply to this layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryCreateItem(
    itemViewModel: ItemViewModel,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier,
) {

  val uiState by itemViewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
      modifier = modifier.testTag("inventoryCreateItem").fillMaxWidth(),
      topBar = {
        TopAppBar(
            title = { Text("Create a new item") },
            modifier = modifier.fillMaxWidth(),
            navigationIcon = {
              IconButton(onClick = { navigationActions.goBack() }) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
              }
            })
      },
  ) {
    var uiCategory by remember { mutableStateOf(Category("", "")) }
    var uiName by remember { mutableStateOf("") }
    var uiDescription by remember { mutableStateOf("") }
    var uiVisibility by remember { mutableStateOf(Visibility.PUBLIC) }
    var uiQuantity by remember { mutableStateOf(1L) }
    var uiLocation by remember { mutableStateOf(Location("")) }

    Column(
        modifier = modifier.padding(it).fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {
          Box(modifier = modifier.fillMaxWidth().height(140.dp).padding(8.dp)) {
            Row(modifier = modifier.fillMaxWidth()) {
              Box(
                  contentAlignment = Alignment.Center,
                  modifier = modifier.fillMaxHeight().fillMaxWidth(.4f)) {
                    MainImagePicker()
                  }

              Spacer(modifier = modifier.width(8.dp))

              Column {
                OutlinedTextField(
                    value = uiName,
                    onValueChange = { uiName = it },
                    label = { Text("Object name") },
                    modifier = modifier.fillMaxWidth(),
                    readOnly = false)

                OutlinedTextField(
                    value =
                        uiState.item
                            .author, // TODO: check with future implementation of Item if author is
                    // correctly linked to user.name by default
                    onValueChange = {},
                    label = { Text("Author") },
                    modifier = modifier.fillMaxWidth(),
                    readOnly = true)
              }
            }
          }
          Column(modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            OutlinedTextField(
                value = uiDescription,
                onValueChange = { uiDescription = it },
                label = { Text("Description") },
                modifier = modifier.fillMaxWidth(),
                minLines = 5,
                readOnly = false)

            Spacer(modifier = modifier.height(8.dp))

            Row(modifier = modifier.fillMaxWidth()) {
              Box(modifier = modifier.fillMaxWidth(.5f).padding(end = 8.dp)) {
                uiCategory = Category("", DropDown("Category", CategoryItems))
              }
              Box(modifier = modifier.fillMaxWidth()) {
                uiVisibility =
                    Visibility.valueOf(DropDown("Visibility", VisibilityItems).uppercase())
              }
            }

            Spacer(modifier = modifier.height(8.dp))

            OutlinedTextField(
                value = uiQuantity.toString(),
                onValueChange = { uiQuantity = it.toLong() },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Quantity") },
                modifier = modifier.fillMaxWidth(),
                readOnly = false)

            Spacer(modifier = modifier.height(8.dp))

            OutlinedTextField(
                value = uiLocation.toString(), // TODO: get default or user's location
                onValueChange = { uiLocation = Location(it) },
                label = { Text("Where") },
                modifier = modifier.fillMaxWidth(),
                readOnly = false)

            Spacer(modifier = modifier.height(8.dp))

            Row(modifier = modifier.fillMaxWidth()) {
              Button(
                  onClick = { /*TODO*/},
                  content = { Text("Download QR code") },
                  modifier = modifier.fillMaxWidth())
            }

            Spacer(modifier = modifier.width(8.dp))

            Button(
                onClick = {
                  itemViewModel.updateUiState(
                      Item(
                          "",
                          uiCategory,
                          uiName,
                          uiDescription,
                          uiState.item.author,
                          uiVisibility,
                          uiQuantity,
                          uiLocation))
                  itemViewModel.saveWithUiState()
                  navigationActions.goBack()
                },
                content = { Text("Create") },
                modifier = modifier.fillMaxWidth())
          }
        }
  }
}
