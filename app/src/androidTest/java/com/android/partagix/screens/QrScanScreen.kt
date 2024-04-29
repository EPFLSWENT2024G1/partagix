package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class QrScanScreen(semanticsProvider: SemanticsNodeInteractionsProvider):
    ComposeScreen<QrScanScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("inventoryViewItem") }) {

    val text: KNode = onNode { hasTestTag("TODO tag") }
    val backButton: KNode = onNode { hasTestTag("backButton") }
}
