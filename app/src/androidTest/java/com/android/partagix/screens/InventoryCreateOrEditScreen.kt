package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class InventoryCreateOrEditScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<InventoryCreateOrEditScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("inventoryCreateItem") }) {

  // val screenTitle: KNode = onNode { hasTestTag("inventoryScreen") }
  val topBar: KNode = onNode { hasTestTag("topBar") }
  val title: KNode = topBar.child { hasTestTag("title") }
  val navigationIcon: KNode = topBar.child { hasTestTag("navigationIcon") }

  val image: KNode = onNode { hasTestTag("image") }
  val name: KNode = onNode { hasTestTag("name") }
  val idUser: KNode = onNode { hasTestTag("idUser") }
  val description: KNode = onNode { hasTestTag("description") }
  val category: KNode = onNode { hasTestTag("category") }
  val visibility: KNode = onNode { hasTestTag("visibility") }
  val quantity: KNode = onNode { hasTestTag("quantity") }
}
