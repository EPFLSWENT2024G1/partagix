package com.android.partagix.model.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.android.partagix.BuildConfig
import com.android.partagix.MainActivity
import com.android.partagix.R
import com.android.partagix.model.Database
import com.android.partagix.ui.App
import com.android.partagix.ui.components.notificationAlert
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.remoteMessage
import java.io.IOException
import java.sql.Date
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
  private var navigationActions = App.getNavigationActions()

  init {
    Log.d(TAG, "context: $context")
  }

  /**
   * Initializes the permissions (notifications) for the app.
   *
   * This function should be called during the MainActivity's onCreate() method. It asks the user
   * for permission to show notifications and creates the notification channels, as described in
   * `createChannels()`.
   */
  fun initPermissions() {
    if (context == null) {
      Log.e(TAG, "Context is not set, cannot initialize permissions")
      return
    }

    requestPermissionLauncher =
        context!!.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
          if (isGranted) {
            Log.d(TAG, "Permission granted")
          } else {
            // TODO: Inform user that that your app will not show notifications.
            Log.d(TAG, "Permission denied")
          }
        }

    askNotificationPermission()
    createChannels()
  }

  /**
   * Called when a message (a notification) is received.
   *
   * When the application is on the foreground, the notification is displayed as an alert, using the
   * `notificationAlert()` function.
   *
   * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
   */
  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)

    if (context == null || navigationActions == null) {
      Log.e(TAG, "Context or navigationActions is not set, cannot show notification")
      return
    }

    val data = remoteMessage.data
    val notificationBody = remoteMessage.notification

    Log.d(TAG, "From: ${remoteMessage.from}, data: $data, notification: $notificationBody")

    if (data.isNotEmpty() && notificationBody != null) {
      val date =
          try {
            Date.valueOf(data["creationDate"])
          } catch (e: IllegalArgumentException) {
            Date(System.currentTimeMillis())
          }

      val type =
          try {
            Notification.Type.valueOf(data["type"] ?: "")
          } catch (e: IllegalArgumentException) {
            Notification.Type.DEFAULT
          }

      val notification =
          Notification(
              title = notificationBody.title ?: "",
              message = notificationBody.body ?: "",
              type = type,
              creationDate = date,
              navigationUrl = data["navigationUrl"])

      notificationAlert(context!!, notification, navigationActions!!)
    }
  }

  /**
   * Called if the FCM registration token is updated. This may occur if the security of the previous
   * token had been compromised. Note that this is called when the FCM registration token is
   * initially generated so this is where you would retrieve the token.
   *
   * It updates the FCM token in the database if it has changed.
   */
  override fun onNewToken(token: String) {
    Log.d(TAG, "Refreshed token: $token")

    db.getCurrentUser { user ->
      checkToken(user.id) { newToken -> db.updateFCMToken(user.id, newToken) }
    }
  }

  /**
   * Gets the FCM registration token of the device.
   *
   * @param onSuccess Callback function that is called when the token is successfully retrieved.
   */
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

              val token = task.result
              Log.d(TAG, token)

              onSuccess(token)
            })
  }

  /**
   * Compares the FCM token of the device with the token stored in the database. If the token has
   * changed, it updates the token in the database.
   *
   * @param userId The ID of the user whose token is being checked.
   * @param onSuccess Callback function that is called when the token is successfully checked.
   */
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

  /** Asks the user for permission to show notifications. */
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

  /**
   * Creates a notification channel.
   *
   * The default priority is set to `NotificationManager.IMPORTANCE_DEFAULT`.
   *
   * @param context The context of the application.
   * @param channelId The ID of the channel.
   * @param name The name of the channel.
   * @param description The description of the channel.
   */
  fun createChannel(context: MainActivity, channelId: String, name: String, description: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      val importance = NotificationManager.IMPORTANCE_DEFAULT

      val channel =
          NotificationChannel(channelId, name, importance).apply { this.description = description }

      val notificationManager = context.getSystemService(NotificationManager::class.java)
      notificationManager.createNotificationChannel(channel)
    }
  }

  /**
   * Creates the notification channels for the application, being:
   * - Incoming
   * - Outgoing
   * - Social
   */
  fun createChannels() {
    if (context == null) {
      Log.e(TAG, "Context is not set, cannot create channels")
      return
    }

    createChannel(
        context!!,
        Notification.Channels.INCOMING.id(),
        context!!.getString(R.string.incoming_name),
        context!!.getString(R.string.incoming_description))

    createChannel(
        context!!,
        Notification.Channels.OUTGOING.id(),
        context!!.getString(R.string.outgoing_name),
        context!!.getString(R.string.outgoing_description))

    createChannel(
        context!!,
        Notification.Channels.SOCIAL.id(),
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
                response.close()
              }
            })
  }

  // Usefully for testing
  fun getJSON(): JSONObject {
    return JSONObject()
  }

  /**
   * Sends a notification to a specific user, using the FCM server.
   *
   * @param content The content of the notification.
   * @param to The FCM token of the user to whom the notification is being sent.
   */
  fun sendNotification(content: Notification, to: String) {
    if (context == null) {
      Log.e(TAG, "Context is not set, cannot send notification")
      return
    }

    val notificationField =
        getJSON().apply {
          put("title", content.title)
          put("body", content.message)
          put("android_channel_id", content.type.channelId())
        }

    val data =
        getJSON().apply {
          put("type", content.type)
          put("creationDate", content.creationDate)
          put("navigationUrl", content.navigationUrl)
        }

    val body = getJSON()
    body.put("notification", notificationField)
    body.put("data", data)
    body.put("to", to)

    sendPostRequest(FCM_SERVER_URL, body.toString())
  }

  companion object {
    private const val TAG = "FirebaseMessagingService"
    private const val FCM_SERVER_URL = "https://fcm.googleapis.com/fcm/send"
  }
}
