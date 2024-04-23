package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class LoginScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LoginScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("LoginScreen") }) {

  val loginTitle: KNode = child { hasTestTag("LoginTitle") }
  val popUpLoginButton: KNode = child { hasTestTag("PopUpLoginButton") }
  val loginButtonOpenBottomSheet: KNode = child { hasTestTag("LoginButtonOpenBottomSheet") }
  val loginBottomSheet: KNode = child { hasTestTag("LoginBottomSheet") }
  val loginButton: KNode = child { hasTestTag("LoginButton") }
}

class LoginScreen2(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LoginScreen2>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("LoginScreen2") }) {

  val loginTitle: KNode = child { hasTestTag("LoginTitle") }
  val popUpLoginButton: KNode = child { hasTestTag("PopUpLoginButton") }
  val loginButtonOpenBottomSheet: KNode = child { hasTestTag("LoginButtonOpenBottomSheet") }
  val loginBottomSheet: KNode = child { hasTestTag("LoginBottomSheet") }
  val loginButton: KNode = child { hasTestTag("LoginButton") }
}
