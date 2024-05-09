package com.android.partagix.model.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.android.partagix.MainActivity
import com.android.partagix.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseMessagingService : FirebaseMessagingService() {
  // To be initialized using setContext()
  private var context: MainActivity? = null

  private val requestPermissionLauncher = context.registerForActivityResult(
    ActivityResultContracts.RequestPermission(),
  ) { isGranted: Boolean ->
    if (isGranted) {
      // FCM SDK (and your app) can post notifications.
    } else {
      // TODO: Inform user that that your app will not show notifications.
    }
  }

  private fun askNotificationPermission() {
    // This is only necessary for API level >= 33 (TIRAMISU)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED
      ) {
        // FCM SDK (and your app) can post notifications.
      } else if (context?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == true) {
        // TODO: display an educational UI explaining to the user the features that will be enabled
        //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
        //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
        //       If the user selects "No thanks," allow the user to continue without notifications.
      } else {
        // Directly ask for the permission
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    // TODO(developer): Handle FCM messages here.
    // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
    Log.d(TAG, "From: ${remoteMessage.from}")

    // Check if message contains a data payload.
    if (remoteMessage.data.isNotEmpty()) {
      Log.d(TAG, "Message data payload: ${remoteMessage.data}")
    }

    // Check if message contains a notification payload.
    remoteMessage.notification?.let {
      Log.d(TAG, "Message Notification Body: ${it.body}")
    }

    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.
  }

  /**
   * Called if the FCM registration token is updated. This may occur if the security of
   * the previous token had been compromised. Note that this is called when the
   * FCM registration token is initially generated so this is where you would retrieve the token.
   */
  override fun onNewToken(token: String) {
    Log.d(TAG, "Refreshed token: $token")

    // If you want to send messages to this application instance or
    // manage this apps subscriptions on the server side, send the
    // FCM registration token to your app server.
    sendRegistrationToServer(token)
  }

  fun setContext(context: MainActivity) {
    this.context = context
  }

  fun getToken() {
    if (context == null) {
      Log.e(TAG, "Context is not set, cannot get token")
      return
    }

    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
      if (!task.isSuccessful) {
        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
        return@OnCompleteListener
      }

      // Get new FCM registration token
      val token = task.result

      // Log and toast
      Log.d(TAG, token)
      val msg = "Token: $token"
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    })
  }

  private fun sendRegistrationToServer(token: String?) {
    // TODO: Implement this method to send token to your app server.
    Log.d(TAG, "sendRegistrationTokenToServer($token)")
  }

  companion object {
    private const val TAG = "FirebaseMessagingService"
  }
}