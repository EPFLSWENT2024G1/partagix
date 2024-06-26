package com.android.partagix.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// Functions commented as "imported" are from this webpage :
// https://medium.com/@jpmtech/jetpack-compose-display-a-photo-picker-6bcb9b357a3a

fun launchPhotoPicker(photoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>) {
  photoPickerLauncher.launch(
      PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
}

/**
 * PhotoSelectorView composable to display a photo picker
 *
 * @param maxSelectionCount the maximum number of photos that can be selected
 */
// imported
@Composable
fun PhotoSelectorView(maxSelectionCount: Int = 1) {
  var selectedImages by remember { mutableStateOf<List<Uri?>>(emptyList()) }

  val buttonText =
      if (maxSelectionCount > 1) {
        "Select up to $maxSelectionCount photos"
      } else {
        "Select a photo"
      }

  val singlePhotoPickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri -> selectedImages = listOf(uri) })

  val multiplePhotoPickerLauncher =
      rememberLauncherForActivityResult(
          contract =
              ActivityResultContracts.PickMultipleVisualMedia(
                  maxItems =
                      if (maxSelectionCount > 1) {
                        maxSelectionCount
                      } else {
                        2
                      }),
          onResult = { uris -> selectedImages = uris })

  Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
    Button(
        onClick = {
          launchPhotoPicker(
              if (maxSelectionCount == 1) singlePhotoPickerLauncher
              else multiplePhotoPickerLauncher)
        },
        modifier = Modifier.testTag("PhotoSelectorClickable $maxSelectionCount")) {
          Text(buttonText)
        }

    ImageLayoutView(selectedImages = selectedImages)
  }
}

// imported
@Composable
fun ImageLayoutView(selectedImages: List<Uri?>) {
  LazyRow {
    items(selectedImages) { uri ->
      AsyncImage(
          model = uri,
          contentDescription = null,
          modifier = Modifier.fillMaxWidth().testTag("ImagePicked $uri"),
          contentScale = ContentScale.Fit)
    }
  }
}

/**
 * MainImagePicker composable to display the image picker
 *
 * @return a clickable box, with default background color, that opens an imagePicker on click, the
 *   picked image is displayed in the box
 */
@Preview
@Composable
fun MainImagePicker(defaultImages: List<Uri?> = emptyList(), onSelected: (Uri) -> Unit = {}) {
  var selectedImages by remember { mutableStateOf<List<Uri?>>(defaultImages) }

  val singlePhotoPickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri ->
            selectedImages = if (uri != null) listOf(uri) else defaultImages
            onSelected(selectedImages.first()!!)
          })

  val multiplePhotoPickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 2),
          onResult = { uris -> selectedImages = uris })

  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier.fillMaxSize()
                    .clickable {
                      launchPhotoPicker(
                          if (selectedImages.size == 1) singlePhotoPickerLauncher
                          else multiplePhotoPickerLauncher)
                    }
                    .testTag("MainImagePickerClickable")) {
              Surface(
                  modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    LazyColumn(
                        verticalArrangement = Arrangement.Center,
                        modifier =
                            Modifier.fillMaxSize()
                                .background(color = MaterialTheme.colorScheme.background)) {
                          items(selectedImages) { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                alignment = Alignment.Center,
                                modifier =
                                    Modifier.border(1.dp, MaterialTheme.colorScheme.outline)
                                        .testTag("ImagePicked"),
                                contentScale = ContentScale.FillWidth)
                          }
                        }
                  }
            }
      }
}
