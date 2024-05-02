package com.android.partagix.components

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class LabeledText(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LabeledText>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("labeledText") }) {

  val labeledText: KNode = onNode { hasTestTag("labeledText") }
  val mainColumn: KNode = child { hasTestTag("mainColumn") }
  val label: KNode = child { hasTestTag("label") }
  val text: KNode = child { hasTestTag("text") }
}
