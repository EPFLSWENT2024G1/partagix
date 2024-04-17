package com.android.partagix.authentication

import android.content.Intent
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.Database
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.screens.NavigationBar
import com.android.partagix.ui.App
import com.android.partagix.ui.MainActivity
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
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

@RunWith(AndroidJUnit4::class)
class NavigationTest {
  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var authentication: Authentication

  @Before
  fun setup() {
    authentication = mockk<Authentication>()
    val mockMain = mockk<MainActivity>()
    val mockResult: ActivityResultLauncher<Intent> = mockk()

    every { authentication.isAlreadySignedIn() } returns true

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


    every {mockTask.addOnSuccessListener {  } } returns mockTask
    every {mockTask.addOnFailureListener {  } } returns mockTask
    // Create Database instance
    val d = Database(mockDb)

    composeTestRule.setContent { App(mockMain, authentication, d) }
  }

  @Test
  fun basicDisplay() {
    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {

    }
  }
  @Test
  fun testNavigation() {
    ComposeScreen.onComposeScreen<NavigationBar>(composeTestRule) {
      loanButton { performClick() }
    }
    //verify { mockNavActions.navigateTo(Route.LOAN) }
  }

  companion object {
    const val SLEEP_TIME = 2000L
  }
}
