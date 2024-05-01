package com.android.partagix.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.ui.components.LabeledText
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LabeledTextTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun defaultLabeledTextIsDisplayed() = run {
    composeTestRule.setContent { LabeledText() }
    onComposeScreen<LabeledText>(composeTestRule) {
      labeledText { assertIsDisplayed() }
      mainColumn { assertIsDisplayed() }
      label {
        assertIsDisplayed()
        assertTextEquals("label")
      }
      text {
        assertIsDisplayed()
        assertTextEquals("text")
      }
    }
  }

  @Test
  fun customLabeledTextIsDisplayed() = run {
    composeTestRule.setContent { LabeledText(label = "testLabel", text = "testText") }
    onComposeScreen<LabeledText>(composeTestRule) {
      labeledText { assertIsDisplayed() }
      mainColumn { assertIsDisplayed() }
      label {
        assertIsDisplayed()
        assertTextEquals("testLabel")
      }
      text {
        assertIsDisplayed()
        assertTextEquals("testText")
      }
    }
  }
}
