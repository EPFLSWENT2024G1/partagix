package com.android.partagix.components.locationPicker

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.location.Location
import com.android.partagix.ui.components.locationPicker.LocationPicker
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationPickerTests {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun LocationPickerIsDisplayedAndWorks() {
    var loc: Location? = Location(0.0, 0.0, "Test Location")
    var location = mutableStateOf("Test Location")
    composeTestRule.setContent {
      LocationPicker(
          location = location.toString(),
          loc = loc,
          onTextChanged = { location.value = it },
          onLocationLookup = {})
    }
    composeTestRule.onNodeWithTag("addressField").assertIsDisplayed()
    composeTestRule.onNodeWithText("Test Location").assertIsDisplayed()
  }
}
