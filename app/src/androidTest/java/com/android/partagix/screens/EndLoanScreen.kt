package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EndLoanScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EndLoanScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("StartLoanScreen") }) {

  val item: KNode = onNode { hasTestTag("item") }
  val endLoanButton: KNode = onNode { hasTestTag("endLoanButton") }
  val title: KNode = onNode { hasTestTag("title") }
  val close: KNode = onNode { hasTestTag("closeButton") }
  val popUp: KNode = onNode { hasTestTag("popup") }
}
