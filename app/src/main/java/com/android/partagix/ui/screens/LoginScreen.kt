package com.android.partagix.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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

  Box(
      modifier =
          Modifier.fillMaxSize()
              .background(MaterialTheme.colorScheme.background)
              .testTag("Banner")) {
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .height(150.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))) {
              Text(
                  text = "Partagix™",
                  style = MaterialTheme.typography.displayLarge,
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.align(Alignment.Center))
            }

        Box(modifier = Modifier.testTag("CenterContent")) {
          Column(
              modifier = Modifier.fillMaxSize().padding(16.dp, 160.dp, 16.dp, 16.dp),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(
                text = "Your neighbors are already here !",
                style =
                    MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center),
            )
            val images =
                listOf(
                    R.drawable.multiprises,
                    R.drawable.holzkohle,
                    R.drawable.ks28,
                    R.drawable.remorque,
                    R.drawable.brouette,
                    R.drawable.gazon,
                )
            Box(modifier = Modifier.fillMaxWidth().graphicsLayer {}) {
              ImageGrid(images = images, onShowSignInChange = { showSignIn = it })
            }
          }
        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center) {
              Button(
                  onClick = { showSignIn = true },
                  modifier = Modifier.fillMaxWidth().height(60.dp).testTag("StartBorrowButton"),
              ) {
                Text(text = "Start Borrowing now", style = MaterialTheme.typography.titleLarge)
              }
            }
      }

  if (showSignIn) {
    ModalBottomSheet(
        onDismissRequest = { showSignIn = false },
        sheetState = sheetState,
        modifier = modifier.fillMaxHeight(.4f).testTag("LoginBottomSheet"),
    ) {
      LazyColumn(
          verticalArrangement = Arrangement.Top,
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = modifier.fillMaxSize().testTag("LoginScreen2")) {
            item {
              Text(
                  text = "Sign in to start Borrowing.",
                  style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
              )
              Spacer(modifier = Modifier.height(20.dp))
            }
            item {
              LoginOptionButton(
                  modifier = Modifier.testTag("GoogleLoginButton"),
                  name = "Sign in with Google",
                  icon = R.drawable.google,
                  onClick = {
                    Log.w(TAG, "push button to sign in (Google)")
                    authentication.signIn()
                  })
            }
          }
    }
  }
}

/**
 * Custom composable to display a grid of images blurred at the end.
 *
 * @param modifier Modifier to apply to this layout.
 * @param images List of image resources to display.
 * @param onShowSignInChange Callback to change the visibility of the sign in screen.
 */
@Composable
fun ImageGrid(
    modifier: Modifier = Modifier,
    images: List<Int>,
    onShowSignInChange: (Boolean) -> Unit
) {
  Box(modifier = modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        userScrollEnabled = true,
        modifier = Modifier.fillMaxWidth().testTag("ImageGrid")) {
          items(images.size) { index ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
                modifier = Modifier.aspectRatio(1f)) {
                  Image(
                      painter = painterResource(id = images[index]),
                      contentDescription = null,
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxSize())
                }
          }
        }
    Column(
        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).testTag("BlurEffectBox"),
        verticalArrangement = Arrangement.Bottom) {
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .height(170.dp)
                      .align(Alignment.CenterHorizontally)
                      .background(
                          Brush.verticalGradient(
                              colors =
                                  listOf(Color.Transparent, MaterialTheme.colorScheme.background)))
                      .blur(
                          radius = 10.dp,
                      ))
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .height(120.dp)
                      .background(color = MaterialTheme.colorScheme.background))
        }
    ClickableText(
        modifier =
            Modifier.align(Alignment.BottomCenter)
                .padding(bottom = 70.dp)
                .testTag("SeeMoreClickableText"),
        text = AnnotatedString(text = "See 99+ more"),
        style =
            MaterialTheme.typography.headlineSmall.copy(
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.onBackground),
        onClick = {
          Log.w(TAG, "See more clicked")
          onShowSignInChange(true)
        })
  }
}

@Composable
fun LoginOptionButton(modifier: Modifier = Modifier, name: String, icon: Int, onClick: () -> Unit) {
  OutlinedButton(
      onClick = onClick,
      shape = RoundedCornerShape(20.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
      modifier =
          modifier
              .fillMaxWidth()
              .padding(PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp))) {
        Box(modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 5.dp).fillMaxWidth()) {
          Image(
              modifier = Modifier.size(30.dp),
              alignment = Alignment.CenterStart,
              painter = painterResource(id = icon),
              contentDescription = "Google Icon",
          )
          Text(
              text = name,
              color = MaterialTheme.colorScheme.onBackground,
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.align(Alignment.Center))
        }
      }
}
