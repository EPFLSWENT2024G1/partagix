package com.android.partagix.components

import android.app.AlertDialog
import android.content.res.Resources
import androidx.activity.ComponentActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.android.partagix.R
import com.android.partagix.model.notification.Notification
import com.android.partagix.ui.components.notificationAlert
import com.android.partagix.ui.navigation.NavigationActions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.mockk.*
import java.util.Date
import org.junit.After
import org.junit.Before
import org.junit.Test

class NotificationAlertTest {
  private lateinit var activityScenario: ActivityScenario<ComponentActivity>

  @Before
  fun setUp() {
    activityScenario = ActivityScenario.launch(ComponentActivity::class.java)
  }

  @After
  fun tearDown() {
    activityScenario.close()
  }

  @Test
  fun testNotificationAlert() {
    var context: ComponentActivity
    activityScenario.onActivity { activity ->
      context = spyk(activity)

      val resources = mockk<Resources>(relaxed = true)
      val theme = mockk<Resources.Theme>(relaxed = true)

      every { context.resources } returns resources
      every { resources.newTheme() } returns theme
      every { context.setTheme(R.style.Theme_Partagix) } just Runs

      val notification: Notification = mockk()
      every { notification.title } returns "title"
      every { notification.message } returns "message"
      every { notification.navigationUrl } returns "navigationUrl"
      every { notification.type } returns Notification.Type.NEW_INCOMING_REQUEST
      every { notification.creationDate } returns java.sql.Date.valueOf("2021-09-01")

      val navigationActions = mockk<NavigationActions>()

      // Mock Glide.with().asBitmap() to avoid actual image loading
      // mockkStatic("com.bumptech.glide.Glide")
      val alertDialog = mockk<androidx.appcompat.app.AlertDialog>()
      every { alertDialog.window } returns mockk(relaxed = true)

      val alertDialogBuilder = mockk<MaterialAlertDialogBuilder>()
      every { alertDialogBuilder.setTitle(any<CharSequence>()) } returns alertDialogBuilder
      every { alertDialogBuilder.setMessage(any<CharSequence>()) } returns alertDialogBuilder
      every { alertDialogBuilder.setNegativeButton(any<CharSequence>(), any()) } returns
          alertDialogBuilder
      every { alertDialogBuilder.setPositiveButton(any<CharSequence>(), any()) } returns
          alertDialogBuilder
      every { alertDialogBuilder.show() } returns alertDialog

      every { anyConstructed<MaterialAlertDialogBuilder>() } returns alertDialogBuilder

      notificationAlert(context, notification, navigationActions)

      // Verify AlertDialog is shown with correct title and message
      onView(withText(notification.title)).check(matches(isDisplayed()))
      onView(withText(notification.message)).check(matches(isDisplayed()))

      // Verify clicking on "View" button dismisses dialog
      onView(withText("View")).perform(click())
      verify { anyConstructed<AlertDialog>().dismiss() }

      // Verify navigation action is invoked when "View" button is clicked
      every { navigationActions.navigateTo(notification.navigationUrl!!) } just Runs
      notificationAlert(context, notification, navigationActions)
      onView(withText("View")).perform(click())
      verify { navigationActions.navigateTo(notification.navigationUrl!!) }
    }
  }
}
