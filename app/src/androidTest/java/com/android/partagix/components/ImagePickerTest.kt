package com.android.partagix.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.ui.components.ImageLayoutView
import com.android.partagix.ui.components.MainImagePicker
import com.android.partagix.ui.components.PhotoSelectorView
import io.mockk.every
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImagePickerTest {
  @get:Rule val composeTestRule = createComposeRule()
  val UI_TIMEOUT: Long = 100000

  val uri1 =
    Uri.parse(
      "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png")
  val uri2 =
    Uri.parse(
      "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_92x30dp.png")
  val uriList = listOf(uri1, uri2)

  @Composable
  fun ImagePickerTestingScreen(testedComponent: @Composable () -> Unit) {

    Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
      Spacer(modifier = Modifier.width(40.dp))
      Column(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
        Spacer(modifier = Modifier.height(40.dp))
        testedComponent()
        Spacer(modifier = Modifier.height(40.dp))
      }
      Spacer(modifier = Modifier.width(40.dp))
    }
  }

  @Test
  fun testPhotoSelectorView1() {
    composeTestRule.setContent { ImagePickerTestingScreen { PhotoSelectorView(1) } }
    composeTestRule.onNodeWithTag("PhotoSelectorClickable 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PhotoSelectorClickable 1").performClick()
  }

  @Test
  fun testPhotoSelectorView3() {
    composeTestRule.setContent { ImagePickerTestingScreen { PhotoSelectorView(3) } }
    composeTestRule.onNodeWithTag("PhotoSelectorClickable 3").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PhotoSelectorClickable 3").performClick()
  }

  @Test
  fun testMainImagePickerEmpty() {
    composeTestRule.setContent { ImagePickerTestingScreen { MainImagePicker() } }
    composeTestRule.onNodeWithTag("MainImagePickerClickable").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MainImagePickerClickable").performClick()
  }

  fun testMainImagePicker1image() {
    composeTestRule.setContent { ImagePickerTestingScreen { MainImagePicker(listOf(uri1)) } }
    composeTestRule.onNodeWithTag("MainImagePickerClickable").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MainImagePickerClickable").performClick()
  }

  fun testMainImagePicker2images() {
    composeTestRule.setContent { ImagePickerTestingScreen { MainImagePicker(uriList) } }
    composeTestRule.onNodeWithTag("MainImagePickerClickable").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MainImagePickerClickable").performClick()
  }

  @Test
  fun testImageLayoutView() {

    composeTestRule.setContent { ImagePickerTestingScreen { ImageLayoutView(uriList) } }

    composeTestRule.waitUntil(UI_TIMEOUT) {
      composeTestRule.onNodeWithTag("ImagePicked $uri1").isDisplayed()
    }
    composeTestRule.waitUntil(UI_TIMEOUT) {
      composeTestRule.onNodeWithTag("ImagePicked $uri2").isDisplayed()
    }
    composeTestRule.onNodeWithTag("ImagePicked $uri1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ImagePicked $uri2").assertIsDisplayed()
  }
}
