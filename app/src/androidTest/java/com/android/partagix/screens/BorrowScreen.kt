package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class BorrowScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<BorrowScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("borrowScreen") }) {

  // val screenTitle: KNode = onNode { hasTestTag("inventoryScreen") }
  val topBar: KNode = onNode { hasTestTag("topBar") }
  val backText: KNode = topBar.child { hasTestTag("backText") }
  val backButton: KNode = topBar.child { hasTestTag("backButton") }
}
