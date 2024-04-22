package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class InventoryViewItem(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<InventoryViewItem>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("inventoryViewItem") }) {

  val topBar: KNode = onNode { hasTestTag("inventoryViewItemTopBar") }
  val bottomBar: KNode = onNode { hasTestTag("inventoryViewItemBottomBar") }
}
