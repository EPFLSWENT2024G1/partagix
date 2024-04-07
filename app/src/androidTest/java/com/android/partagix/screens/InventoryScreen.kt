package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class InventoryScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<InventoryScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("inventoryScreen") }) {

  // val screenTitle: KNode = onNode { hasTestTag("inventoryScreen") }
  val mainContent: KNode = onNode { hasTestTag("inventoryScreenMainContent") }
  val mainContentText: KNode = onNode { hasTestTag("inventoryScreenMainContentText") }
  val bottomNavBar: KNode = onNode { hasTestTag("inventoryScreenBottomNavBar") }

  val bottomNavBarItemInventory: KNode =
      bottomNavBar.child { hasTestTag("bottomNavBarItem-Inventory") }
}