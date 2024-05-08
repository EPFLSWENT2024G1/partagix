package com.android.partagix.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.ui.components.EvaluationPopUp
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EvaluationPopUpTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun contentIsDisplayed() {
    composeTestRule.setContent { EvaluationPopUp() }
    onComposeScreen<EvaluationPopUp>(composeTestRule) {
      rateText { assertIsDisplayed() }
      rateStars { assertIsDisplayed() }
      validateButton { assertIsDisplayed() }
      commentText { assertIsDisplayed() }
      commentField { assertIsDisplayed() }
      commentButton { assertIsDisplayed() }
    }
  }

  /** Rate and comment, and check the pop up disappear afterwards */
  @Test
  fun rateAndCommentWorks() {
    composeTestRule.setContent { EvaluationPopUp() }
    onComposeScreen<EvaluationPopUp>(composeTestRule) {
      commentButton { assertIsNotEnabled() }
      commentField { performTextReplacement("test") }
      commentButton { assertIsEnabled() }
      commentButton { performClick() }
      commentField { assertTextEquals("test") }

      validateButton { assertIsNotEnabled() }
      onNode { hasTestTag("star2") }.performClick()
      validateButton { assertIsEnabled() }
      validateButton { performClick() }
    }
    composeTestRule.onNodeWithTag("evaluationPopUp").assertDoesNotExist()
  }
}
