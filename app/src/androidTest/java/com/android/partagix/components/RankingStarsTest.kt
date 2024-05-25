package com.android.partagix.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.ui.components.RankingStars
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RankingStarsTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun everyValuesRankingStarsIsDisplayed() = run {
    composeTestRule.setContent {
      //      RankingStars("")
      Spacer(modifier = Modifier.height(10.dp))
      //      RankingStars("0.0")
      Spacer(modifier = Modifier.height(10.dp))
      //      RankingStars("else")
      Spacer(modifier = Modifier.height(10.dp))
      RankingStars("0.1")
      Spacer(modifier = Modifier.height(10.dp))

      RankingStars("0.6")
      Spacer(modifier = Modifier.height(10.dp))

      RankingStars("1.1")
      Spacer(modifier = Modifier.height(10.dp))

      RankingStars("1.4")
      RankingStars("1.8")
      RankingStars("2.3")
      RankingStars("3.0")
      RankingStars("3.5")
      RankingStars("3.9999999")
      RankingStars("4.51111111111111")
      RankingStars("4.78")
    }
    //    onComposeScreen<RankingStars>(composeTestRule) { stars_empty { assertIsDisplayed() } }

    //    composeTestRule.setContent { RankingStars("0.0") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_empty { assertIsDisplayed() } }

    //    composeTestRule.setContent { RankingStars("else") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_else { assertIsDisplayed() } }

    //    composeTestRule.setContent { RankingStars("0.1") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_0 { assertIsDisplayed() } }
    //    composeTestRule.setContent { RankingStars("0.6") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_0_5 { assertIsDisplayed() } }
    //    composeTestRule.setContent { RankingStars("1.1") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_1 { assertIsDisplayed() } }
    //    composeTestRule.setContent { RankingStars("1.4") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_1_5 { assertIsDisplayed() } }
    //    composeTestRule.setContent { RankingStars("1.8") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_2 { assertIsDisplayed() } }
    //    composeTestRule.setContent { RankingStars("2.3") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_2_5 { assertIsDisplayed() } }
    //    composeTestRule.setContent { RankingStars("3.0") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_3 { assertIsDisplayed() } }
    //    composeTestRule.setContent { RankingStars("3.5") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_3_5 { assertIsDisplayed() } }
    //    composeTestRule.setContent { RankingStars("3.9999999") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_4 { assertIsDisplayed() } }
    //    composeTestRule.setContent { RankingStars("4.51111111111111") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_4_5 { assertIsDisplayed() } }
    //    composeTestRule.setContent { RankingStars("4.78") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_5 { assertIsDisplayed() } }
  }
}
