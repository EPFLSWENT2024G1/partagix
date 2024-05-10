package com.android.partagix.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.R
import com.android.partagix.model.BorrowViewModel
import com.android.partagix.ui.navigation.NavigationActions
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowScreen(
    viewModel: BorrowViewModel,
    modifier: Modifier = Modifier,
    navigationActions: NavigationActions
) {
  Scaffold(
      modifier = modifier.testTag("borrowScreen").fillMaxWidth(),
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("topBar"),
            title = { Text("Back", modifier = Modifier.testTag("backText")) },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("backButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null)
                  }
            })
      }
      // Bottom navbar
      ) {
        val loanUiState = viewModel.loanUiState.collectAsStateWithLifecycle()
        val itemUIState = viewModel.itemUiState.collectAsStateWithLifecycle()
        val loan = loanUiState.value
        val item = itemUIState.value

        var loanItemName by remember { mutableStateOf(item.name) }
        val loanItemOwner by remember { mutableStateOf(item.idUser) }
        var loanDescription by remember {
          mutableStateOf("")
        } // TODO: edit Loan type to include description
        val loanLocation by remember { mutableStateOf(item.location) }
        val loanStartDate by remember { mutableStateOf(loan.startDate) }
        val loanEndDate by remember { mutableStateOf(loan.endDate) }
        var loanQuantity by remember {
          mutableIntStateOf(1)
        } // TODO: edit Loan type to include quantity

        Column(
            modifier.padding(it).fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Box(modifier = modifier.fillMaxWidth().height(140.dp).padding(8.dp)) {
                Row(modifier = modifier.fillMaxWidth()) {
                  Box(
                      contentAlignment = Alignment.Center,
                      modifier = modifier.fillMaxHeight().fillMaxWidth(.4f).testTag("itemImage")) {
                        Image(
                            painter =
                                painterResource(
                                    id =
                                        R.drawable
                                            .ic_launcher_background), // TODO replace with actual
                            // image
                            contentDescription = "Item image",
                            modifier = modifier.fillMaxSize())
                      }

                  Spacer(modifier = modifier.width(8.dp))

                  Column {
                    OutlinedTextField(
                        value = loanItemName,
                        onValueChange = {},
                        label = { Text("Item name") },
                        modifier = modifier.testTag("itemName").fillMaxWidth(),
                        maxLines = 1, // Ensure only one line is displayed
                        readOnly = true)
                    OutlinedTextField(
                        value = loanItemOwner, // TODO: display owner name instead of id
                        onValueChange = {},
                        label = { Text("Owner") },
                        modifier = modifier.testTag("itemOwner").fillMaxWidth(),
                        readOnly = true)
                  }
                }
              }
              Column(modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                OutlinedTextField(
                    value = loanDescription,
                    onValueChange = { loanDescription = it },
                    label = { Text("Description") },
                    modifier = modifier.fillMaxWidth(),
                    minLines = 5,
                    readOnly = false)

                Spacer(modifier = modifier.height(8.dp))

                OutlinedTextField(
                    value = loanLocation.toString(),
                    onValueChange = {},
                    label = { Text("Location") },
                    modifier = modifier.fillMaxWidth(),
                    readOnly = true)

                Spacer(modifier = modifier.height(8.dp))

                OutlinedTextField(
                    modifier = modifier.fillMaxWidth(),
                    value = loanStartDate.toString(), // TODO: use Date instead of String
                    label = { Text("Start date") },
                    onValueChange = { viewModel.updateLoan(loan.copy(startDate = Date(it))) },
                    readOnly = true,
                    suffix = {
                      IconButton(
                          modifier = Modifier.height(30.dp).padding(0.dp),
                          onClick = { /*TODO: see calendar with disponibilities*/},
                          content = { Icon(Icons.Default.DateRange, contentDescription = null) })
                    })

                Spacer(modifier = modifier.height(8.dp))

                OutlinedTextField(
                    modifier = modifier.fillMaxWidth(),
                    value = loanEndDate.toString(), // TODO: use Date instead of String
                    label = { Text("End date") },
                    onValueChange = { viewModel.updateLoan(loan.copy(endDate = Date(it))) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    readOnly = true,
                    suffix = {
                      IconButton(
                          modifier = Modifier.height(30.dp).padding(0.dp),
                          onClick = { /*TODO: see calendar with disponibilities*/},
                          content = { Icon(Icons.Default.DateRange, contentDescription = null) })
                    })

                Spacer(modifier = modifier.height(8.dp))

                OutlinedTextField(
                    value = loanQuantity.toString(),
                    onValueChange = { str ->
                      val intValue: Int? = str.toIntOrNull()
                      loanQuantity = intValue ?: 0
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Quantity") },
                    modifier = modifier.testTag("itemQuantity").fillMaxWidth(),
                    readOnly = false)
              }

              Button(
                  modifier = modifier.fillMaxWidth().testTag("button").padding(10.dp),
                  onClick = {
                    viewModel.createLoan()
                    navigationActions.goBack()
                  },
                  content = { Text(text = "Make request reservation") })
            }
      }
}
