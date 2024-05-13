package com.android.partagix.ui.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.partagix.R
import com.android.partagix.model.auth.Authentication
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

private const val TAG = "BootActivity"

/**
 * Screen to display the boot screen.
 *
 * @param authentication an Authentication instance to handle login.
 * @param navigationActions a NavigationActions instance to navigate between screens.
 * @param modifier Modifier to apply to this layout.
 */
@Composable
fun BootScreen(
    authentication: Authentication,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier
) {
  Box(
      modifier =
          modifier
              .background(color = MaterialTheme.colorScheme.background)
              .fillMaxSize()
              .testTag("BootScreen")) {
        Image(
            alignment = Alignment.Center,
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Logo",
            contentScale = ContentScale.FillBounds,
            modifier =
                Modifier.aspectRatio(1f)
                    .requiredWidth(width = 150.dp)
                    .requiredHeight(height = 150.dp)
                    .testTag("BootLogo"))

        Handler(Looper.getMainLooper())
            .postDelayed(
                {
                  if (authentication.isAlreadySignedIn()) {
                    navigationActions.navigateTo(Route.HOME)
                  } else {
                    navigationActions.navigateTo(Route.LOGIN)
                  }
                },
                0)
      }
}
