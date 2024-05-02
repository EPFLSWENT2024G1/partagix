package com.android.partagix.model.stampDimension

enum class StampDimension(val detailedDimension: String) {
  SMALL("Small, 5cm x 6cm (18 per A4 page)"), // ordinal = 0
  MEDIUM("Medium, 8cm x 9cm (6 per A4 page)"), // ordinal = 1
  BIG("Big, 14cm x 16cm (2 per A4 page)"), // ordinal = 2
  FULL_PAGE("Entire page, 20cm x 24cm (1 per A4 page)") // ordinal = 3
}
