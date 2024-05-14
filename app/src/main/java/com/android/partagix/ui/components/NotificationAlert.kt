package com.android.partagix.ui.components

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.ContextThemeWrapper
import android.view.Gravity
import com.android.partagix.MainActivity
import com.android.partagix.R
import com.android.partagix.model.notification.Notification
import com.android.partagix.ui.navigation.NavigationActions
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun notificationAlert(context: MainActivity, notification: Notification, navigationActions: NavigationActions) {
  Handler(Looper.getMainLooper()).post {
    val dialog =
        MaterialAlertDialogBuilder(ContextThemeWrapper(context, R.style.Theme_Partagix))
            .setTitle(notification.title)
            .setMessage(notification.message)
            .setNegativeButton("Ignore") {
               dialog, _ -> dialog.dismiss()
            }
          .setPositiveButton("View") {
            dialog, _ ->
              dialog.dismiss()
            if (notification.navigationUrl != null)
              navigationActions.navigateTo(notification.navigationUrl)
          }
            .show()

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
      })

    val window = dialog.window
    window?.setGravity(Gravity.TOP)
  }
}
