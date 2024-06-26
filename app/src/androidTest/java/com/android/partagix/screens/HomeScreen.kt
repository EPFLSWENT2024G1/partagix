package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class HomeScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<HomeScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("homeScreen") }) {

  val topBar: KNode = onNode { hasTestTag("homeScreenTopAppBar") }
  val bottomNavBar: KNode = onNode { hasTestTag("homeScreenBottomNavBar") }

  val bottomNavBarItemInventory: KNode =
      bottomNavBar.child { hasTestTag("bottomNavBarItem-Inventory") }

  val mainContent: KNode = onNode { hasTestTag("homeScreenMainContent") }

  val firstBigButton: KNode = onNode { hasTestTag("homeScreenFirstBigButton") }
  val secondBigButton: KNode = onNode { hasTestTag("homeScreenSecondBigButton") }
  val thirdBigButton: KNode = onNode { hasTestTag("homeScreenThirdBigButton") }

  val itemList: KNode = onNode { hasTestTag("homeScreenItemList") }
}
