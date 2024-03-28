package com.android.partagix.ui.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.partagix.R
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

private const val TAG = "BootActivity"

@Composable
fun BootScreen(navigationActions: NavigationActions, modifier: Modifier = Modifier) {
  Column(
      modifier = modifier.padding(15.dp).testTag("BootScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(50.dp, Alignment.CenterVertically),
  ) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Logo",
        contentScale = ContentScale.FillBounds,
        modifier =
            Modifier.aspectRatio(1f)
                .requiredWidth(width = 189.dp)
                .requiredHeight(height = 189.dp)
                .testTag("LoginLogo"))
  }

  // wait 2sec before navigating to login screen
  Handler(Looper.getMainLooper()).postDelayed({ navigationActions.navigateTo(Route.LOGIN) }, 2000)
}
