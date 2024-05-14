package com.android.partagix.model.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.android.partagix.BuildConfig
import com.android.partagix.MainActivity
import com.android.partagix.R
import com.android.partagix.model.Database
import com.android.partagix.ui.components.notificationAlert
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.remoteMessage
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

class FirebaseMessagingService(private val db: Database = Database()) : FirebaseMessagingService() {
  private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
  private var context = MainActivity.getContext()

  enum class Channels {
    INCOMING,
    OUTGOING,
    SOCIAL;

    fun id(): String {
      return this.ordinal.toString()
    }
  }

  init {
    Log.d(TAG, "context: $context")

    if (context != null) {
      /*requestPermissionLauncher =
      context!!.registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
      ) { isGranted: Boolean ->
        if (isGranted) {
          // FCM SDK (and your app) can post notifications.
        } else {
          // TODO: Inform user that that your app will not show notifications.
        }
      }*/

      askNotificationPermission()
      createChannels()
    }
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)
    // TODO(developer): Handle FCM messages here.
    // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
    Log.d(TAG, "From: ${remoteMessage.from}")

    if (context == null) {
      Log.e(TAG, "Context is not set, cannot show notification")
      return
    }

    // Check if message contains a data payload.
    if (remoteMessage.data.isNotEmpty()) {
      Log.d(TAG, "Message data payload: ${remoteMessage.data}")
      remoteMessage.notification?.let { Log.d(TAG, "Message Notification Body: ${it.body}") }
    }

    notificationAlert(
        context!!, remoteMessage.notification?.title ?: "", remoteMessage.notification?.body ?: "")
  }

  /**
   * Called if the FCM registration token is updated. This may occur if the security of the previous
   * token had been compromised. Note that this is called when the FCM registration token is
   * initially generated so this is where you would retrieve the token.
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

  fun getToken(onSuccess: (String) -> Unit = {}) {
    if (context == null) {
      Log.e(TAG, "Context is not set, cannot get token")
      return
    }

    FirebaseMessaging.getInstance()
        .token
        .addOnCompleteListener(
            OnCompleteListener { task ->
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

              onSuccess(token)
            })
  }

  fun checkToken(userId: String, onSuccess: (String) -> Unit = {}) {
    getToken { newToken ->
      db.getFCMToken(userId) { oldToken ->
        if (oldToken != null && oldToken != newToken) {
          db.updateFCMToken(userId, newToken)
        }

        onSuccess(newToken)
      }
    }
  }

  fun askNotificationPermission() {
    if (context == null) {
      Log.e(TAG, "Context is not set, cannot ask for permission")
      return
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.POST_NOTIFICATIONS) ==
          PackageManager.PERMISSION_GRANTED) {
        // FCM SDK (and your app) can post notifications.
        Log.d(TAG, "Permission already granted")
      } else if (context?.shouldShowRequestPermissionRationale(
          Manifest.permission.POST_NOTIFICATIONS) == true) {
        // TODO: display an educational UI explaining to the user the features that will be enabled
        //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
        //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the
        // permission.
        //       If the user selects "No thanks," allow the user to continue without notifications.
        Log.d(TAG, "Permission rationale")
      } else {
        // Directly ask for the permission
        Log.d(TAG, "Requesting permission")
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

  private fun createChannel(
      context: MainActivity,
      channelId: String,
      name: String,
      description: String
  ) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      val importance = NotificationManager.IMPORTANCE_DEFAULT

      val channel =
          NotificationChannel(channelId, name, importance).apply { this.description = description }

      val notificationManager = context.getSystemService(NotificationManager::class.java)
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun createChannels() {
    if (context == null) {
      Log.e(TAG, "Context is not set, cannot create channels")
      return
    }

    createChannel(
        context!!,
        Channels.INCOMING.id(),
        context!!.getString(R.string.incoming_name),
        context!!.getString(R.string.incoming_description))

    createChannel(
        context!!,
        Channels.OUTGOING.id(),
        context!!.getString(R.string.outgoing_name),
        context!!.getString(R.string.outgoing_description))

    createChannel(
        context!!,
        Channels.SOCIAL.id(),
        context!!.getString(R.string.social_name),
        context!!.getString(R.string.social_description))
  }

  private fun sendPostRequest(url: String, body: String) {
    if (context == null) {
      Log.e(TAG, "Context is not set, cannot send POST request")
      return
    }

    val client = OkHttpClient()

    val apiKey = BuildConfig.SERVER_API_KEY

    // Create request body
    val json = body
    val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

    // Create request
    val request =
        Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization", "key=$apiKey")
            .header("Content-Type", "application/json")
            .build()

    // Send request
    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to send POST request", e)
              }

              override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "POST request successful")
              }
            })
  }

  fun sendNotification() {
    if (context == null) {
      Log.e(TAG, "Context is not set, cannot send notification")
      return
    }

    val body = JSONObject()
    body.put("notification", JSONObject().apply { put("title", "Yooo") })
    body.put(
        "to",
        "enNSGi7WQdSlkiSPvFBY_W:APA91bFlKDlLkwD7A9hmRSeTSvkEjpjQv5r_DAQQbWf4MwlXzMvTbj2ZPKGL0pDbOhpvCbuFHo1RcT7TdgJAQOGHaDRJh7_W7L3h_Ke_UWXs_gnIRqdrzvQ7_vVCSCEbK2HQJWqAAUTY")

    sendPostRequest(FCM_SERVER_URL, body.toString())
  }

  private fun sendRegistrationToServer(token: String?) {
    // TODO: Implement this method to send token to your app server.
    Log.d(TAG, "sendRegistrationTokenToServer($token)")
  }

  companion object {
    private const val TAG = "FirebaseMessagingService"

    private const val SERVER_KEY_FILE_NAME = "secrets.properties"
    private const val SERVER_KEY = "SERVER_API_KEY"
    private const val FCM_SERVER_URL = "https://fcm.googleapis.com/fcm/send"
  }
}
