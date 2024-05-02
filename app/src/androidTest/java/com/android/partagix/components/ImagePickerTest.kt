package com.android.partagix.components

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
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.ui.components.MainImagePicker
import com.android.partagix.ui.components.PhotoSelectorView
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImagePickerTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Composable
  fun ImagePickerTestingScreen(testedComponent: @Composable () -> Unit) {

    Row(modifier = Modifier.fillMaxWidth().height(200.dp)) {
      Spacer(modifier = Modifier.width(16.dp))
      Column(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))
        testedComponent()
        Spacer(modifier = Modifier.height(16.dp))
      }
      Spacer(modifier = Modifier.width(16.dp))
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
  fun testMainImagePicker() {
    composeTestRule.setContent { ImagePickerTestingScreen { MainImagePicker() } }
    composeTestRule.onNodeWithTag("MainImagePickerClickable").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MainImagePickerClickable").performClick()
  }
}
