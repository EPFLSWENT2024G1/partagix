package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class LoginScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LoginScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("LoginScreen") }) {

  val banner: KNode = onNode { hasTestTag("Banner") }
  val centerContent: KNode = onNode { hasTestTag("CenterContent") }
  val startBorrowButton: KNode = onNode { hasTestTag("StartBorrowButton") }
  val loginBottomSheet: KNode = onNode { hasTestTag("LoginBottomSheet") }
  val imageGrid: KNode = onNode { hasTestTag("ImageGrid") }
  val blurEffectBox: KNode = onNode { hasTestTag("BlurEffectBox") }
  val seeMoreClickableText: KNode = onNode { hasTestTag("SeeMoreClickableText") }
  val googleLoginButton: KNode = onNode { hasTestTag("GoogleLoginButton") }
  val otherLoginButton: KNode = onNode { hasTestTag("OtherLoginButton") }
}
