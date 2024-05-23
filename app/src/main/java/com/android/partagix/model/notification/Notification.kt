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
    LOAN_REJECTED,
    NEW_INCOMING_REQUEST,
    USER_REVIEW,
    DEFAULT;

    fun channelId(): String {
      return when (this) {
        NEW_INCOMING_REQUEST -> Channels.INCOMING.id()
        LOAN_ACCEPTED -> Channels.OUTGOING.id()
        LOAN_REJECTED -> Channels.OUTGOING.id()
        USER_REVIEW -> Channels.SOCIAL.id()
        DEFAULT -> Channels.SOCIAL.id()
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
