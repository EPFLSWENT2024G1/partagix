package com.android.partagix.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EditAccount(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EditAccount>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("editAccount") }) {

  val topBar: KNode = child { hasTestTag("topBar") }
  val backButton: KNode = child { hasTestTag("backButton") }
  val bottomNavBar: KNode = child { hasTestTag("bottomNavBar") }
  val notYourAccount: KNode = child { hasTestTag("notYourAccount") }
  val mainContent: KNode = child { hasTestTag("mainContent") }
  val userImage: KNode = child { hasTestTag("userImage") }
  val usernameField: KNode = child { hasTestTag("usernameField") }
  val addressField: KNode = child { hasTestTag("addressField") }
  val actionButtons: KNode = child { hasTestTag("actionButtons") }
  val saveButton: KNode = child { hasTestTag("saveButton") }
  val email: KNode = child { hasTestTag("email") }
  val phoneNumber: KNode = child { hasTestTag("phoneNumber") }
  val telegram: KNode = child { hasTestTag("telegram") }
  val contactInfo: KNode = child { hasTestTag("contactInfo") }
  val emailCheckBox: KNode = child { hasTestTag("emailCheckbox") }
  val phoneNumberCheckBox: KNode = child { hasTestTag("phoneNumberCheckbox") }
  val telegramCheckBox: KNode = child { hasTestTag("telegramCheckbox") }
}
