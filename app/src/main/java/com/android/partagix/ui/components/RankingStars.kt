package com.android.partagix.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import kotlin.math.round

/**
 * This function takes a rank as a string and returns a string with the corresponding amount of
 * stars
 *
 * @param rank the rank of the user
 * @return a string with the corresponding amount of stars
 */
@Composable
fun RankingStars(rank: String) {
  val modifier = Modifier.testTag("rankingStars")

  var isNan = false
  var rating = 0f
  /*  try {
    rating = round(rank.toFloat() * 200) / 100
  } catch (e: NumberFormatException) {
    isNan = true
  }*/

  if (!isNan && rank != "0.0") {
    val roundedRating = round(rating).toInt()
    when (roundedRating) {
      0 -> { // 0 star
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(
            Icons.Default.StarOutline,
            contentDescription = "Empty rank",
            modifier = Modifier.testTag("stars_0"))
      }
      1 -> { // 0.5 star
        Icon(Icons.AutoMirrored.Default.StarHalf, contentDescription = "Half rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(
            Icons.Default.StarOutline,
            contentDescription = "Empty rank",
            modifier = Modifier.testTag("stars_0_5"))
      }
      2 -> { // 1 star
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(
            Icons.Default.StarOutline,
            contentDescription = "Empty rank",
            modifier = Modifier.testTag("stars_1"))
      }
      3 -> { // 1.5 star
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.AutoMirrored.Default.StarHalf, contentDescription = "Half rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(
            Icons.Default.StarOutline,
            contentDescription = "Empty rank",
            modifier = Modifier.testTag("stars_1.5"))
      }
      4 -> { // 2 star
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(
            Icons.Default.StarOutline,
            contentDescription = "Empty rank",
            modifier = Modifier.testTag("stars_2"))
      }
      5 -> { // 2.5 star
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.AutoMirrored.Default.StarHalf, contentDescription = "Half rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(
            Icons.Default.StarOutline,
            contentDescription = "Empty rank",
            modifier = Modifier.testTag("stars_2.5"))
      }
      6 -> { // 3 star
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        Icon(
            Icons.Default.StarOutline,
            contentDescription = "Empty rank",
            modifier = Modifier.testTag("stars_3"))
      }
      7 -> { // 3.5 star
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.AutoMirrored.Default.StarHalf, contentDescription = "Half rank")
        Icon(
            Icons.Default.StarOutline,
            contentDescription = "Empty rank",
            modifier = Modifier.testTag("stars_3.5"))
      }
      8 -> { // 4 star
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(
            Icons.Default.StarOutline,
            contentDescription = "Empty rank",
            modifier = Modifier.testTag("stars_4"))
      }
      9 -> { // 4.5 star
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(
            Icons.AutoMirrored.Default.StarHalf,
            contentDescription = "Half rank",
            modifier = Modifier.testTag("stars_4.5"))
      }
      10 -> { // 5 star
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(Icons.Default.Star, contentDescription = "Full rank")
        Icon(
            Icons.Default.Star,
            contentDescription = "Full rank",
            modifier = Modifier.testTag("stars_5"))
      }
      else -> {
        Text("Not ranked yet", modifier = Modifier.testTag("stars_else"))
      }
    }
  } else {
    Text("Not ranked yet", modifier = Modifier.testTag("stars_empty"))
  }
}
