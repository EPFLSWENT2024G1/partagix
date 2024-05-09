package com.android.partagix.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun oldLoansScreen(
    modifier: Modifier = Modifier /*, finishedLoansViewModel: FinishedLoansViewModel*/
) {

  Scaffold(
      modifier = modifier,
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("homeScreenTopAppBar"),
            title = { Text(text = "Partagix") },
        )
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.HOME,
            navigateToTopLevelDestination = { /*navigationActions::navigateTo*/},
            modifier = modifier)
      }) { innerPadding ->
        if (/*TODO: get all loan of someone*/ true) {
          Column(modifier = modifier.fillMaxSize().padding(innerPadding)) {
            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            Box(modifier = modifier.padding(innerPadding).fillMaxSize()) {
              Text(text = "You have no finished loans", modifier = modifier.align(Alignment.Center))
            }
          }
        } else {}
      }
}
