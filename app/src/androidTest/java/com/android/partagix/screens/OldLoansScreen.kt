package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class OldLoansScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<OldLoansScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("oldLoansScreen") }) {
  val noOldLoans: KNode = onNode { hasTestTag("emptyOldLoans") }
  val expandableCard: KNode = onNode { hasTestTag("expandableCard") }
  val evaluationPopUp: KNode = onNode { hasTestTag("evaluationPopUp") }
}
