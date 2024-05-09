package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class StartLoanScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<StartLoanScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("StartLoanScreen") }) {

  val item: KNode = onNode { hasTestTag("ItemUiNotExpanded") }
  val startButton : KNode = onNode { hasTestTag("startButton") }
  val cancelButton : KNode = onNode { hasTestTag("cancelButton") }
}
