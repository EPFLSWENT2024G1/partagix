package com.android.partagix.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class BootScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<BootScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("BootScreen") }) {

  val bootLogo: KNode = onNode { hasTestTag("BootLogo") }
}
