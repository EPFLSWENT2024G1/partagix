package com.android.partagix.components

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class RankingStars(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<RankingStars>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("rankingStars") }) {

  val stars_0: KNode = onNode { hasTestTag("stars_0") }
  val stars_0_5: KNode = onNode { hasTestTag("stars_0_5") }
  val stars_1: KNode = onNode { hasTestTag("stars_1") }
  val stars_1_5: KNode = onNode { hasTestTag("stars_1_5") }
  val stars_2: KNode = onNode { hasTestTag("stars_2") }
  val stars_2_5: KNode = onNode { hasTestTag("stars_2_5") }
  val stars_3: KNode = onNode { hasTestTag("stars_3") }
  val stars_3_5: KNode = onNode { hasTestTag("stars_3_5") }
  val stars_4: KNode = onNode { hasTestTag("stars_4") }
  val stars_4_5: KNode = onNode { hasTestTag("stars_4_5") }
  val stars_5: KNode = onNode { hasTestTag("stars_5") }
  val stars_wrong_rating: KNode = onNode { hasTestTag("stars_wrong_rating") }
  val stars_empty: KNode = onNode { hasTestTag("stars_empty") }
}
