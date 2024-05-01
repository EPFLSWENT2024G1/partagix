package com.android.partagix.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.partagix.R
import com.android.partagix.model.ItemViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.LabeledText
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.google.firebase.auth.FirebaseAuth

/**
 * Screen to view an item.
 *
 * @param navigationActions a NavigationActions instance to navigate between screens.
 * @param viewModel an ItemViewModel which handles functionality.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryViewItemScreen(navigationActions: NavigationActions, viewModel: ItemViewModel) {
  val uiState = viewModel.uiState.collectAsState()

  var item = uiState.value.item

  LaunchedEffect(key1 = uiState) { item = viewModel.uiState.value.item }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Back to selection") },
            modifier = Modifier.fillMaxWidth().testTag("inventoryViewItemTopBar"),
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("navigationIcon")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null)
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
                    LabeledText(label = "Object Name", text = item.name)

                    LabeledText("Author", item.idUser)
                  }
                }
              }
              Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                LabeledText("Description", item.description)

                Spacer(modifier = Modifier.height(8.dp))

                LabeledText("Category", item.category.name)

                Spacer(modifier = Modifier.height(8.dp))

                LabeledText("Visibility", item.visibility.visibilityLabel)

                Spacer(modifier = Modifier.height(8.dp))

                LabeledText("Quantity", item.quantity.toString())

                Spacer(modifier = Modifier.height(8.dp))

                LabeledText("Where", item.location.toString())

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.padding(8.dp)) {
                  Column() {
                    Text(
                        text = "Availability",
                        style = TextStyle(color = Color.Gray),
                        fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                          Text(
                              text = "available", /*TODO: get item disponibility*/
                              style = TextStyle(color = Color.Black),
                              modifier = Modifier.padding(6.dp, 0.dp, 0.dp, 0.dp),
                          )

                          IconButton(
                              onClick = { /*TODO: see calendar with disponibilities*/},
                              content = {
                                Icon(Icons.Default.DateRange, contentDescription = null)
                              })
                        }
                  }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                  Button(
                      onClick = { navigationActions.navigateTo("${Route.STAMP}/${item.id}") },
                      content = { Text("Download QR code") },
                      modifier = Modifier.fillMaxWidth(0.5f))

                  Spacer(modifier = Modifier.width(8.dp))

                  Button(
                      onClick = { /*TODO: go to loan requests page*/},
                      content = { Text("Loan requests") },
                      modifier = Modifier.fillMaxWidth())
                }

                Spacer(modifier = Modifier.width(8.dp))

                // This should be displayed only if the user is the owner of the item
                if (viewModel.compareIDs(
                    item.idUser, FirebaseAuth.getInstance().currentUser?.uid)) {
                  Button(
                      onClick = { navigationActions.navigateTo(Route.EDIT_ITEM) },
                      content = { Text("Edit") },
                      modifier = Modifier.fillMaxWidth().testTag("editItemButton"))
                }
              }
            }
      }
}
