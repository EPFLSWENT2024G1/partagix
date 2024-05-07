package com.android.partagix.model.rating

data class Rating (
  val evaluatedId: String,
  val authorId: String,
  val rank: Long,
  val comment: String
)