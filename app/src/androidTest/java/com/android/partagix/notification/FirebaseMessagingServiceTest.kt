package com.android.partagix.notification

import org.junit.Rule
import org.junit.Test

class FirebaseMessagingServiceTest {
  @get:Rule
  val fcmTestRule = FirebasePushNotificationTestRule(FirebaseMessagingService())

  @Test
  fun testNotificationEffect() {
    // Simulate a push message
    fcmTestRule.sendPush(mockedRemoteMessage)
    // Execute Espresso tests
  }
}
