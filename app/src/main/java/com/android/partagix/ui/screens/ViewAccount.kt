package com.android.partagix.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.partagix.R
import com.android.partagix.model.UserViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import kotlin.math.round

// @Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAccount(
    modifier: Modifier = Modifier,
    navigationActions: NavigationActions,
    userViewModel: UserViewModel,
) {
  val uiState = userViewModel.uiState.collectAsState()
  val user = uiState.value.user
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("viewAccount"),
      topBar = {
        TopAppBar(
            title = { Text("My Account") },
            modifier = Modifier.fillMaxWidth().testTag("title"),
            navigationIcon = {
              IconButton(onClick = { navigationActions.goBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.width(48.dp))
              }
            })
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.ACCOUNT,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = modifier.testTag("accountScreenBottomNavBar"))
      }) {
        Column(
            modifier =
                Modifier.fillMaxHeight()
                    .padding(it)
                    .verticalScroll(rememberScrollState())
                    .testTag("mainContent")) {
              Image(
                  painter =
                      painterResource(
                          id = R.drawable.ic_launcher_background) /*TODO: get profile picture*/,
                  contentDescription = null,
                  modifier = Modifier.fillMaxWidth().testTag("userImage"),
                  alignment = Alignment.Center)
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                  modifier = Modifier.fillMaxWidth().testTag("username"),
                  horizontalArrangement = Arrangement.Absolute.SpaceAround) {
                    val username = user.name
                    Text("$username's profile")
                  }
              Spacer(modifier = Modifier.height(16.dp))
              TextField(
                  value = user.address,
                  onValueChange = {},
                  label = { Text("Location") },
                  colors =
                      TextFieldDefaults.colors(
                          focusedIndicatorColor = Color.Transparent,
                          disabledIndicatorColor = Color.Transparent,
                          unfocusedIndicatorColor = Color.Transparent,
                          focusedContainerColor = Color.Transparent,
                          unfocusedContainerColor = Color.Transparent,
                          disabledContainerColor = Color.Transparent),
                  modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("location"),
                  readOnly = true,
                  leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) })
              val rank = user.rank
              val stars: String
              if (rank == "") {
                stars = "No trust yet"
              } else {
                val rating = round(rank.toFloat() * 100) / 100
                // val rating = 4.5
                val roundedRating = round(rating).toInt()
                stars =
                    when (roundedRating) {
                      0 -> {
                        "☆☆☆☆☆ ($rating/5)"
                      }
                      1 -> {
                        "★☆☆☆☆ ($rating/5)"
                      }
                      2 -> {
                        "★★☆☆☆ ($rating/5)"
                      }
                      3 -> {
                        "★★★☆☆ ($rating/5)"
                      }
                      4 -> {
                        "★★★★☆ ($rating/5)"
                      }
                      5 -> {
                        "★★★★★ ($rating/5)"
                      }
                      else -> {
                        "..."
                      }
                    }
              }
              TextField(
                  value = stars,
                  onValueChange = {},
                  label = { Text("Trust") },
                  colors =
                      TextFieldDefaults.colors(
                          focusedIndicatorColor = Color.Transparent,
                          disabledIndicatorColor = Color.Transparent,
                          unfocusedIndicatorColor = Color.Transparent,
                          focusedContainerColor = Color.Transparent,
                          unfocusedContainerColor = Color.Transparent,
                          disabledContainerColor = Color.Transparent),
                  modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("rating"),
                  readOnly = true,
                  leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) })
              Spacer(modifier = Modifier.height(16.dp))
              Row(
                  modifier = Modifier.fillMaxWidth().padding(8.dp, 0.dp).testTag("actionButtons"),
                  horizontalArrangement = Arrangement.Absolute.Center) {
                    Button(
                        onClick = { navigationActions.navigateTo(Route.INVENTORY) },
                        modifier = Modifier.weight(1f).testTag("inventoryButton")) {
                          Text("See inventory")
                        }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { /*TODO: friends */},
                        modifier = Modifier.weight(1f).testTag("friendButton")) {
                          Text("Edit Profile [not yet implemented]")
                        }
                  }
            }
      }
}
