package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ViewAccount(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ViewAccount>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = {
          hasTestTag("viewAccount") // scaffold
        }) {

  val title: KNode = child { hasTestTag("title") } // topappbar
  val mainContent: KNode = child { hasTestTag("mainContent") } // column
  val userImage: KNode = child { hasTestTag("userImage") } // image
  val username: KNode = child { hasTestTag("username") } // row
  val location: KNode = child { hasTestTag("location") } // textfield
  val rating: KNode = child { hasTestTag("rating") } // textfield
  val buttons: KNode = child { hasTestTag("actionButtons") } // row
  val inventoryButton: KNode = child { hasTestTag("inventoryButton") } // button
  val friendButton: KNode = child { hasTestTag("friendButton") } // button
}
