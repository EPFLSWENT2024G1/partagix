package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class InventoryViewItemScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<InventoryViewItemScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("inventoryViewItem") }) {

  val topBar: KNode = onNode { hasTestTag("inventoryViewItemTopBar") }
  val bottomBar: KNode = onNode { hasTestTag("inventoryViewItemBottomBar") }
  val ownerField: KNode = onNode { hasTestTag("ownerField") }
}
