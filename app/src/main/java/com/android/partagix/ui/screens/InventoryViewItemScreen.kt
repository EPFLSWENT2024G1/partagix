package com.android.partagix.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.partagix.model.BorrowViewModel
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.UserViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.LabeledText
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.google.firebase.auth.FirebaseAuth

/**
 * Screen to view an item.
 *
 * @param navigationActions a NavigationActions instance to navigate between screens.
 * @param itemViewModel an ItemViewModel which handles functionality.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryViewItemScreen(
    navigationActions: NavigationActions,
    itemViewModel: ItemViewModel,
    borrowViewModel: BorrowViewModel,
    userViewModel: UserViewModel,
    viewOthersItem: Boolean = false
) {
  val uiState = itemViewModel.uiState.collectAsState()
  var actualUser = FirebaseAuth.getInstance().currentUser?.uid ?: ""
  var item = uiState.value.item
  val user = uiState.value.user

  var imgBitmap: Bitmap? = null
  val imgFile = item.imageId
  if (imgFile.exists()) {
    // on below line we are creating an image bitmap variable
    // and adding a bitmap to it from image file.
    imgBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
  }

  LaunchedEffect(key1 = uiState) { item = itemViewModel.uiState.value.item }

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
                  Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight()) {
                    AsyncImage(
                        model = item.imageId.absolutePath,
                        contentDescription = "fds",
                        contentScale = ContentScale.FillWidth,
                        modifier =
                            Modifier.fillMaxWidth(0.4f)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(4.dp)),
                        alignment = Alignment.Center)
                  }
                  Spacer(modifier = Modifier.width(4.dp))

                  Column {
                    LabeledText(
                        label = "Object Name",
                        text = item.name,
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f))

                    LabeledText(
                        label = "Owner",
                        text = user.name,
                        modifier =
                            Modifier.fillMaxWidth()
                                .fillMaxHeight()
                                .testTag("ownerField")
                                .clickable {
                                  userViewModel.setUser(user)
                                  navigationActions.navigateTo(Route.OTHER_ACCOUNT)
                                })
                  }
                }
              }
              Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                LabeledText(label = "Description", text = item.description)

                if (!viewOthersItem) {
                  LabeledText(label = "Category", text = item.category.name)

                  LabeledText(label = "Visibility", text = item.visibility.visibilityLabel)
                }

                LabeledText(label = "Quantity", text = item.quantity.toString())

                LabeledText(
                    label = "Location",
                    text = item.location.extras?.getString("display_name", "Unknown") ?: "Unknown")

                Row(modifier = Modifier.fillMaxWidth()) {
                  LabeledText(
                      modifier = Modifier.fillMaxWidth(0.8f),
                      label = "Availability",
                      text =
                          "Now available") /* todo : branch with the availability like in itemUi */

                  IconButton(
                      onClick = { /*TODO: see calendar with disponibilities*/},
                      content = { Icon(Icons.Default.DateRange, contentDescription = null) })
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                  Button(
                      onClick = { navigationActions.navigateTo("${Route.STAMP}/${item.id}") },
                      content = { Text("Download QR code") },
                      modifier = Modifier.fillMaxWidth(0.5f))

                  Spacer(modifier = Modifier.width(8.dp))

                  // Displays the Edit button to the owner, and Borrow to someone else
                  if (itemViewModel.compareIDs(
                      item.idUser, FirebaseAuth.getInstance().currentUser?.uid)) {

                    Button(
                        onClick = { navigationActions.navigateTo(Route.EDIT_ITEM) },
                        content = { Text("Edit") },
                        modifier = Modifier.fillMaxWidth().testTag("editItemButton"))
                  } else {
                    Button(
                        onClick = {
                          borrowViewModel.startBorrow(item, user)
                          navigationActions.navigateTo(Route.BORROW)
                        },
                        content = { Text("Borrow item") },
                        modifier = Modifier.fillMaxWidth())
                  }
                }
              }
            }
      }
}
