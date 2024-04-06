package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class HomeScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<HomeScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("homeScreen") }) {

    val mainContent: KNode = onNode { hasTestTag("homeScreenMainContent") }
    val bottomNavBar: KNode = onNode { hasTestTag("homeScreenBottomNavBar") }

    val bottomNavBarItemInventory: KNode =
        bottomNavBar.child { hasTestTag("bottomNavBarItem-Inventory") }
}