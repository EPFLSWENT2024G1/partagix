package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class LoanScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LoanScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("LoanScreen") }) {

  val searchBar: KNode = onNode { hasTestTag("LoanScreenSearchBar") }
  val searchBarInput: KNode = onNode { hasTestTag("SearchField") }

  val maps: KNode = onNode { hasTestTag("LoanScreenMaps") }

  val distanceFilter: KNode = onNode { hasTestTag("LoanScreenDistanceFilter") }
  val qtyFilter: KNode = onNode { hasTestTag("LoanScreenQtyFilter") }

  val itemListView: KNode = onNode { hasTestTag("LoanScreenItemListView") }
  val itemListViewItem: KNode = itemListView.child { hasTestTag("ItemListItem") }

  val bottomNavBar: KNode = onNode { hasTestTag("LoanScreenBottomNavBar") }
  val bottomNavBarItemInventory: KNode = bottomNavBar.child { hasTestTag("bottomNavBarItem-Loan") }
}
