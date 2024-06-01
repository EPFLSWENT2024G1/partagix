package com.android.partagix.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.android.partagix.utils.displayedDateFormat
import com.android.partagix.utils.formattedDate
import com.android.partagix.utils.stripTime
import com.google.firebase.auth.FirebaseAuth
import java.util.Date
import java.util.Locale

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
  var isCalendarVisible by remember { mutableStateOf(false) }
  val unavailableDates = uiState.value.unavailableDates
  val datePickerState =
      DatePickerState(
          locale = Locale.getDefault(),
          selectableDates =
              object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                  val currentDate = stripTime(Date(utcTimeMillis))
                  return unavailableDates.none { stripTime(it) == currentDate }
                }

                override fun isSelectableYear(year: Int): Boolean {
                  return true
                }
              })

  var actualUser = FirebaseAuth.getInstance().currentUser?.uid ?: ""
  var item = uiState.value.item
  val user = uiState.value.user

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
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
                                  if (actualUser != user.id) {
                                    navigationActions.navigateTo(
                                        "${Route.OTHER_ACCOUNT}/${user.id}")
                                  } else {
                                    navigationActions.navigateTo(Route.ACCOUNT)
                                  }
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

                Box(modifier = Modifier.padding(8.dp)) {
                  Column {
                    Text(
                        text = "Availability",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                          Text(
                              text =
                                  if (uiState.value.unavailableDates.none {
                                    stripTime(it) == formattedDate(Date())
                                  } && uiState.value.unavailableDates.isNotEmpty()) {
                                    "unavailable until " +
                                        displayedDateFormat(
                                            uiState.value.unavailableDates
                                                .sorted()[uiState.value.unavailableDates.size - 1])
                                  } else {
                                    "available"
                                  },
                              style = MaterialTheme.typography.bodyMedium,
                              color = MaterialTheme.colorScheme.onBackground,
                              modifier = Modifier.padding(6.dp, 0.dp, 0.dp, 0.dp),
                          )

                          IconButton(
                              onClick = { isCalendarVisible = true },
                              content = {
                                Icon(Icons.Default.DateRange, contentDescription = null)
                              })
                        }
                  }
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
              Spacer(modifier = Modifier.height(8.dp))
            }
        if (isCalendarVisible) {
          DatePickerDialog(
              modifier = Modifier.testTag("endDatePicker"),
              onDismissRequest = { isCalendarVisible = false },
              confirmButton = {
                TextButton(modifier = Modifier.testTag("endDateOk"), onClick = {}) { Text("OK") }
              },
              dismissButton = {
                TextButton(
                    modifier = Modifier.testTag("endDateCancel"),
                    onClick = { isCalendarVisible = false }) {
                      Text("Cancel")
                    }
              }) {
                DatePicker(datePickerState)
              }
        }
      }
}
