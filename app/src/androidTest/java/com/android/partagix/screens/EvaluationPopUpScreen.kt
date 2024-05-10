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
  val validateButton: KNode = onNode { hasTestTag("validateButton") }
  val commentText: KNode = onNode { hasTestTag("commentText") }
  val commentField: KNode = onNode { hasTestTag("commentField") }
  val commentButton: KNode = onNode { hasTestTag("commentButton") }
  val closeButton: KNode = onNode { hasTestTag("closeButton") }
}
