package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ManageLoanScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ManageLoanScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("manageLoanScreen") }) {

  val noItemBox: KNode = onNode { hasTestTag("manageScreenNoItemBox") }
  val noItemText: KNode = onNode { hasTestTag("manageScreenNoItemText") }
  val itemList: KNode = onNode { hasTestTag("manageLoanScreenItemListColumn") }
  val itemCard: KNode = itemList.child { hasTestTag("manageLoanScreenItemCard") }
  val itemCardExpanded: KNode = onNode { hasTestTag("manageLoanScreenItemCardExpanded") }

  val topBar: KNode = onNode { hasTestTag("manageLoanScreenTopBar") }
  val bottomNavBar: KNode = onNode { hasTestTag("manageScreenBottomNavBar") }
  val bottomNavBarItemInventory: KNode = bottomNavBar.child { hasTestTag("bottomNavBarItem-Home") }
}
