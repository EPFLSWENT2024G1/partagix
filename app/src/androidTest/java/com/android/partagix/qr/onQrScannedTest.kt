package com.android.partagix.qr

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.MainActivity
import com.android.partagix.model.Database
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.category.Category
import com.android.partagix.model.emptyConst.emptyInventory
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.notification.FirebaseMessagingService
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.ui.App
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import java.io.File
import java.util.Date
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class onQrScannedTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockAuthentication: Authentication
  @RelaxedMockK lateinit var mockDatabase: Database
  @RelaxedMockK lateinit var mockMainActivity: MainActivity
  @RelaxedMockK lateinit var mockPackageManager: PackageManager
  @RelaxedMockK lateinit var mockNotificationManager: FirebaseMessagingService
  @RelaxedMockK lateinit var mockFirebaseUser: FirebaseUser
  @RelaxedMockK lateinit var app: App

  @Before
  fun setup() {
    val user = mockk<FirebaseUser>()
    val mockActivityResultLauncher = mockk<ActivityResultLauncher<Intent>>()
    mockkObject(Authentication)

    mockMainActivity = mockk<MainActivity>()
    mockPackageManager = mockk<PackageManager>()
    mockDatabase = mockk<Database>()
    mockNavActions = mockk<NavigationActions>()
    mockAuthentication = mockk<Authentication>()
    mockNotificationManager = mockk<FirebaseMessagingService>()
    mockFirebaseUser = mockk<FirebaseUser>()

    every { Authentication.getUser() } returns user
    every { user.uid } returns "abcd"
    every { mockNavActions.navigateTo(any<String>()) } just runs

    every {
      mockMainActivity.registerForActivityResult(any<FirebaseAuthUIActivityResultContract>(), any())
    } returns mockActivityResultLauncher

    every { mockMainActivity.applicationContext } returns mockMainActivity
    every { mockMainActivity.attributionTag } returns "tag"
    every { mockPackageManager.hasSystemFeature(any()) } returns true
    every { mockPackageManager.getPackageInfo(any<String>(), any<Int>()) } returns null
    every { mockMainActivity.packageManager } returns mockPackageManager
    every { mockMainActivity.packageName } returns "com.android.partagix"
    every { mockNotificationManager.initPermissions() } just runs
    every { mockNotificationManager.checkToken(any(), any()) } answers
        {
          val callback = it.invocation.args[1] as (String) -> Unit
          callback("new_token")
        }

    every { mockDatabase.getItems(any()) } just Runs
    every { mockDatabase.getItemsWithImages(any()) } just Runs
    every { mockDatabase.getComments(any(), any()) } just Runs

    every { mockFirebaseUser.uid } returns "abcd"
    every { mockFirebaseUser.displayName } returns "name"
    every { mockFirebaseUser.email } returns "email"

    mockkStatic(FirebaseAuth::class)
    every { Authentication.getUser() } returns mockFirebaseUser
  }

  @Test
  fun testOnQrScannedWhenBorrower() {

    val loan = Loan("1234", "", "abcd", "efgh", Date(), Date(), "", "", "", "", LoanState.ACCEPTED)
    val item =
        Item(
            "efgh",
            Category("1234", "abcd"),
            "abcd",
            "abcd",
            Visibility.PUBLIC,
            1,
            Location(""),
            "")
    val borrower = User("abcd", "", "", "", emptyInventory, File("image"))
    val lender = emptyUser

    every { mockDatabase.getLoans(any()) } answers
        {
          val callback = it.invocation.args[0] as (List<Loan>) -> Unit
          callback(listOf(loan))
        }
    every { mockDatabase.getItem("efgh", any()) } answers
        {
          val callback = it.invocation.args[1] as (Item) -> Unit
          callback(item)
        }
    every { mockDatabase.getUser("abcd", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(borrower)
        }

    every { mockDatabase.getUser("", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(lender)
        }

    every { mockDatabase.getUser("1234", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(lender)
        }

    every { mockDatabase.getUserWithImage("abcd", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(borrower)
        }

    every { mockDatabase.getUserWithImage("", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(lender)
        }

    every { mockDatabase.getUserWithImage("1234", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(lender)
        }
    app = App(mockMainActivity, mockAuthentication, mockDatabase, mockNotificationManager)
    composeTestRule.setContent { app.Create("efgh", true, mockNavActions) }

    verify { mockNavActions.navigateTo(Route.STARTLOAN) }
  }

  @Test
  fun testOnQrScannedWhenLender() {

    val loan = Loan("1234", "abcd", "", "efgh", Date(), Date(), "", "", "", "", LoanState.ONGOING)
    val item =
        Item(
            "efgh",
            Category("1234", "abcd"),
            "abcd",
            "abcd",
            Visibility.PUBLIC,
            1,
            Location(""),
            "abcd")
    val borrower = emptyUser
    val lender = User("abcd", "", "", "", emptyInventory)

    every { mockDatabase.getLoans(any()) } answers
        {
          val callback = it.invocation.args[0] as (List<Loan>) -> Unit
          callback(listOf(loan))
        }
    every { mockDatabase.getItem("efgh", any()) } answers
        {
          val callback = it.invocation.args[1] as (Item) -> Unit
          callback(item)
        }
    every { mockDatabase.getUser("abcd", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(lender)
        }

    every { mockDatabase.getUser("", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(borrower)
        }

    every { mockDatabase.getUserWithImage("abcd", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(lender)
        }

    every { mockDatabase.getUserWithImage("", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(borrower)
        }

    every { mockDatabase.getUser("1234", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(borrower)
        }
    every { mockDatabase.getUserWithImage("1234", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(borrower)
        }

    app = App(mockMainActivity, mockAuthentication, mockDatabase, mockNotificationManager)
    composeTestRule.setContent { app.Create("efgh", true, mockNavActions) }

    verify { mockNavActions.navigateTo(Route.ENDLOAN) }
  }

  @Test
  fun testOnQrScannedWhenNothing() {

    val item =
        Item(
            "efgh",
            Category("1234", "abcd"),
            "abcd",
            "abcd",
            Visibility.PUBLIC,
            1,
            Location(""),
            "abcd")
    val borrower = emptyUser
    val lender = emptyUser

    every { mockDatabase.getLoans(any()) } answers
        {
          val callback = it.invocation.args[0] as (List<Loan>) -> Unit
          callback(listOf())
        }
    every { mockDatabase.getItem("efgh", any()) } answers
        {
          val callback = it.invocation.args[1] as (Item) -> Unit
          callback(item)
        }
    every { mockDatabase.getUser("abcd", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(lender)
        }

    every { mockDatabase.getUser("", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(borrower)
        }

    every { mockDatabase.getUserWithImage("abcd", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(lender)
        }

    every { mockDatabase.getUserWithImage("", any(), any()) } answers
        {
          val callback = it.invocation.args[2] as (User) -> Unit
          callback(borrower)
        }

    every { mockDatabase.getUser("1234", any(), any()) } just Runs
    every { mockDatabase.getUserWithImage("1234", any(), any()) } just Runs
    app = App(mockMainActivity, mockAuthentication, mockDatabase, mockNotificationManager)
    composeTestRule.setContent { app.Create("efgh", true, mockNavActions) }

    verify { mockNavActions.navigateTo(Route.VIEW_ITEM) }
  }
}
