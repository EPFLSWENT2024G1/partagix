package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class BorrowScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<BorrowScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("borrowScreen") }) {

  // val screenTitle: KNode = onNode { hasTestTag("inventoryScreen") }
  val topBar: KNode = onNode { hasTestTag("topBar") }
  val backText: KNode = topBar.child { hasTestTag("backText") }
  val backButton: KNode = topBar.child { hasTestTag("backButton") }

  val popup: KNode = onNode { hasTestTag("popup") }
  val itemImage: KNode = onNode { hasTestTag("itemImage") }
  val itemName: KNode = onNode { hasTestTag("itemName") }
  val itemOwner: KNode = onNode { hasTestTag("itemOwner") }
  val description: KNode = onNode { hasTestTag("description") }
  val location: KNode = onNode { hasTestTag("location") }
  val startDate: KNode = onNode { hasTestTag("startDate") }
  val startDateButton: KNode = onNode { hasTestTag("startDateButton") }
  val startDateOk: KNode = onNode { hasTestTag("startDateOk") }
  val startDateCancel: KNode = onNode { hasTestTag("startDateCancel") }
  val endDate: KNode = onNode { hasTestTag("endDate") }
  val endDateButton: KNode = onNode { hasTestTag("endDateButton") }
  val endDateOk: KNode = onNode { hasTestTag("endDateOk") }
  val endDateCancel: KNode = onNode { hasTestTag("endDateCancel") }

  val saveButton: KNode = onNode { hasTestTag("saveButton") }
}
