package com.android.partagix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.StartOrEndLoanViewModel
import com.android.partagix.ui.components.ItemUi
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

@Composable
fun StartLoanScreen(
    startOrEndLoanViewModel: StartOrEndLoanViewModel,
    navigationActions: NavigationActions,
    itemViewModel: ItemViewModel,
    modifier: Modifier = Modifier, // needed for testTags
    manageLoanViewModel: ManageLoanViewModel = ManageLoanViewModel(),
) {
  val uiState by startOrEndLoanViewModel.uiState.collectAsStateWithLifecycle()

  val item = uiState.item
  val loan = uiState.loan
  val lender = uiState.lender

  var open by remember { mutableStateOf(true) }
  if (open) {

    Dialog(
        onDismissRequest = {
          navigationActions.navigateTo(Route.INVENTORY)
          open = false
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)) {
          Surface(
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.fillMaxWidth().testTag("popup")) {
                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(10.dp, 0.dp) // todo maybe
                            .background(MaterialTheme.colorScheme.background)) {
                      Row(
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically,
                          modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Start loan :",
                                fontSize = 20.sp,
                                modifier =
                                    Modifier.padding(
                                            start = 10.dp, end = 26.dp, top = 16.dp, bottom = 16.dp)
                                        .testTag("title"))
                            IconButton(
                                onClick = {
                                  navigationActions.navigateTo(Route.INVENTORY)
                                  open = false
                                },
                                modifier = Modifier.testTag("closeButton")) {
                                  Icon(imageVector = Icons.Default.Close, contentDescription = "")
                                }
                          }

                      Box(modifier = Modifier.height(80.dp)) {
                        ItemUi(
                            item = item,
                            user = lender,
                            loan = loan,
                            manageLoanViewModel = manageLoanViewModel,
                            isLender = true,
                            modifier = Modifier.fillMaxWidth().testTag("item"),
                            onItemClick = {
                              itemViewModel.updateUiItem(item)
                              navigationActions.navigateTo(Route.VIEW_ITEM)
                              open = false
                            },
                            onUserClick = {
                              navigationActions.navigateTo("${Route.OTHER_ACCOUNT}/${lender.id}")
                            },
                            navigationActions = navigationActions)
                      }

                      Row(
                          modifier = Modifier.fillMaxWidth().padding(0.dp, 20.dp, 0.dp, 6.dp),
                          horizontalArrangement = Arrangement.Center) {
                            Button(
                                modifier = Modifier.fillMaxWidth(0.5f).testTag("startButton"),
                                colors =
                                    ButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                                        disabledContentColor = MaterialTheme.colorScheme.onPrimary),
                                onClick = {
                                  startOrEndLoanViewModel.onStart()
                                  navigationActions.navigateTo(Route.INVENTORY)
                                  open = false
                                }) {
                                  Text(text = "Start Loan")
                                }
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                modifier = Modifier.fillMaxWidth().testTag("cancelButton"),
                                colors =
                                    ButtonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError,
                                        disabledContainerColor = MaterialTheme.colorScheme.error,
                                        disabledContentColor = MaterialTheme.colorScheme.onError),
                                onClick = {
                                  startOrEndLoanViewModel.onCancel()
                                  navigationActions.navigateTo(Route.INVENTORY)
                                  open = false
                                }) {
                                  Text(text = "Cancel Loan")
                                }
                          }
                    }
              }
        }
  }
}
