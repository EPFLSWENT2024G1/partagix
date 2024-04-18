package com.android.partagix.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.partagix.R
import com.android.partagix.model.ItemViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

/**
 * Screen to view an item.
 *
 * @param navigationActions a NavigationActions instance to navigate between screens.
 * @param viewModel an ItemViewModel which handles functionality.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryViewItem(navigationActions: NavigationActions, viewModel: ItemViewModel) {
  val uiState = viewModel.uiState.collectAsState()

  var item = uiState.value.item

  LaunchedEffect(key1 = uiState) { item = viewModel.uiState.value.item }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Back to selection") },
            modifier = Modifier.fillMaxWidth().testTag("inventoryViewItemTopBar"),
            navigationIcon = {
              IconButton(onClick = { navigationActions.goBack() }) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
              }
            })
      },
      bottomBar = {
        BottomNavigationBar(
            modifier = Modifier.testTag("inventoryViewItemBottomBar"),
            selectedDestination = "Inventory",
            navigateToTopLevelDestination = { dest -> navigationActions.navigateTo(dest) })
      },
      modifier = Modifier.fillMaxWidth().testTag("inventoryViewItem")) {
        Column(
            modifier = Modifier.padding(it).fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Box(modifier = Modifier.fillMaxWidth().height(140.dp).padding(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {

                  /*TODO: get photo and display it*/
                  Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight()) {
                    Image(
                        painter =
                            painterResource(
                                id = R.drawable.ic_launcher_background) /*TODO: get item photo*/,
                        contentDescription = null,
                        alignment = Alignment.BottomCenter)
                  }
                  Spacer(modifier = Modifier.width(8.dp))

                  Column {
                    OutlinedTextField(
                        value = item.name,
                        onValueChange = {},
                        label = { Text("Object name") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true)

                    OutlinedTextField(
                        value = item.idUser,
                        onValueChange = {},
                        label = { Text("Author") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true)
                  }
                }
              }
              Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                OutlinedTextField(
                    value = item.description,
                    onValueChange = {},
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                    readOnly = true)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = item.category.name,
                    onValueChange = {},
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true)

                println("Category: ${item.category.name}")

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = item.visibility.name,
                    onValueChange = {},
                    label = { Text("Visibility") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = item.quantity.toString(),
                    onValueChange = {},
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = item.location.toString(),
                    onValueChange = {},
                    label = { Text("Where") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = "Availability", /*TODO: get item availability*/
                    onValueChange = {},
                    label = { Text("Availability") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(), // Apply any necessary modifier
                    trailingIcon = {
                      IconButton(
                          onClick = { /*TODO: see calendar with availabilities*/},
                          content = { Icon(Icons.Default.DateRange, contentDescription = null) })
                    })

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                  Button(
                      onClick = { navigationActions.navigateTo(Route.STAMP) },
                      content = { Text("Download QR code") },
                      modifier = Modifier.fillMaxWidth(0.5f))

                  Spacer(modifier = Modifier.width(8.dp))

                  Button(
                      onClick = { /*TODO: go to loan requests page*/},
                      content = { Text("Loan requests") },
                      modifier = Modifier.fillMaxWidth())
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { navigationActions.navigateTo("${Route.EDIT_ITEM}/${item.id}") },
                    content = { Text("Edit") },
                    modifier = Modifier.fillMaxWidth())
              }
            }
      }
}
