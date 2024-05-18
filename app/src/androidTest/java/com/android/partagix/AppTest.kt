package com.android.partagix

import androidx.activity.result.registerForActivityResult
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.Database
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.notification.FirebaseMessagingService
import com.android.partagix.model.user.User
import com.android.partagix.ui.App
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseUser
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

@RunWith(AndroidJUnit4::class)
class AppTest {
  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

  @Mock private lateinit var mockActivity: MainActivity

  @Mock private lateinit var mockAuth: Authentication

  @Mock private lateinit var mockDatabase: Database

  @Mock private lateinit var mockNavActions: NavigationActions

  @Mock private lateinit var mockFirebaseUser: FirebaseUser

  @Mock private lateinit var mockNavigationActions: FirebaseMessagingService

  @Mock private lateinit var mockNotificationManager: FirebaseMessagingService

  @Mock private lateinit var mockFusedLocationProviderClient: FusedLocationProviderClient

  private lateinit var app: App
  private val new_token = "new_token"

  @Before
  fun setup() {
    mockActivity = mockk()

    every {
      mockActivity.registerForActivityResult(any<FirebaseAuthUIActivityResultContract>(), any())
    } returns mockk()
    every { mockActivity.applicationContext } returns mockk()
    every { mockActivity.attributionTag } returns "testTag"

    mockAuth = spyk(Authentication(mockActivity, mockk()))

    mockDatabase = spyk(Database())
    every { mockDatabase.createUser(any()) } just Runs
    every { mockDatabase.getUser(any(), any(), any()) } just Runs

    mockNavActions = spyk(NavigationActions(mockk()))
    every { mockNavActions.navigateTo(any<String>()) } just Runs

    mockFirebaseUser = mockk()
    every { mockFirebaseUser.uid } returns "testUid"
    every { mockFirebaseUser.displayName } returns "testUser"

    mockNotificationManager = mockk()
    every { mockNotificationManager.sendNotification(any(), any()) } just Runs
    every { mockNotificationManager.initPermissions() } just Runs
    every { mockNotificationManager.checkToken(any(), any()) } answers
        {
          val callback = secondArg<(String) -> Unit>()
          callback(newToken)
        }

    mockFusedLocationProviderClient = mockk()

    app =
        App(
            mockActivity,
            mockAuth,
            mockDatabase,
            mockNavigationActions,
            mockNotificationManager,
            mockFusedLocationProviderClient)
    app.navigationActions = mockNavActions
  }

  @After
  fun tearDown() {
    clearAllMocks()
  }

  @Test
  fun signInSuccess_navigatesToHome() {
    app.setNavigationActionsInitialized(true)
    app.onSignInSuccess(mockFirebaseUser)

    verify { mockNavActions.navigateTo(Route.HOME) }
  }

  @Test
  fun signInSuccess_noUser() {
    app.onSignInSuccess(null)

    verify(exactly = 0) { mockNavActions.navigateTo(Route.BOOT) }
  }

  @Test
  fun signInSuccess_createsNewUserInDatabase() {
    app.setNavigationActionsInitialized(true)
    app.onSignInSuccess(mockFirebaseUser)

    val expectedUser =
        User("testUid", "testUser", "Unknown Location", "0", Inventory("testUid", emptyList()))

    every { mockDatabase.createUser(expectedUser) } just Runs
    every { mockDatabase.getUser("testUid", any(), any()) } answers
        {
          val callback = firstArg<() -> Unit>()
          callback()
        }

    verify {
      mockDatabase.getUser("testUid", any(), any())
      mockNavActions.navigateTo(Route.HOME)
    }
  }

  @Test
  fun signInFailure_navigatesToBoot() {
    app.onSignInFailure(123)

    verify { mockNavActions.navigateTo(Route.BOOT) }
  }

  @Test
  fun navigateForTest() {
    app.navigateForTest(Route.INVENTORY)

    verify { mockNavActions.navigateTo(Route.INVENTORY) }
  }
}
