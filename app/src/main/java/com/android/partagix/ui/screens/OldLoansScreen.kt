package com.android.partagix.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.android.partagix.model.EvaluationViewModel
import com.android.partagix.model.FinishedLoansViewModel
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.emptyConst.emptyLoan
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.EvaluationPopUp
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OldLoansScreen(
    modifier: Modifier = Modifier,
    navigationActions: NavigationActions,
    itemViewModel: ItemViewModel,
    evaluationViewModel: EvaluationViewModel,
    finishedLoansViewModel: FinishedLoansViewModel
) {
  val uiState by finishedLoansViewModel.uiState.collectAsStateWithLifecycle()
  var open by remember { mutableStateOf(false) }
  var actualLoan by remember { mutableStateOf(emptyLoan) }

  Scaffold(
      modifier = modifier.testTag("oldLoansScreen"),
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("homeScreenTopAppBar"),
            title = { Text(text = "Loan History") },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("navigationIcon")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null)
                  }
            })
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.HOME,
            navigateToTopLevelDestination = { dest -> navigationActions.navigateTo(dest) },
            modifier = modifier)
      }) { innerPadding ->
        if (uiState.loans.isEmpty()) {
          Column(modifier = modifier.fillMaxSize().padding(innerPadding).testTag("emptyOldLoans")) {
            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            Box(modifier = modifier.padding(innerPadding).fillMaxSize()) {
              Text(text = "You have no finished loans", modifier = modifier.align(Alignment.Center))
            }
          }
        } else {
          Column(
              modifier =
                  modifier
                      .fillMaxWidth()
                      .padding(innerPadding)
                      .verticalScroll(rememberScrollState())) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.outlineVariant)

                if (open) {
                  EvaluationPopUp(
                      loan = actualLoan,
                      userId = Authentication.getUser()?.uid ?: "",
                      viewModel = evaluationViewModel,
                      onClose = { l ->
                        open = false
                        finishedLoansViewModel.updateLoan(l)
                      })
                }
                for (loan in uiState.loans) {
                  ExpandableCard(
                      modifier = modifier.testTag("expandableCard"),
                      loan = loan.first,
                      item = loan.second,
                      navigationActions = navigationActions,
                      itemViewModel = itemViewModel,
                      evaluationViewModel = evaluationViewModel,
                      setOpen = {
                        actualLoan = loan.first
                        open = it
                      })
                }
              }
        }
      }
}

/**
 * ExpandableCard is a composable function that displays a card that can be expanded to show more
 * information.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param loan Loan to be displayed.
 * @param item Item to be displayed.
 * @param navigationActions NavigationActions to handle navigation.
 * @param itemViewModel ItemViewModel to handle the item.
 * @param evaluationViewModel EvaluationViewModel to handle the evaluation.
 * @param setOpen Function to be called when the card is expanded.
 */
@Composable
fun ExpandableCard(
    modifier: Modifier = Modifier,
    loan: Loan,
    item: Item,
    navigationActions: NavigationActions,
    itemViewModel: ItemViewModel,
    evaluationViewModel: EvaluationViewModel,
    setOpen: (Boolean) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }
  OutlinedCard(
      modifier =
          modifier.fillMaxWidth().padding(16.dp).animateContentSize().clickable {
            expanded = !expanded
          }, // This enables the expansion animation
      colors = CardDefaults.outlinedCardColors()) {
        Column(
            modifier =
                Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 10.dp)
                    .fillMaxWidth()
                    .testTag("card")) {
              Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().testTag("nameAndDate"),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                      Column(modifier = Modifier.fillMaxWidth(0.65f)) {
                        Text(
                            text = item.name,
                            fontSize = 20.sp,
                        )
                        Spacer(modifier.height(8.dp))
                        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
                        Text(
                            text = "Start Date : ${dateFormatter.format(loan.startDate)}",
                            fontSize = 15.sp)
                        Spacer(modifier.height(2.dp))
                        Text(
                            text = "End Date : ${dateFormatter.format(loan.endDate)}",
                            fontSize = 15.sp)
                      }
                      Box(
                          contentAlignment = Alignment.Center,
                          modifier = modifier.fillMaxWidth().aspectRatio(1f)) {
                            AsyncImage(
                                model = item.imageId.absolutePath,
                                contentDescription = "fds",
                                contentScale = ContentScale.FillWidth,
                                modifier =
                                    Modifier.border(1.dp, MaterialTheme.colorScheme.onBackground)
                                        .testTag("image"),
                                alignment = Alignment.Center)
                          }
                    }
              }
              Spacer(modifier.height(3.dp))

              Row(modifier = modifier.fillMaxWidth()) {
                var user by remember { mutableStateOf("Unknown") }
                if (item.idUser == Authentication.getUser()?.uid) {

                  evaluationViewModel.getUser(loan.idBorrower, { u -> user = u.name }, {})
                  Text(
                      text = "Borrower : $user",
                      fontSize = 15.sp,
                  )
                } else {
                  evaluationViewModel.getUser(loan.idLender, { u -> user = u.name }, {})
                  Text(
                      text = "Lender : $user",
                      fontSize = 15.sp,
                  )
                }
              }

              if (expanded) {
                Spacer(modifier = modifier.height(6.dp))
                Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                  IconButton(
                      modifier = modifier.fillMaxWidth(0.3f).testTag("infoButton"),
                      colors =
                          IconButtonColors(
                              containerColor = MaterialTheme.colorScheme.secondary,
                              contentColor = MaterialTheme.colorScheme.onSecondary,
                              disabledContentColor = MaterialTheme.colorScheme.onSecondary,
                              disabledContainerColor = MaterialTheme.colorScheme.secondary),
                      onClick = {
                        expanded = false
                        itemViewModel.updateUiItem(item)
                        navigationActions.navigateTo(Route.VIEW_ITEM)
                      }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Icon(imageVector = Icons.Default.Info, contentDescription = "info")
                          Spacer(modifier = modifier.width(5.dp))
                          Text("Infos")
                          Spacer(modifier = modifier.width(2.dp))
                        }
                      }
                  Spacer(modifier = modifier.width(10.dp))

                  IconButton(
                      modifier = modifier.fillMaxWidth(0.6f),
                      colors =
                          IconButtonColors(
                              containerColor = MaterialTheme.colorScheme.primary,
                              contentColor = MaterialTheme.colorScheme.onPrimary,
                              disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                              disabledContainerColor = MaterialTheme.colorScheme.primary),
                      onClick = {
                        evaluationViewModel.updateUIState(loan)
                        setOpen(true)
                        expanded = false
                      }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Icon(imageVector = Icons.Default.Star, contentDescription = "rate")
                          Spacer(modifier = modifier.width(5.dp))
                          Text("Evaluate")
                          Spacer(modifier = modifier.width(2.dp))
                        }
                      }
                  Spacer(modifier = modifier.fillMaxWidth())
                }
              }
            }
      }
}
