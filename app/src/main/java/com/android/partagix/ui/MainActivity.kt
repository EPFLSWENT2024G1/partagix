package com.android.partagix.ui

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.partagix.R
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.auth.SignInResultListener
import com.android.partagix.resources.C
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.BootScreen
import com.android.partagix.ui.screens.HomeScreen
import com.android.partagix.ui.screens.LoginScreen
import com.android.partagix.ui.theme.PartagixAppTheme
import com.google.firebase.auth.FirebaseUser
import uploadImageToFirebaseStorage
import java.io.File

class MainActivity : ComponentActivity(), SignInResultListener {
  private lateinit var authentication: Authentication
  private lateinit var navigationActions: NavigationActions

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    authentication = Authentication(this, this)

    setContent {
      PartagixAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              Main("Android")
            }
      }
    }
  }

  override fun onSignInSuccess(user: FirebaseUser?) {
    navigationActions.navigateTo(Route.HOME)
  }

  override fun onSignInFailure(errorCode: Int) {
    // TODO
  }

  @Composable
  fun Main(name: String, modifier: Modifier = Modifier) {
    val resourceId: Int = R.drawable.logo // Replace "your_image" with the name of your image resource
    val imageUri: Uri = Uri.Builder()
      .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
      .authority(resources.getResourcePackageName(resourceId))
      .appendPath(resources.getResourceTypeName(resourceId))
      .appendPath(resources.getResourceEntryName(resourceId))
      .build()
    println("----- $imageUri")

    uploadImageToFirebaseStorage(imageUri)

    val navController = rememberNavController()
    navigationActions = NavigationActions(navController)

    NavHost(navController = navController, startDestination = Route.LOGIN) {
      composable(Route.BOOT) { BootScreen(authentication, navigationActions, modifier) }
      composable(Route.LOGIN) { LoginScreen(authentication, modifier) }
      composable(Route.HOME) { HomeScreen() }
    }

    navigationActions.navigateTo(Route.BOOT)
  }

  @Composable
  fun test() {
    val inventoryViewModel: InventoryViewModel by viewModels()

    val uiState by inventoryViewModel.uiState.collectAsStateWithLifecycle()

    App(
        inventoryViewModel = inventoryViewModel,
    )
  }

  companion object {
    private const val TAG = "Main"
  }
}
