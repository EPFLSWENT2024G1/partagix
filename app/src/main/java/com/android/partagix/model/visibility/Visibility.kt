package com.android.partagix.model.visibility

enum class Visibility(val visibilityLabel: String) {
  PUBLIC("Everyone"), // ordinal = 0
  PRIVATE("No one") // ordinal = 1
  //  FRIENDS("Friends only"), // ordinal = 2
}

/**
 * Get the Visibility given the visibilityLabel string.
 *
 * @param visibilityLabel the label of the visibility string
 * @return the Visibility corresponding to the visibilityLabel, or Visibility.PUBLIC if not found
 */
fun getVisibility(visibilityLabel: String): Visibility {
  var ret = Visibility.PUBLIC
  for (visibility in Visibility.values()) {
    if (visibility.visibilityLabel == visibilityLabel) {
      ret = visibility
    }
  }
  return ret
}
