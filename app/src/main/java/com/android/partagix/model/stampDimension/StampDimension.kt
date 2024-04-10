package com.android.partagix.model.stampDimension

enum class StampDimension(val detailedDimension: String) {
  SMALL("Small, XXcm x XXcm (XX per A4 page)"), // ordinal = 0
  MEDIUM("Medium, XXcm x XXcm (XX per A4 page)"), // ordinal = 1
  BIG("Big, XXcm x XXcm (XX per A4 page)"), // ordinal = 2
  FULL_PAGE("Entire page, XXcm x XXcm (1 per A4 page)") // ordinal = 3
}
