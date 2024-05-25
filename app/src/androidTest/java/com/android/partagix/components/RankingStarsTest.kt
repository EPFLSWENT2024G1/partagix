package com.android.partagix.components

import androidx.compose.ui.test.junit4.createComposeRule
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
  fun emptyRatingWorks() = run {
    composeTestRule.setContent { RankingStars("") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_empty { assertIsDisplayed() } }
  }

  @Test
  fun randomTextWorks() = run {
    composeTestRule.setContent { RankingStars("else") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_empty { assertIsDisplayed() } }
  }

  @Test
  fun illegalRatingWorks() = run {
    composeTestRule.setContent { RankingStars("10") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_wrong_rating { assertIsDisplayed() } }
  }

  @Test
  fun rating0Works() = run {
    composeTestRule.setContent { RankingStars("0.1") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_0 { assertIsDisplayed() } }
  }

  @Test
  fun rating0_5Works() = run {
    composeTestRule.setContent { RankingStars("0.7") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_0_5 { assertIsDisplayed() } }
  }

  @Test
  fun rating1Works() = run {
    composeTestRule.setContent { RankingStars("1.01") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_1 { assertIsDisplayed() } }
  }

  @Test
  fun rating1_5Works() = run {
    composeTestRule.setContent { RankingStars("1.5") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_1_5 { assertIsDisplayed() } }
  }

  @Test
  fun rating2Works() = run {
    composeTestRule.setContent { RankingStars("2.25") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_2 { assertIsDisplayed() } }
  }

  @Test
  fun rating2_5Works() = run {
    composeTestRule.setContent { RankingStars("2.7") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_2_5 { assertIsDisplayed() } }
  }

  @Test
  fun rating3Works() = run {
    composeTestRule.setContent { RankingStars("3.0") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_3 { assertIsDisplayed() } }
  }

  @Test
  fun rating3_5Works() = run {
    composeTestRule.setContent { RankingStars("3.6") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_3_5 { assertIsDisplayed() } }
  }

  @Test
  fun rating4Works() = run {
    composeTestRule.setContent { RankingStars("4.0") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_4 { assertIsDisplayed() } }
  }

  @Test
  fun rating4_5Works() = run {
    composeTestRule.setContent { RankingStars("4.5") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_4_5 { assertIsDisplayed() } }
  }

  @Test
  fun rating5Works() = run {
    composeTestRule.setContent { RankingStars("5.0") }
    onComposeScreen<RankingStars>(composeTestRule) { stars_5 { assertIsDisplayed() } }
  }
}
