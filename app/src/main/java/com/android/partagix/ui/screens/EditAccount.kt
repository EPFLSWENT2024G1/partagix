package com.android.partagix.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.android.partagix.R
import com.android.partagix.model.UserViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.MainImagePicker
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import getImageFromFirebaseStorage
import uploadImageToFirebaseStorage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccount(
    modifier: Modifier = Modifier,
    navigationActions: NavigationActions,
    userViewModel: UserViewModel,
) {
  val uiState = userViewModel.uiState.collectAsState()
  val user = uiState.value.user

  // Local state variables to hold temporary values for editable fields
  var tempUsername by remember { mutableStateOf(user.name) }
  var tempAddress by remember { mutableStateOf(user.address) }
    var uiImage by remember { mutableStateOf<File?>(user.imageId) }
  // Set local state variables to user's current values
  fun resetTempValues() {
    tempUsername = user.name
    tempAddress = user.address
  }

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
          Text(
              text = "Loading...",
              modifier = Modifier.padding(it).testTag("notYourAccount"))
        } else {
          Column(
              modifier =
                  Modifier.fillMaxHeight()
                      .padding(it)
                      .verticalScroll(rememberScrollState())
                      .testTag("mainContent")) {
              Box(
                  contentAlignment = Alignment.Center,
                  modifier = modifier.width(150.dp).height(150.dp).padding(8.dp)
                      .align(Alignment.CenterHorizontally).testTag("image")) {
                  MainImagePicker(listOf(user.imageId.toUri())) { uri ->
                      // TODO :  Save the image to a local file to its displayed correctly while waiting for the upload
                      /*
                      val localFilePath = kotlin.io.path.createTempFile("temp-${i.id}", ".tmp").toFile()
                      Missing : save the image to the local file (need a ContentResolver ?)
                      uiImage = localFilePath
                       */
                      // Before this is done, display an empty image while waiting for the upload
                      uiImage = File.createTempFile("default_image", null)

                      // in the meantime do nothing and the image will be loaded from the database later
                      uploadImageToFirebaseStorage(uri, imageName = "users/${user.id}"){
                          getImageFromFirebaseStorage("users/${user.id}") { file -> uiImage = file }
                      }

                  }
              }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().testTag("username"),
                    horizontalArrangement = Arrangement.Absolute.SpaceAround) {
                      TextField(
                          modifier = Modifier.testTag("usernameField"),
                          value = tempUsername,
                          onValueChange = { tempUsername = it },
                          label = { Text("username") })
                    }
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = tempAddress,
                    onValueChange = { tempAddress = it },
                    label = { Text("Location", modifier = Modifier.testTag("addressText")) },
                    modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("addressField"),
                    leadingIcon = {
                      Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier)
                    })
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp, 0.dp).testTag("actionButtons"),
                    horizontalArrangement = Arrangement.Absolute.Center) {
                      Spacer(modifier = Modifier.width(8.dp))
                      Button(
                          onClick = {
                            userViewModel.updateUser(
                                user.copy(
                                    name = tempUsername,
                                    address =
                                        tempAddress)) // Update user with new values in the database
                            navigationActions.goBack()
                          },
                          modifier = Modifier.weight(1f).testTag("saveButton")) {
                            Text("Save changes")
                          }
                    }
              }
        }
      }
}
