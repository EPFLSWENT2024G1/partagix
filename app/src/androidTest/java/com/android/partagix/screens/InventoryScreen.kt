package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class InventoryScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<InventoryScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("inventoryScreen") }) {

  // val screenTitle: KNode = onNode { hasTestTag("inventoryScreen") }
  val searchBar: KNode = onNode { hasTestTag("inventoryScreenSearchBar") }
  val searchBarBackIcon: KNode = searchBar.child { hasTestTag("inventoryScreenSearchBarBack") }
  val searchBarSearchIcon: KNode = searchBar.child { hasTestTag("inventoryScreenSearchBarSearch") }
  // val mainContentText: KNode = onNode { hasTestTag("inventoryScreenMainContentText") }

  val fab: KNode = onNode { hasTestTag("inventoryScreenFab") }
  val noItemBox: KNode = onNode { hasTestTag("inventoryScreenNoItemBox") }
  val noItemText: KNode = onNode { hasTestTag("inventoryScreenNoItemText") }
  val itemList: KNode = onNode { hasTestTag("inventoryScreenItemList") }

  val bottomNavBar: KNode = onNode { hasTestTag("inventoryScreenBottomNavBar") }
  val bottomNavBarItemInventory: KNode =
      bottomNavBar.child { hasTestTag("bottomNavBarItem-Inventory") }
}
