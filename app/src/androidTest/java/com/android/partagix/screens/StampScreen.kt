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
  val stampScreen: KNode = onNode { hasTestTag("stampScreen") }
  val topAppBar: KNode = child { hasTestTag("topAppBar") }
  val title: KNode = child { hasTestTag("title") }
  val backButton: KNode = child { hasTestTag("backButton") }
  val bottomBar: KNode = child { hasTestTag("bottomBar") }
  val mainContent: KNode = child { hasTestTag("mainContent") }
  val dimensionBox: KNode = child { hasTestTag("dimensionBox") }
  val labelTextField: KNode = child { hasTestTag("labelTextField") }
  val downloadRow: KNode = child { hasTestTag("downloadRow") }
  val downloadButton: KNode = child { hasTestTag("downloadButton") }
}
