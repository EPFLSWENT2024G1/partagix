package com.android.partagix.ui.screens

import android.location.Location
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.CategoryItems
import com.android.partagix.ui.components.ItemListColumn
import com.android.partagix.ui.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun oldLoansScreen(modifier: Modifier = Modifier, manageLoanViewModel: ManageLoanViewModel) {
    val uiState by manageLoanViewModel.uiState.collectAsStateWithLifecycle()
    Scaffold (
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
                navigateToTopLevelDestination = {/*navigationActions::navigateTo*/},
                modifier = modifier)
        }) { innerPadding ->
        if (/*TODO: get all loan of someone*/ true) {
            Column(modifier = modifier.fillMaxSize().padding(innerPadding)) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())

                Box(
                    modifier =
                    modifier.padding(innerPadding).fillMaxSize()) {
                    Text(
                        text = "You have no finished loans",
                        modifier = modifier.align(Alignment.Center))
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier =
                modifier.fillMaxSize().padding(innerPadding)) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Color(0xffcac4d0))

                ItemListColumn(
                    list = uiState.items,
                    users = uiState.users,
                    loan = uiState.loans,
                    title = "Old requests",
                    corner = uiState.items.size.toString(),
                    isCornerClickable = false,
                    isExpandable = true,
                    expandState = /*expandables*/ false,
                    wasExpanded = uiState.expanded,
                    onClick = { /* isnt usefull for this column */},
                    onClickCorner = { /* isnt usefull for this column */},
                    manageLoanViewModel = manageLoanViewModel,
                    isClickable = true,
                    modifier = modifier)
            }
        }

    }
}
