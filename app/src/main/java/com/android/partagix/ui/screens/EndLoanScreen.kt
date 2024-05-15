package com.android.partagix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.StartOrEndLoanViewModel
import com.android.partagix.ui.components.ItemUi
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

@Composable
fun EndLoanScreen(
    startOrEndLoanViewModel: StartOrEndLoanViewModel,
    navigationActions: NavigationActions,
    itemViewModel: ItemViewModel,
    modifier: Modifier = Modifier
) {
  val uiState by startOrEndLoanViewModel.uiState.collectAsStateWithLifecycle()

  val item = uiState.item
  val loan = uiState.loan
  val borrower = uiState.borrower
  val lender = uiState.lender

  var open by remember { mutableStateOf(true) }
  if (open) {

    Dialog(
        onDismissRequest = { open = false /*TODO: other things?*/ },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)) {
          Surface(shape = RoundedCornerShape(16.dp), modifier = modifier.fillMaxWidth()) {
            Column(
                modifier =
                    modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                  Row(
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = modifier.fillMaxWidth()) {
                        Text(
                            text = "End loan :",
                            fontSize = 20.sp,
                            modifier =
                                modifier.padding(
                                    start = 10.dp, end = 26.dp, top = 16.dp, bottom = 16.dp))
                        IconButton(onClick = {}) {
                          Icon(imageVector = Icons.Default.Close, contentDescription = "")
                        }
                      }
                  Column(modifier = modifier.padding(8.dp, 0.dp, 8.dp, 8.dp).fillMaxWidth()) {
                    ItemUi(item = item, user = lender, loan = loan)
                    Spacer(modifier = modifier.width(8.dp))
                    Row(
                        modifier = modifier.fillMaxWidth().padding(0.dp, 35.dp, 0.dp, 6.dp),
                        horizontalArrangement = Arrangement.Center) {
                          Button(
                              modifier = modifier.fillMaxWidth(0.5f),
                              colors =
                                  ButtonColors(
                                      containerColor = MaterialTheme.colorScheme.primary,
                                      contentColor = MaterialTheme.colorScheme.onPrimary,
                                      disabledContainerColor = MaterialTheme.colorScheme.primary,
                                      disabledContentColor = MaterialTheme.colorScheme.onPrimary),
                              onClick = {
                                itemViewModel.updateUiItem(item)
                                navigationActions.navigateTo(Route.VIEW_ITEM)
                              }) {
                                Text(text = "See Item")
                              }
                          Spacer(modifier = modifier.width(10.dp))
                          Button(
                              modifier = modifier.fillMaxWidth(),
                              colors =
                                  ButtonColors(
                                      containerColor = MaterialTheme.colorScheme.error,
                                      contentColor = MaterialTheme.colorScheme.onError,
                                      disabledContainerColor = MaterialTheme.colorScheme.error,
                                      disabledContentColor = MaterialTheme.colorScheme.onError),
                              onClick = { startOrEndLoanViewModel.onFinish() }) {
                                Text(text = "End Loan")
                              }
                        }
                  }
                }
          }
        }
  }
}
