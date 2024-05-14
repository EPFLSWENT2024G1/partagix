package com.android.partagix.notification

import com.android.partagix.model.notification.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class FirebasePushNotificationTestRule(private val pushService: FirebaseMessagingService) : TestWatcher() {

  companion object {
    private const val FIREBASE_PUSH_TOKEN = "mocked_token_value"
  }

  override fun starting(description: Description) {
    super.starting(description)
    pushService.onCreate()
    pushService.onNewToken(FIREBASE_PUSH_TOKEN)
  }

  override fun finished(description: Description) {
    pushService.onDestroy()
    super.finished(description)
  }

  fun onNewToken(token: String) = pushService.onNewToken(token)

  fun sendPush(remoteMessage: RemoteMessage) = pushService.onMessageReceived(remoteMessage)
}