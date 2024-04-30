package com.android.partagix.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.ui.components.Filter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilterTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun filterIsDisplayed() {
    composeTestRule.setContent {
      Filter(
        title = "Title",
        selectedValue = {},
        disabled = false,
        unit = "",
        minUnit = "",
        maxUnit = "",
        minValue = 0f,
        maxValue = 50f,
        sliderTextValue = null
      )
    }

    composeTestRule.onNodeWithText("Title").assertIsDisplayed()
  }
}