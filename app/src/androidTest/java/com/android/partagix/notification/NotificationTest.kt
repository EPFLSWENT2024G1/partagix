package com.android.partagix.notification

import com.android.partagix.model.notification.Notification
import org.junit.Test

class NotificationTest {
  @Test
  fun channelIdWorks() {
    assert(
        Notification.Type.NEW_INCOMING_REQUEST.channelId() == Notification.Channels.INCOMING.id())
    assert(Notification.Type.LOAN_ACCEPTED.channelId() == Notification.Channels.OUTGOING.id())
    assert(Notification.Type.LOAN_REJECTED.channelId() == Notification.Channels.OUTGOING.id())
    assert(Notification.Type.USER_REVIEW.channelId() == Notification.Channels.SOCIAL.id())
    assert(Notification.Type.DEFAULT.channelId() == Notification.Channels.SOCIAL.id())
  }
}
