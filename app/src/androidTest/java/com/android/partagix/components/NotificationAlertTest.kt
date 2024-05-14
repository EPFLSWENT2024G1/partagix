package com.android.partagix.components

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.services.storage.file.PropertyFile.Column
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.model.notification.Notification
import com.android.partagix.MainActivity
import com.android.partagix.ui.components.notificationAlert
import io.mockk.*
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.util.Date

class NotificationAlertTest {
  private val context = mockk<MainActivity>(relaxed = true)
  private val navigationActions = mockk<NavigationActions>(relaxed = true)

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun displayNotificationWithTitleAndMessage() {
    val notification = Notification(
      "Test Title",
      "Test Message",
      Notification.Type.NEW_INCOMING_REQUEST,
      Date.from(Instant.now())
    )

    composeTestRule.setContent {
      Column {
        notificationAlert(context, notification, navigationActions)
      }

      onView(withText(notification.title)).check(matches(isDisplayed()))
      onView(withText(notification.message)).check(matches(isDisplayed()))
    }
  }
}
