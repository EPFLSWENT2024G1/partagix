package com.android.partagix.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.android.partagix.model.Database
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.screens.NavigationBar
import com.android.partagix.ui.App
import com.android.partagix.ui.MainActivity
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.gms.common.api.internal.LifecycleFragment
import com.google.android.gms.common.api.internal.zaae
import com.google.android.gms.common.api.internal.zzb
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.database.Query
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.screens.LoginScreen
import io.mockk.core.ValueClassSupport.boxedValue
import org.hamcrest.core.Is.`is`


@RunWith(AndroidJUnit4::class)
class NavigationTest {
  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var authentication: Authentication

  /*
      val mockContext = mockk<Context>()
      val mockPackageManager = mockk<PackageManager>()
      val mockFragmentManager = mockk<android.app.FragmentManager>()
      val mockFragment = mockk<android.app.Fragment>()
      val mockzzb : zzb = mockk()

      every {mockMain.applicationContext} returns mockContext
      every {mockMain.attributionTag} returns ""
      every {mockMain.fragmentManager} returns mockFragmentManager

      every { mockContext.applicationContext } returns mockContext
      every {mockContext.packageManager} returns mockPackageManager

      every { mockPackageManager.hasSystemFeature(any()) } returns true

      every {mockFragmentManager.findFragmentByTag(any())} returns mockzzb
      every {mockFragmentManager.beginTransaction()} returns mockk()

      every {mockzzb.isRemoving} returns false

      // let's print everything we can to debug
      println("------ mockMain: $mockMain")
      println("------ mockContext: $mockContext")
      println("------ mockPackageManager: $mockPackageManager")
      println("------ mockFragmentManager: $mockFragmentManager")
      println("------ mockFragment: $mockFragment")
      println("------ mockResult: $mockResult")
      println("----- mockMain.applicationContext: ${mockMain.applicationContext}")

   */


  //every { LocationServices.getFusedLocationProviderClient(any<Activity>()) } returns mockk<FusedLocationProviderClient>()
  //every { LocationServices.getFusedLocationProviderClient(any<Context>()) } returns mockk<FusedLocationProviderClient>()

  @Before
  fun setup() {
    println("----- NavigationTest")

    authentication = mockk<Authentication>()
    every { authentication.isAlreadySignedIn() } returns true


    val mockMain = mockk<MainActivity>()
    val mockResult: ActivityResultLauncher<Intent> = mockk()
    every { mockMain.
    registerForActivityResult(
      any<FirebaseAuthUIActivityResultContract>(), any()
    )
    } returns mockResult


    val mockDb: FirebaseFirestore = mockk()
    val mockCollection = mockk<CollectionReference>()
    val mockTask = mockk<Task<QuerySnapshot>>()
    val mockDocument = mockk<DocumentReference>()

    every { mockDb.collection(any()) } returns mockCollection

    every {mockCollection.get()} returns mockTask
    every { mockCollection.document(any()) } returns mockDocument
    every { mockCollection.document() } returns mockDocument

    val mockQuerySnapshot = mockk<QuerySnapshot>()

    // Wrap the QuerySnapshot into a Task object
    every { mockTask.result } returns mockQuerySnapshot

    // Define the behavior for the `addOnSuccessListener` function call
    every { mockTask.addOnSuccessListener(any()) } returns mockTask
    every { mockTask.addOnFailureListener(any()) } returns mockTask


    // Create Database instance
    val d = Database(mockDb)
    composeTestRule.setContent { App( mockMain, authentication, d).Create() }

  }
  @Test
  fun basicDisplay() {
    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {

    }
  }
  companion object {
    const val SLEEP_TIME = 2000L
  }
}
@RunWith(AndroidJUnit4::class)
class MyTestSuite {
  @get:Rule
  val composeTestRule = createComposeRule()
  @Test
  fun testEvent() {
    val scenario = launch(MainActivity::class.java)

    // Create an instance of NavigationBar screen
    val navigationBar = NavigationBar(composeTestRule)

    composeTestRule.waitForIdle()

    // Verify the visibility of navigation bar buttons
    navigationBar.apply {
      homeButton.assertIsDisplayed()
    }

    // Close the activity after the test
    scenario.close()

  }
  @Test
  fun testIsh(){
    composeTestRule.setContent { BottomNavigationBar(
      selectedDestination = "Home",
      navigateToTopLevelDestination = { dest -> println("----- dest: $dest") })
    }

    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {
      homeButton { assertIsDisplayed() }
    }
  }
  @Test
  fun testIsh2(){
    val scenario = launch(MainActivity::class.java)

    // Wait for the activity to be in the resumed state
    scenario.moveToState(Lifecycle.State.RESUMED)

    // Verify that the bottom navigation bar is displayed
    composeTestRule.onNodeWithTag("navigationBar").assertIsDisplayed()

    // Verify that the home button is displayed
    composeTestRule.onNodeWithTag("bottomNavBarItem-Home").assertIsDisplayed()

    // Verify that the loan button is displayed
    composeTestRule.onNodeWithTag("bottomNavBarItem-Loan").assertIsDisplayed()

    // Verify that the inventory button is displayed
    composeTestRule.onNodeWithTag("bottomNavBarItem-Inventory").assertIsDisplayed()

    // Verify that the account button is displayed
    composeTestRule.onNodeWithTag("bottomNavBarItem-Account").assertIsDisplayed()
  }


}




