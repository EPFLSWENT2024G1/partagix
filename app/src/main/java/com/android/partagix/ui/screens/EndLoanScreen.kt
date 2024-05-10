package com.android.partagix.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.StartOrEndLoanViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.ItemUi
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndLoanScreen(
    startOrEndLoanViewModel: StartOrEndLoanViewModel,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier
) {
  val uiState by startOrEndLoanViewModel.uiState.collectAsStateWithLifecycle()

  val item = uiState.item
  val loan = uiState.loan
  val borrower = uiState.borrower
  val lender = uiState.lender

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
            modifier = Modifier.fillMaxWidth().testTag("topBar"),
            title = { Text("Start Loan", modifier = Modifier.fillMaxWidth().testTag("title")) },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("backButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.width(48.dp))
                  }
            })
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.HOME,
            navigateToTopLevelDestination = navigationActions::navigateTo,
        )
      }) {
        Column(
            modifier =
                Modifier.fillMaxHeight()
                    .padding(it) // Adjust padding as needed
                    .fillMaxWidth(), // Make the column fill the entire width
            verticalArrangement = Arrangement.Center, // Center items vertically
            horizontalAlignment = Alignment.CenterHorizontally // Center items horizontally
            ) {
              ItemUi(
                  item,
                  borrower,
                  loan,
              )
              Row {
                Button(
                    onClick = { startOrEndLoanViewModel.onFinish() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    modifier =
                        Modifier.weight(1f)
                            .testTag("endLoanButton") // Expand to fill available space
                    ) {
                      Text(text = "End Loan", color = Color.Black)
                    }
              }
            }
      }
}
