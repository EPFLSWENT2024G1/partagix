package com.android.partagix.components

import androidx.compose.ui.test.TouchInjectionScope
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.ui.components.Filter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilterTest {
  @get:Rule val composeTestRule = createComposeRule()

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
          sliderTextValue = null)
    }

    composeTestRule.onNodeWithText("Title").assertIsDisplayed()
  }

  @Test
  fun selectedValueWorks() {
    var selectedValue = 0f
    composeTestRule.setContent {
      Filter(
          title = "Title",
          selectedValue = { selectedValue = it },
      )
    }

    composeTestRule.onNodeWithText("Title").assertIsDisplayed()
    composeTestRule.onNodeWithText("Title").performClick()

    val slider = composeTestRule.onNodeWithTag("SliderFilter")
    slider.assertIsDisplayed()
    slider.performTouchInput(
        fun TouchInjectionScope.() {
          swipeRight()
        })

    assert(selectedValue > 0f)
  }

  @Test
  fun sliderTextValueIsDisplayed() {
    composeTestRule.setContent {
      Filter(
          title = "Title", selectedValue = {}, maxValue = 1f, sliderTextValue = { "Value is $it" })
    }

    composeTestRule.onNodeWithText("Title").assertIsDisplayed()
    composeTestRule.onNodeWithText("Title").performClick()

    val slider = composeTestRule.onNodeWithTag("SliderFilter")
    slider.assertIsDisplayed()
    slider.performTouchInput(
        fun TouchInjectionScope.() {
          swipeRight()
        })

    composeTestRule.onNodeWithText("Value is 1.0").assertIsDisplayed()
  }

  @Test
  fun disabledFilterIsNotClickable() {
    composeTestRule.setContent { Filter(title = "Title", selectedValue = {}, disabled = true) }

    composeTestRule.onNodeWithText("Title").assertIsDisplayed()
    composeTestRule.onNodeWithText("Title").performClick()

    val slider = composeTestRule.onNodeWithTag("SliderFilter")
    slider.assertIsNotDisplayed()
  }

  @Test
  fun filterWorksWithNonDefaultValue() {
    var selectedValue = 0f
    composeTestRule.setContent {
      Filter(
          title = "Title",
          selectedValue = { selectedValue = it },
          unit = "km",
          minUnit = "1 km",
          maxUnit = "10 km",
          minValue = 1f,
          maxValue = 10f,
          sliderTextValue = null)
    }

    composeTestRule.onNodeWithText("Title").assertIsDisplayed()
    composeTestRule.onNodeWithText("Title").performClick()

    composeTestRule.onNodeWithText("1 km").assertIsDisplayed()

    val slider = composeTestRule.onNodeWithTag("SliderFilter")
    slider.assertIsDisplayed()
    slider.performTouchInput(
        fun TouchInjectionScope.() {
          swipeRight()
        })

    assert(selectedValue == 10f)
  }
}
