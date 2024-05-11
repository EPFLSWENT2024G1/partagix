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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.getVisibility
import com.android.partagix.ui.components.CategoryItems
import com.android.partagix.ui.components.DropDown
import com.android.partagix.ui.components.MainImagePicker
import com.android.partagix.ui.components.VisibilityItems
import com.android.partagix.ui.navigation.NavigationActions
import getImageFromFirebaseStorage
import java.io.File
import uploadImageToFirebaseStorage

/**
 * Screen to create a new item in user's inventory.
 *
 * @param itemViewModel an ItemViewModel which handles functionality.
 * @param navigationActions a NavigationActions instance to navigate between screens.
 * @param modifier Modifier to apply to this layout.
 * @param mode is in create by default, to go on edit use the string "edit".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryCreateOrEditItem(
    itemViewModel: ItemViewModel,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier,
    mode: String
) {

  val uiState by itemViewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
      modifier = modifier.testTag("inventoryCreateItem").fillMaxWidth(),
      topBar = {
        TopAppBar(
            title = {
              Text(
                  modifier = modifier.testTag("title"),
                  text =
                      if (mode == "edit") {
                        "Edit item"
                      } else {
                        "Create a new item"
                      })
            },
            modifier = modifier.testTag("topBar").fillMaxWidth(),
            navigationIcon = {
              IconButton(
                  modifier = modifier.testTag("navigationIcon"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null)
                  }
            })
      },
  ) {
    val uis = itemViewModel.uiState.collectAsState()
    val i = uis.value.item

    var uiCategory by remember { mutableStateOf(i.category) }
    var uiName by remember { mutableStateOf(i.name) }
    var uiDescription by remember { mutableStateOf(i.description) }
    var uiVisibility by remember { mutableStateOf(i.visibility) }
    var uiQuantity by remember { mutableStateOf(i.quantity) }
    var uiLocation by remember { mutableStateOf(Location(i.location)) }
    var uiImage by remember { mutableStateOf(i.imageId) }

    Column(
        modifier = modifier.padding(it).fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {
          Box(modifier = modifier.fillMaxWidth().height(140.dp).padding(8.dp)) {
            Row(modifier = modifier.fillMaxWidth()) {
              Box(
                  contentAlignment = Alignment.Center,
                  modifier =
                      modifier.fillMaxHeight().fillMaxWidth(.4f).testTag("imageBox ${i.id}")) {
                    MainImagePicker(listOf(i.imageId.toUri())) { uri ->
                      // TODO :  Save the image to a local file to its displayed correctly while
                      // waiting for the upload
                      /*
                      val localFilePath = kotlin.io.path.createTempFile("temp-${i.id}", ".tmp").toFile()
                      Missing : save the image to the local file (need a ContentResolver ?)
                      uiImage = localFilePath
                       */
                      // Before this is done, display an empty image while waiting for the upload
                      uiImage = File.createTempFile("default_image", null)
                      // in the meantime do nothing and the image will be loaded from the database
                      // later
                      uploadImageToFirebaseStorage(uri, imageName = i.id) {
                        println("------ uploading image.....")
                        getImageFromFirebaseStorage(i.id) { file ->
                          println("------ updating uiImage")
                          uiImage = file
                        }
                      }
                    }
                  }

              Spacer(modifier = modifier.width(8.dp))

              Column {
                OutlinedTextField(
                    value = uiName,
                    onValueChange = { input ->
                      // Filter out newline characters from the input string
                      val filteredInput = input.replace("\n", "")
                      uiName = filteredInput
                    },
                    label = { Text("Object name") },
                    modifier = modifier.testTag("name").fillMaxWidth(),
                    maxLines = 1, // Ensure only one line is displayed
                    readOnly = false)

                OutlinedTextField(
                    value = uiState.user.name,
                    onValueChange = {},
                    label = { Text("Author") },
                    modifier = modifier.testTag("idUser").fillMaxWidth(),
                    readOnly = true)
              }
            }
          }
          Column(modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            OutlinedTextField(
                value = uiDescription,
                onValueChange = { it -> uiDescription = it },
                label = { Text("Description") },
                modifier = modifier.testTag("description").fillMaxWidth(),
                minLines = 5,
                readOnly = false)

            Spacer(modifier = modifier.height(8.dp))

            Row(modifier = modifier.fillMaxWidth()) {
              Box(modifier = modifier.testTag("category").fillMaxWidth(.5f).padding(end = 8.dp)) {
                val displayedCategory = if (uiCategory.name == "") "Category" else uiCategory.name
                val c = DropDown(displayedCategory, CategoryItems)

                uiCategory = Category(uiCategory.id, c)
              }
              Box(modifier = modifier.testTag("visibility").fillMaxWidth()) {
                uiVisibility = getVisibility(DropDown("Visibility", VisibilityItems))
              }
            }

            Spacer(modifier = modifier.height(8.dp))

            OutlinedTextField(
                value = if (uiQuantity == 0L) "" else uiQuantity.toString(),
                onValueChange = { str ->
                  val longValue: Long? = str.toLongOrNull()
                  uiQuantity = longValue ?: 0L
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Quantity") },
                modifier = modifier.testTag("quantity").fillMaxWidth(),
                readOnly = false)

            Spacer(modifier = modifier.height(8.dp))

            OutlinedTextField(
                value = uiLocation.toString(), // TODO: get default or user's location
                onValueChange = { it -> uiLocation = Location(it) },
                label = { Text("Where") },
                modifier = modifier.fillMaxWidth(),
                readOnly = false)

            Button(
                onClick = {
                  var id = ""
                  if (mode == "edit") {
                    id = i.id
                  }
                  itemViewModel.save(
                      Item(
                          id,
                          uiCategory,
                          uiName,
                          uiDescription,
                          uiVisibility,
                          uiQuantity,
                          uiLocation,
                          i.idUser,
                          uiImage))
                  navigationActions.goBack()
                },
                content = {
                  if (mode == "edit") {
                    Text("Save")
                  } else {
                    Text("Create")
                  }
                },
                modifier = modifier.fillMaxWidth().testTag("button").padding(top = 8.dp))
          }
        }
  }
}
