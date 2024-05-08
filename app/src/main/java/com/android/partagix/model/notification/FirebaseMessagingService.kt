package com.android.partagix.model.notification

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.partagix.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseMessagingService : FirebaseMessagingService() {
  private var context: Context? = null

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

  fun setContext(context: Context) {
    this.context = context
  }

  fun getToken() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
      if (!task.isSuccessful) {
        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
        return@OnCompleteListener
      }

      // Get new FCM registration token
      val token = task.result

      // Log and toast
      Log.d(TAG, token)
      //val msg = getString(R.string.msg_token_fmt, token)
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