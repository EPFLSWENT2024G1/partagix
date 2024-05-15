package com.android.partagix.ui.components

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import androidx.activity.ComponentActivity
import com.android.partagix.R
import com.android.partagix.model.notification.Notification
import com.android.partagix.ui.navigation.NavigationActions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TAG = "NotificationAlert"

fun notificationAlert(
    context: ComponentActivity,
    notification: Notification,
    navigationActions: NavigationActions
) {
  Handler(Looper.getMainLooper()).post {
    val dialog =
        MaterialAlertDialogBuilder(ContextThemeWrapper(context, R.style.Theme_Partagix))
            .setTitle(notification.title)
            .setMessage(notification.message)
            .setNegativeButton("Ignore") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("View") { dialog, _ ->
              dialog.dismiss()
              if (notification.navigationUrl != null) {
                Log.d(TAG, "Navigation to ${notification.navigationUrl}")
                navigationActions.navigateTo(notification.navigationUrl)
              }
            }
            .show()

    /* TODO: load image from URL, not working yet
    Glide.with(context)
      .asBitmap()
      .load(notification.imageUrl)
      .into(object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
          val drawable = BitmapDrawable(context.resources, resource)
          dialog.setIcon(drawable)
        }

        override fun onLoadCleared(placeholder: Drawable?) {
          // Handle cleanup if necessary
        }
      })*/

    val window = dialog.window
    window?.setGravity(Gravity.TOP)
  }
}
