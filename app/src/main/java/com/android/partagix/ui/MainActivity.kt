package com.android.partagix.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.auth.SignInResultListener
import com.android.partagix.resources.C
import com.android.partagix.ui.theme.PartagixAppTheme
import com.google.firebase.auth.FirebaseUser

class MainActivity : ComponentActivity(), SignInResultListener {
  private lateinit var authentication: Authentication

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    authentication = Authentication(this, this)

    setContent {
      PartagixAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              //Greeting("Android")
                test()
            }
      }
    }
  }

  override fun onSignInSuccess(user: FirebaseUser?) {
    // TODO
  }

  override fun onSignInFailure(errorCode: Int) {
    // TODO
  }

  @Composable
  fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier.semantics { testTag = C.Tag.greeting })

    OutlinedButton(
        onClick = {
          Log.w(TAG, "push button to sign in")

          authentication.signIn()
        },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFDADCE0)),
        modifier = modifier.fillMaxWidth(),
    ) {
      Text("Sign in")
    }
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
