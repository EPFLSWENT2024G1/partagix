package com.android.partagix.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import java.lang.reflect.Modifier
import kotlin.math.round

/**
 * This function takes a rank as a string and returns a string with the corresponding amount of stars
 * @param rank the rank of the user
 * @return a string with the corresponding amount of stars
 */

@Composable
fun rankStars(rank: String) {

  val starSize = 10.dp

  if (rank == "") {
//    ret = "No trust yet" // todo
  } else {
    val rating = round(rank.toFloat() * 200) / 100
    val roundedRating = round(rating).toInt()
    when (roundedRating) {
        0 -> { // 0 star
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        }
        1 -> { // 0.5 star
          Icon(Icons.AutoMirrored.Default.StarHalf, contentDescription = "Half rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        }
        2 -> { // 1 star
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        }
        3 -> { // 1.5 star
            Icon(Icons.Default.Star, contentDescription = "Full rank")
            Icon(Icons.AutoMirrored.Default.StarHalf, contentDescription = "Half rank")
            Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
            Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
            Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        }
        4 -> { // 2 star
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        }
        5 -> { // 2.5 star
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.AutoMirrored.Default.StarHalf, contentDescription = "Half rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        }
        6 -> { // 3 star
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        }
        7 -> { // 3.5 star
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.AutoMirrored.Default.StarHalf, contentDescription = "Half rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        }
        8 -> { // 4 star
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.StarOutline, contentDescription = "Empty rank")
        }
        9 -> { // 4.5 star
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.AutoMirrored.Default.StarHalf, contentDescription = "Half rank")
        }
        10 -> { // 5 star
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
          Icon(Icons.Default.Star, contentDescription = "Full rank")
        }
        else -> {
//          "..."
        }
      }
  }
}