package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class NavigationBar(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<NavigationBar>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("navigationBar") }) {

  val homeButton: KNode = onNode { hasTestTag("bottomNavBarItem-Home") }
  val loanButton: KNode = onNode { hasTestTag("bottomNavBarItem-Loan") }
  val inventoryButton: KNode = onNode { hasTestTag("bottomNavBarItem-Inventory") }
  val accountButton: KNode = onNode { hasTestTag("bottomNavBarItem-Account") }
}
