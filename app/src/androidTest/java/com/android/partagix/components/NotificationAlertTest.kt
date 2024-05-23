package com.android.partagix.components

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.partagix.MainActivity
import com.android.partagix.model.notification.Notification
import com.android.partagix.ui.components.notificationAlert
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import io.mockk.mockk
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationAlertTest {

  @get:Rule val activityRule = ActivityScenarioRule(MainActivity::class.java)

  @get:Rule
  val grantPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS)

  @Test
  fun notificationAlert_displaysDialog() {
    val notification =
        Notification(
            title = "Test Title", message = "Test Message", type = Notification.Type.DEFAULT)

    val navigationActions = mockk<NavigationActions>()

    val latch = CountDownLatch(1)

    activityRule.scenario.onActivity { activity ->
      activity.myInitializationFunction(Route.ACCOUNT)
      notificationAlert(activity, notification, navigationActions)
      activity.runOnUiThread { latch.countDown() }
    }

    latch.await(5, TimeUnit.SECONDS)
    onView(withText("Test Title")).check(matches(isDisplayed()))
    onView(withText("Test Message")).check(matches(isDisplayed()))
  }
}
