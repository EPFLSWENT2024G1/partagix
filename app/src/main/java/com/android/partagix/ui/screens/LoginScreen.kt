package com.android.partagix.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.partagix.R
import com.android.partagix.model.auth.Authentication

private const val TAG = "LoginActivity"

/**
 * Screen to display the login screen.
 *
 * @param authentication an Authentication instance to handle login.
 * @param modifier Modifier to apply to this layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(authentication: Authentication, modifier: Modifier = Modifier) {
  val sheetState = rememberModalBottomSheetState()
  var showSignIn by remember { mutableStateOf(false) }

  Column(
      modifier = modifier.padding(15.dp).testTag("LoginScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(50.dp, Alignment.CenterVertically),
  ) {
    Text(
        text = "Your neighbors are already here !",
        modifier = modifier.testTag("LoginTitle"),
        style =
            TextStyle(
                fontSize = 28.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(500),
                color = Color(0xFF000000),
            ))

    OutlinedButton(
        modifier = modifier.testTag("PopUpLoginButton").fillMaxWidth().padding(16.dp),
        onClick = { showSignIn = true },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xffafafaf)),
    ) {
      Text(
          text = "Start borrowing now",
          style =
              TextStyle(
                  fontSize = 28.sp,
                  fontFamily = FontFamily(Font(R.font.roboto)),
                  fontWeight = FontWeight(500),
                  color = Color(0xFF000000),
                  textAlign = TextAlign.Center,
              ),
          modifier = modifier.padding(12.dp).testTag("LoginButtonOpenBottomSheet"))
    }
  }
  if (showSignIn) {
    println("----- we are there")
    ModalBottomSheet(
        onDismissRequest = { showSignIn = false },
        sheetState = sheetState,
        modifier = modifier.fillMaxHeight(.35f),
    ) {
      Column(
          verticalArrangement = Arrangement.Top,
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = modifier.fillMaxSize().testTag("LoginScreen2"),
      ) {
        OutlinedButton(
            onClick = {
              Log.w(TAG, "push button to sign in")

              authentication.signIn()
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, Color(0xFFDADCE0)),
            modifier =
                modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 32.dp))
                    .testTag("LoginButton"),
        ) {
          Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = modifier.padding(horizontal = 24.dp)) {
                Image(
                    modifier = modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google Icon",
                )
                Text(
                    text = "Sign in with Google",
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    modifier =
                        modifier
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .padding(6.dp))
              }
        }
      }
    }
  }
}
