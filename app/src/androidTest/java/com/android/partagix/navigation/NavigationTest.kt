package com.android.partagix.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.screens.InventoryScreen
import com.android.partagix.screens.NavigationBar
import com.android.partagix.ui.MainActivity
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/*
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

  // every { LocationServices.getFusedLocationProviderClient(any<Activity>()) } returns
  // mockk<FusedLocationProviderClient>()
  // every { LocationServices.getFusedLocationProviderClient(any<Context>()) } returns
  // mockk<FusedLocationProviderClient>()

  @Before
  fun setup() {
    println("----- NavigationTest")

    authentication = mockk<Authentication>()
    every { authentication.isAlreadySignedIn() } returns true

    val mockMain = mockk<MainActivity>()
    val mockResult: ActivityResultLauncher<Intent> = mockk()
    every {
      mockMain.registerForActivityResult(any<FirebaseAuthUIActivityResultContract>(), any())
    } returns mockResult

    val mockDb: FirebaseFirestore = mockk()
    val mockCollection = mockk<CollectionReference>()
    val mockTask = mockk<Task<QuerySnapshot>>()
    val mockDocument = mockk<DocumentReference>()

    every { mockDb.collection(any()) } returns mockCollection

    every { mockCollection.get() } returns mockTask
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
    composeTestRule.setContent { App(mockMain, authentication, d).Create() }
  }

  @Test
  fun basicDisplay() {
    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {}
  }

  companion object {
    const val SLEEP_TIME = 2000L
  }
}

 */

@RunWith(AndroidJUnit4::class)
class MyTestSuite {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val composeTestRule2 = createComposeRule()

  /*
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

   */
  @Test
  fun testIsh2() {
    val scenario = launch(MainActivity::class.java)

    scenario.onActivity { mainActivity ->
      // Call functions on MainActivity instance here
      mainActivity.myInitializationFunction()
    }

    // Wait for the activity to be in the resumed state
    scenario.moveToState(Lifecycle.State.RESUMED)

    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {
      homeButton { assertIsDisplayed() }
      loanButton { assertIsDisplayed() }
      inventoryButton { assertIsDisplayed() }
      accountButton { assertIsDisplayed() }

      inventoryButton { performClick() }
    }

    ComposeScreen.onComposeScreen<InventoryScreen>(composeTestRule) {}
  }
}
