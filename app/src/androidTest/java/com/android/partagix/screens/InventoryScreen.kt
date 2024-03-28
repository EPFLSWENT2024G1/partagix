package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class InventoryScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<InventoryScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("inventoryScreen") }) {

    //val screenTitle: KNode = onNode { hasTestTag("inventoryScreen") }
}
