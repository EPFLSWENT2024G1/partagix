package com.android.partagix.model.notification

import java.util.Date

data class Notification(
  val title: String,
  val message: String,
  val type: Type,
  val creationDate: Date,
  val navigationUrl: String? = null
) {
  enum class Type {
    NEW_INCOMING_REQUEST;

    fun channelId(): String {
      return when (this) {
        NEW_INCOMING_REQUEST -> Channels.INCOMING.id()
      }
    }
  }

  enum class Channels {
    INCOMING,
    OUTGOING,
    SOCIAL;

    fun id(): String {
      return this.ordinal.toString()
    }
  }
}
