package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class LoginScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LoginScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("LoginScreen") }) {

  val banner: KNode = child { hasTestTag("Banner") }
  val centerContent: KNode = child { hasTestTag("CenterContent") }
  val startBorrowButton: KNode = child { hasTestTag("StartBorrowButton") }
  val loginBottomSheet: KNode = child { hasTestTag("LoginBottomSheet") }
  val imageGrid: KNode = child { hasTestTag("ImageGrid") }
  val blurEffectBox: KNode = child { hasTestTag("BlurEffectBox") }
  val seeMoreClickableText: KNode = child { hasTestTag("SeeMoreClickableText") }
  val googleLoginButton: KNode = child { hasTestTag("GoogleLoginButton") }
}
