package com.android.partagix.model.notification

import java.util.Date

data class Notification(
    val title: String,
    val message: String,
    val type: Type,
    val creationDate: Date = Date(System.currentTimeMillis()),
    val navigationUrl: String? = null
) {
  enum class Type {
    LOAN_ACCEPTED,
    NEW_INCOMING_REQUEST,
    DEFAULT;

    fun channelId(): String {
      return when (this) {
        NEW_INCOMING_REQUEST -> Channels.INCOMING.id()
        LOAN_ACCEPTED -> Channels.OUTGOING.id()
        else -> Channels.SOCIAL.id()
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
