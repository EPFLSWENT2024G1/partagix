package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class StampScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<StampScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = {
          hasTestTag("stampScreen") // scaffold
        }) {
  val stampScreen: KNode = child { hasTestTag("stampScreen") }
  val topAppBar: KNode = child { hasTestTag("topAppBar") }
  val title: KNode = child { hasTestTag("title") }
  val backButton: KNode = child { hasTestTag("backButton") }
  val mainContent: KNode = child { hasTestTag("mainContent") }
  val dimensionLabel: KNode = child { hasTestTag("dimensionLabel") }
  val dimensionBox: KNode = child { hasTestTag("dimensionBox") }
  val labelLabel: KNode = child { hasTestTag("labelLabel") }
  val labelTextField: KNode = child { hasTestTag("labelTextField") }
  val downloadRow: KNode = child { hasTestTag("downloadRow") }
  val downloadButton: KNode = child { hasTestTag("downloadButton") }
}
