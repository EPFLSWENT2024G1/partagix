package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ManageOutgoingLoanScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ManageOutgoingLoanScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("manageLoanScreen") }) {

  val noLoanBox: KNode = onNode { hasTestTag("manageOutgoingScreenNoItemBox") }
  val noItemText: KNode = onNode { hasTestTag("manageScreenNoItemText") }
  val itemList: KNode = onNode { hasTestTag("manageLoanScreenItemListColumn") }
  val itemCard: KNode = itemList.child { hasTestTag("manageLoanScreenItemCard") }

  val topBar: KNode = onNode { hasTestTag("manageLoanScreenTopBar") }
  val bottomNavBar: KNode = onNode { hasTestTag("manageScreenBottomNavBar") }
  val bottomNavBarItemInventory: KNode = bottomNavBar.child { hasTestTag("bottomNavBarItem-Home") }
}
