package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EvaluationPopUp(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EvaluationPopUp>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("evaluationPopUp") }) {
  val rateText: KNode = onNode { hasTestTag("rateText") }
  val rateStars: KNode = onNode { hasTestTag("rateStars") }
  val commentText: KNode = onNode { hasTestTag("commentText") }
  val commentField: KNode = onNode { hasTestTag("commentField") }
  val closeButton: KNode = onNode { hasTestTag("closeButton") }
  val evaluateButton: KNode = onNode { hasTestTag("evaluationButton") }
}
