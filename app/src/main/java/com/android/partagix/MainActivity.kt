package com.android.partagix.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import com.android.partagix.resources.C
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.theme.PartagixAppTheme

class MainActivity : ComponentActivity() {
  private lateinit var app: App

  override fun onCreate(savedInstanceState: Bundle?) {
    println("----- MainActivity")
    super.onCreate(savedInstanceState)
    app = App(this)

    setContent {
      PartagixAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              app.Create()
            }
      }
    }
  }

  fun myInitializationFunction() {
    app.navigateForTest(Route.HOME)
  }

  companion object {
    private const val TAG = "Main"
  }
}
