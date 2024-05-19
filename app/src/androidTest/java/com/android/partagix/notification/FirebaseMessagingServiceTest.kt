package com.android.partagix.notification

import android.app.NotificationManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.registerForActivityResult
import androidx.core.content.ContextCompat
import androidx.test.rule.GrantPermissionRule
import com.android.partagix.MainActivity
import com.android.partagix.model.Database
import com.android.partagix.model.notification.FirebaseMessagingService
import com.android.partagix.model.notification.Notification
import com.android.partagix.model.user.User
import com.android.partagix.ui.components.notificationAlert
import com.android.partagix.ui.navigation.NavigationActions
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import io.mockk.Called
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyAll
import java.sql.Date
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FirebaseMessagingServiceTest {
  companion object {
    private const val FIREBASE_PUSH_TOKEN = "mocked_token_value"
    private const val NB_NOTIFICATION_CHANNELS = 3
  }

  private val mockedMainActivity = mockk<MainActivity>()
  private val mockedNavigationActions = mockk<NavigationActions>()
  private var mockedDB = mockk<Database>()

  private lateinit var mockedFirebaseMessagingService: FirebaseMessagingService

  @get:Rule
  val grantPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS)

  @Before
  fun setup() {
    mockkStatic(MainActivity::class)
    every { MainActivity.getContext() } returns mockedMainActivity

    every { mockedDB.getCurrentUser(any()) } just runs

    mockedFirebaseMessagingService =
        spyk(FirebaseMessagingService(db = mockedDB, navigationActions = mockedNavigationActions))

    mockedFirebaseMessagingService.onCreate()
    mockedFirebaseMessagingService.onNewToken(FIREBASE_PUSH_TOKEN)
  }

  @Test
  fun testInitPermissionsContextNull() {
    every { MainActivity.getContext() } returns null
    every {
          mockedMainActivity.registerForActivityResult(
              any<ActivityResultContract<String, Boolean>>(),
              any<ActivityResultCallback<Boolean>>())
        }
        .answers {
          val callback = secondArg<ActivityResultCallback<Boolean>>()
          callback.onActivityResult(true)

          mockk<ActivityResultLauncher<String>>()
        }

    mockedFirebaseMessagingService = spyk(FirebaseMessagingService())
    mockedFirebaseMessagingService.initPermissions()

    verify(exactly = 0) { mockedFirebaseMessagingService.askNotificationPermission() }
    verify(exactly = 0) { mockedFirebaseMessagingService.createChannels() }
  }

  @Test
  fun testInitPermissionsTrue() {
    every {
          mockedMainActivity.registerForActivityResult(
              any<ActivityResultContract<String, Boolean>>(),
              any<ActivityResultCallback<Boolean>>())
        }
        .answers {
          val callback = secondArg<ActivityResultCallback<Boolean>>()
          callback.onActivityResult(true)

          mockk<ActivityResultLauncher<String>>()
        }

    every { mockedFirebaseMessagingService.askNotificationPermission() } just runs
    every { mockedFirebaseMessagingService.createChannels() } just runs

    mockedFirebaseMessagingService.initPermissions()

    verify { mockedFirebaseMessagingService.askNotificationPermission() }
    verify { mockedFirebaseMessagingService.createChannels() }
  }

  @Test
  fun testInitPermissionsFalse() {
    every {
          mockedMainActivity.registerForActivityResult(
              any<ActivityResultContract<String, Boolean>>(),
              any<ActivityResultCallback<Boolean>>())
        }
        .answers {
          val callback = secondArg<ActivityResultCallback<Boolean>>()
          callback.onActivityResult(false)

          mockk<ActivityResultLauncher<String>>()
        }

    every { mockedFirebaseMessagingService.askNotificationPermission() } just runs
    every { mockedFirebaseMessagingService.createChannels() } just runs

    mockedFirebaseMessagingService.initPermissions()

    // Currently it calls anyway
    verify { mockedFirebaseMessagingService.askNotificationPermission() }
    verify { mockedFirebaseMessagingService.createChannels() }
  }

  @Test
  fun testOnMessageReceivedContextNull() {
    every { MainActivity.getContext() } returns null

    mockkStatic("com.android.partagix.ui.components.NotificationAlertKt")
    every { notificationAlert(any(), any(), any()) } just Runs

    mockedFirebaseMessagingService = spyk(FirebaseMessagingService())

    val remoteMessage = mockk<RemoteMessage>()
    every { remoteMessage.data } returns mutableMapOf()
    every { remoteMessage.from } returns "sender"
    every { remoteMessage.notification } returns mockk()
    every { remoteMessage.notification?.title } returns "notification title"
    every { remoteMessage.notification?.body } returns "notification body"

    mockedFirebaseMessagingService.onMessageReceived(remoteMessage)

    verify { mockedFirebaseMessagingService.onMessageReceived(any()) }
    verify(exactly = 0) { notificationAlert(any(), any(), any()) }
  }

  @Test
  fun testOnMessageReceivedNavigationActionsNull() {
    mockedFirebaseMessagingService =
        spyk(FirebaseMessagingService(db = mockedDB, navigationActions = null))

    mockedFirebaseMessagingService.onCreate()
    mockedFirebaseMessagingService.onNewToken(FIREBASE_PUSH_TOKEN)

    mockkStatic("com.android.partagix.ui.components.NotificationAlertKt")
    every { notificationAlert(any(), any(), any()) } just Runs

    mockedFirebaseMessagingService = spyk(FirebaseMessagingService())

    val remoteMessage = mockk<RemoteMessage>()
    every { remoteMessage.data } returns mutableMapOf()
    every { remoteMessage.from } returns "sender"
    every { remoteMessage.notification } returns mockk()
    every { remoteMessage.notification?.title } returns "notification title"
    every { remoteMessage.notification?.body } returns "notification body"

    mockedFirebaseMessagingService.onMessageReceived(remoteMessage)

    verify { mockedFirebaseMessagingService.onMessageReceived(any()) }
    verify(exactly = 0) { notificationAlert(any(), any(), any()) }
  }

  @Test
  fun testOnMessageReceivedDataEmpty() {
    mockkStatic("com.android.partagix.ui.components.NotificationAlertKt")
    every { notificationAlert(any(), any(), any()) } just Runs

    val remoteMessage = mockk<RemoteMessage>()
    val notification = mockk<RemoteMessage.Notification>()
    val data = mutableMapOf<String, String>()

    val sender = "sender"

    every { remoteMessage.data } returns data
    every { remoteMessage.from } returns sender
    every { remoteMessage.notification } returns notification
    every { notification.title } returns "notification title"
    every { notification.body } returns "notification body"

    mockedFirebaseMessagingService.onMessageReceived(remoteMessage)

    verify { mockedFirebaseMessagingService.onMessageReceived(remoteMessage) }
    verify(exactly = 0) { notificationAlert(any(), any(), any()) }
  }

  @Test
  fun testOnMessageReceivedNoNotification() {
    mockkStatic("com.android.partagix.ui.components.NotificationAlertKt")
    every { notificationAlert(any(), any(), any()) } just Runs

    val remoteMessage = mockk<RemoteMessage>()
    val data = mutableMapOf<String, String>()

    val sender = "sender"

    every { remoteMessage.data } returns data
    every { remoteMessage.from } returns sender
    every { remoteMessage.notification } returns null
    every { remoteMessage.notification?.title } returns "notification title"
    every { remoteMessage.notification?.body } returns "notification body"

    mockedFirebaseMessagingService.onMessageReceived(remoteMessage)

    verify { mockedFirebaseMessagingService.onMessageReceived(remoteMessage) }
    verify(exactly = 0) { notificationAlert(any(), any(), any()) }
  }

  @Test
  fun testOnMessageReceivedWithData() {
    mockkStatic("com.android.partagix.ui.components.NotificationAlertKt")
    every { notificationAlert(any(), any(), any()) } just Runs

    val remoteMessage = mockk<RemoteMessage>()
    val notification = mockk<RemoteMessage.Notification>()

    val data =
        mutableMapOf(
            "title" to "title",
            "message" to "message",
            "navigationUrl" to "navigationUrl",
            "creationDate" to "2021-09-01")

    val sender = "sender"

    every { remoteMessage.data } returns data
    every { remoteMessage.from } returns sender
    every { remoteMessage.notification } returns notification
    every { notification.title } returns "notification title"
    every { notification.body } returns "notification body"

    mockedFirebaseMessagingService.onMessageReceived(remoteMessage)

    verify { mockedFirebaseMessagingService.onMessageReceived(remoteMessage) }
    verify { notificationAlert(any(), any(), any()) }
  }

  @Test
  fun testOnNewToken() {
    val emptyUser = mockk<User>()
    every { emptyUser.id } returns "id"

    clearMocks(mockedDB)
    every { mockedDB.getCurrentUser(any()) } answers
        {
          val callback = firstArg<(User) -> Unit>()
          callback(emptyUser)
        }
    every { mockedFirebaseMessagingService.checkToken(any(), any()) } just runs

    val token = "new_token"

    mockedFirebaseMessagingService.onNewToken(token)

    verify { mockedFirebaseMessagingService.checkToken(any(), any()) }
  }

  @Test
  fun getTokenContextNull() {
    every { MainActivity.getContext() } returns null

    mockkStatic(FirebaseMessaging::class)
    every { FirebaseMessaging.getInstance() } returns mockk()

    val mockedTask: Task<String> = mockk()
    every { FirebaseMessaging.getInstance().token } returns mockedTask
    every { mockedTask.addOnSuccessListener(any()) } returns mockedTask
    every { mockedTask.addOnCompleteListener(any()) } returns mockedTask

    mockedFirebaseMessagingService = spyk(FirebaseMessagingService())

    val getToken =
        FirebaseMessagingService::class.java.getDeclaredMethod("getToken", Function1::class.java)
    getToken.isAccessible = true

    var newToken = ""

    getToken.invoke(
        mockedFirebaseMessagingService,
        object : Function1<String, Unit> {
          override fun invoke(p1: String): Unit {
            newToken = p1
          }
        })

    verifyAll { FirebaseMessaging.getInstance() wasNot Called }
  }

  @Test
  fun getTokenWorks() {
    mockkStatic(FirebaseMessaging::class)
    every { FirebaseMessaging.getInstance() } returns mockk()

    val mockedTask: Task<String> = mockk()
    every { FirebaseMessaging.getInstance().token } returns mockedTask
    every { mockedTask.addOnSuccessListener(any()) } returns mockedTask
    every { mockedTask.addOnCompleteListener(any()) } returns mockedTask

    val getToken =
        FirebaseMessagingService::class.java.getDeclaredMethod("getToken", Function1::class.java)
    getToken.isAccessible = true

    runBlocking {
      getToken.invoke(
          mockedFirebaseMessagingService,
          object : Function1<String, Unit> {
            override fun invoke(newToken: String): Unit {
              assert(newToken == FIREBASE_PUSH_TOKEN)
            }
          })
    }
  }

  @Test
  fun checkTokenSameWorks() {
    every { mockedFirebaseMessagingService.getToken(any()) } answers
        {
          val onSuccess = firstArg<Function1<String, Unit>>()
          onSuccess(FIREBASE_PUSH_TOKEN)
        }

    every { mockedDB.getFCMToken(any(), any()) } answers
        {
          val callback = secondArg<(String?) -> Unit>()
          callback(FIREBASE_PUSH_TOKEN)
        }

    every { mockedDB.updateFCMToken(any(), any()) } just runs

    val mockedOnSuccess: (String) -> Unit = mockk()
    every { mockedOnSuccess(any()) } just runs

    mockedFirebaseMessagingService.checkToken(FIREBASE_PUSH_TOKEN, mockedOnSuccess)

    verify { mockedOnSuccess(FIREBASE_PUSH_TOKEN) }
    verify(exactly = 0) { mockedDB.updateFCMToken(any(), any()) }
  }

  @Test
  fun checkTokenDiffWorks() {
    val oldToken = FIREBASE_PUSH_TOKEN
    val newToken = "$FIREBASE_PUSH_TOKEN!!!"

    every { mockedFirebaseMessagingService.getToken(any()) } answers
        {
          val onSuccess = firstArg<Function1<String, Unit>>()
          onSuccess(newToken)
        }

    every { mockedDB.getFCMToken(any(), any()) } answers
        {
          val callback = secondArg<(String?) -> Unit>()
          callback(oldToken)
        }

    every { mockedDB.updateFCMToken(any(), any()) } just runs

    val mockedOnSuccess: (String) -> Unit = mockk()
    every { mockedOnSuccess(any()) } just runs

    mockedFirebaseMessagingService.checkToken(newToken, mockedOnSuccess)

    verify { mockedOnSuccess(newToken) }
    verify { mockedDB.updateFCMToken(any(), any()) }
  }

  @Test
  fun askNotificationPermissionContextNull() {
    every { MainActivity.getContext() } returns null
    mockkStatic(ContextCompat::class)

    mockedFirebaseMessagingService = spyk(FirebaseMessagingService())
    mockedFirebaseMessagingService.askNotificationPermission()

    verify(exactly = 0) { ContextCompat.checkSelfPermission(any(), any()) }
  }

  @Test
  fun createChannelsContextNull() {
    every { MainActivity.getContext() } returns null
    mockkStatic(ContextCompat::class)

    mockedFirebaseMessagingService = spyk(FirebaseMessagingService())
    mockedFirebaseMessagingService.createChannels()

    verify(exactly = 0) { mockedFirebaseMessagingService.createChannel(any(), any(), any(), any()) }
  }

  @Test
  fun createChannelsWork() {
    every { mockedMainActivity.getString(any()) } returns "1"
    every { mockedMainActivity.getSystemService(NotificationManager::class.java) } returns mockk()
    every { mockedFirebaseMessagingService.createChannel(any(), any(), any(), any()) } just runs

    mockedFirebaseMessagingService.createChannels()

    verify(exactly = NB_NOTIFICATION_CHANNELS) {
      mockedFirebaseMessagingService.createChannel(any(), any(), any(), any())
    }
  }

  @Test
  fun sendNotificationContextNull() {
    every { MainActivity.getContext() } returns null
    val mockedJSONObject = mockk<JSONObject>()

    every { mockedFirebaseMessagingService.getJSON() } returns mockedJSONObject

    mockedFirebaseMessagingService = spyk(FirebaseMessagingService())

    val notification: Notification = mockk()
    val to = "to"
    mockedFirebaseMessagingService.sendNotification(notification, to)

    verify { mockedJSONObject wasNot Called }
  }

  @Test
  fun sendNotificationWorks() {
    val mockedJSONObject = mockk<JSONObject>()
    every { mockedFirebaseMessagingService.getJSON() } returns mockedJSONObject
    every { mockedJSONObject.put(any(), any<String>()) } answers { mockedJSONObject }

    val notification: Notification = mockk()
    every { notification.title } returns "title"
    every { notification.message } returns "message"
    every { notification.navigationUrl } returns "navigationUrl"
    every { notification.type } returns Notification.Type.NEW_INCOMING_REQUEST
    every { notification.creationDate } returns Date.valueOf("2021-09-01")

    val to = "to"
    mockedFirebaseMessagingService.sendNotification(notification, to)

    // should not crash
  }
}
