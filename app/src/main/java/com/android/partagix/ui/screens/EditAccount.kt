package com.android.partagix.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.location.Location
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.MainImagePicker
import com.android.partagix.ui.components.locationPicker.LocationPicker
import com.android.partagix.ui.components.locationPicker.LocationPickerViewModel
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccount(
    modifier: Modifier = Modifier,
    navigationActions: NavigationActions,
    userViewModel: UserViewModel,
    locationViewModel: LocationPickerViewModel
) {
  val uiState = userViewModel.uiState.collectAsState()
  val user = uiState.value.user

  // Local state variables to hold temporary values for editable fields
  var tempUsername by remember { mutableStateOf(user.name) }
  var tempAddress by remember { mutableStateOf(user.address) }
  var uiImage by remember { mutableStateOf<File?>(user.imageId) }
  var email by remember { mutableStateOf(user.email ?: "Please enter an email address") }
  val defaultEmail = "Please enter an email address"
  var phoneNumber by remember { mutableStateOf(user.phoneNumber ?: "") }
  var telegram by remember { mutableStateOf(user.telegram ?: "") }
  var favorite: SnapshotStateList<Boolean> = remember {
    user.favorite?.toMutableStateList() ?: mutableStateListOf(false, false, false)
  }

  // The field with the actual location
  val loc = remember { mutableStateOf<Location?>(null) }

  // Set local state variables to user's current values
  fun resetTempValues() {
    tempUsername = user.name
    tempAddress = user.address
    email = user.email ?: "Please enter an email address"
    phoneNumber = user.phoneNumber ?: ""
    telegram = user.telegram ?: ""
    // favorite = user.favorite?.toMutableStateList()?: mutableStateListOf(false, false, false)
  }

  // Menu for contact info : open or not
  var contact by remember { mutableStateOf(true) }

  // Set temporary values to real values when the screen is opened
  LaunchedEffect(key1 = user.id) { resetTempValues() }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("editAccount"),
      topBar = {
        TopAppBar(
            modifier = Modifier.fillMaxWidth().testTag("topBar"),
            title = { Text("My Account", modifier = Modifier.fillMaxWidth().testTag("title")) },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("backButton"),
                  onClick = {
                    resetTempValues() // Reset temp values to real values when back button is
                    // pressed
                    navigationActions.goBack()
                  }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.width(48.dp))
                  }
            })
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.ACCOUNT,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = modifier.testTag("bottomNavBar"))
      }) {
        if (user.id !=
            userViewModel.getLoggedUserId()) { // Check if user is editing their own account
          Text(text = "     Loading...", modifier = Modifier.padding(it).testTag("notYourAccount"))
        } else {
          Column(
              modifier =
                  Modifier.fillMaxHeight()
                      .padding(it)
                      .verticalScroll(rememberScrollState())
                      .testTag("mainContent")) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        modifier
                            .width(150.dp)
                            .height(150.dp)
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .testTag("image")) {
                      MainImagePicker(listOf(user.imageId.toUri())) { uri ->
                        if (uri.path != null &&
                            uri.path!!.isNotEmpty() &&
                            uri.path!! != user.imageId.path) {
                          uiImage = null
                          userViewModel.uploadImage(uri, imageName = "users/${user.id}") {
                            userViewModel.updateImage("users/${user.id}") { file -> uiImage = file }
                          }
                        }
                      }
                    }
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    modifier =
                        Modifier.fillMaxWidth().padding(16.dp, 0.dp).testTag("usernameField"),
                    value = tempUsername,
                    onValueChange = { tempUsername = it },
                    label = { Text("username") },
                    colors = OutlinedTextFieldDefaults.colors())

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                  LocationPicker(
                      location = tempAddress,
                      loc = loc.value,
                      onTextChanged = { tempAddress = it },
                      onLocationLookup = { locationViewModel.getLocation(it, loc) })
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier =
                        Modifier.padding(16.dp, 0.dp)
                            .clickable { contact = !contact }
                            .testTag("contactInfo"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.Left) {
                      if (contact) {
                        Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = null)
                      } else {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null)
                      }
                      Text("Contact information ")
                    }

                if (contact) {
                  Spacer(modifier = Modifier.height(8.dp))

                  TextField(
                      modifier = Modifier.fillMaxWidth().padding(16.dp, 0.dp).testTag("email"),
                      value = email,
                      onValueChange = { value ->
                        email =
                            if (value.contains(defaultEmail.dropLast(1))) {
                              ""
                            } else {
                              value
                            }
                        if (email == "") favorite[0] = false
                      },
                      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                      singleLine = true,
                      label = { Text("Email") },
                      colors = OutlinedTextFieldDefaults.colors())

                  Spacer(modifier = Modifier.height(10.dp))
                  TextField(
                      modifier =
                          Modifier.fillMaxWidth().padding(16.dp, 0.dp).testTag("phoneNumber"),
                      value = phoneNumber,
                      onValueChange = {
                        phoneNumber = it
                        if (phoneNumber == "") favorite[1] = false
                      },
                      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                      singleLine = true,
                      label = { Text("Phone Number") },
                      colors = OutlinedTextFieldDefaults.colors())
                  Spacer(modifier = Modifier.height(10.dp))
                  TextField(
                      modifier = Modifier.fillMaxWidth().padding(16.dp, 0.dp).testTag("telegram"),
                      value = telegram,
                      onValueChange = {
                        telegram = it
                        if (telegram == "") favorite[2] = false
                      },
                      singleLine = true,
                      label = { Text("Telegram") },
                      colors = OutlinedTextFieldDefaults.colors())

                  Spacer(modifier = Modifier.height(16.dp))
                  Text(
                      text = " Favorite contact methods",
                      fontSize = 15.sp,
                      modifier = Modifier.padding(16.dp, 0.dp))
                  Row(
                      modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                      horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                      verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            horizontalArrangement = Arrangement.Absolute.Left,
                            verticalAlignment = Alignment.CenterVertically) {
                              Checkbox(
                                  checked = favorite[0],
                                  onCheckedChange = { favorite[0] = !favorite[0] },
                                  enabled = email.contains("@") && email.contains("."),
                                  modifier = Modifier.testTag("emailCheckbox"))
                              Text("Email", fontSize = 11.sp)
                            }
                        Spacer(modifier = Modifier.width(2.dp))
                        Row(
                            horizontalArrangement = Arrangement.Absolute.Left,
                            verticalAlignment = Alignment.CenterVertically) {
                              Checkbox(
                                  checked = favorite[1],
                                  onCheckedChange = { favorite[1] = !favorite[1] },
                                  enabled = phoneNumber.isNotEmpty(),
                                  modifier = Modifier.testTag("phoneNumberCheckbox"))
                              Text("Phone number", fontSize = 11.sp)
                            }

                        Spacer(modifier = Modifier.width(2.dp))
                        Row(
                            horizontalArrangement = Arrangement.Absolute.Left,
                            verticalAlignment = Alignment.CenterVertically) {
                              Checkbox(
                                  checked = favorite[2],
                                  onCheckedChange = { favorite[2] = !favorite[2] },
                                  enabled = telegram.isNotEmpty(),
                                  modifier = Modifier.testTag("telegramCheckbox"))
                              Text("Telegram", fontSize = 11.sp)
                            }
                      }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier =
                        Modifier.fillMaxWidth().padding(16.dp, 0.dp).testTag("actionButtons"),
                    horizontalArrangement = Arrangement.Absolute.Center) {
                      Button(
                          onClick = {
                            userViewModel.updateUser(
                                user.copy(
                                    name = tempUsername,
                                    address = loc.value?.locationName ?: "Unknown Address",
                                    email = email,
                                    phoneNumber = phoneNumber,
                                    telegram = telegram,
                                    favorite = favorite))
                            navigationActions.goBack()
                          },
                          enabled =
                              favorite.contains(true) &&
                                  email != defaultEmail &&
                                  (email == "" || (email.contains("@") && email.contains("."))),
                          modifier = Modifier.weight(1f).testTag("saveButton"),
                      ) {
                        Text("Save changes")
                      }
                    }
                Spacer(modifier = Modifier.height(6.dp))
              }
        }
      }
}
