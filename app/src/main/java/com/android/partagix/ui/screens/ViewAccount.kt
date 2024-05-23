package com.android.partagix.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.partagix.model.UserViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.LabeledText
import com.android.partagix.ui.components.UserComment
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
            modifier = Modifier.fillMaxWidth().testTag("topBar"),
            title = { Text("My Account", modifier = Modifier.fillMaxWidth().testTag("title")) },
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
            selectedDestination = Route.ACCOUNT,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = modifier.testTag("accountScreenBottomNavBar"))
      }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.fillMaxHeight()
                    .padding(it)
                    .verticalScroll(rememberScrollState())
                    .testTag("mainContent")) {
              Row(
                  modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("editButton"),
                  horizontalArrangement = Arrangement.Absolute.Right) {
                    Button(
                        onClick = { navigationActions.navigateTo(Route.EDIT_ACCOUNT) },
                        modifier = Modifier.testTag("editProfileButton")) {
                          Text("Edit Profile")
                        }
                  }
              Spacer(modifier = Modifier.height(8.dp))
              Box(
                  modifier = Modifier.height(150.dp).width(150.dp).testTag("userImageBox"),
                  contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = user.imageId.absolutePath,
                        contentDescription = "image",
                        contentScale = ContentScale.Inside,
                        modifier =
                            Modifier.border(1.dp, Color.Black).fillMaxHeight().testTag("userImage"),
                        alignment = Alignment.Center,
                    )
                  }
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                  modifier = Modifier.fillMaxWidth().testTag("username"),
                  horizontalArrangement = Arrangement.Absolute.SpaceAround) {
                    val username = user.name
                    Text("$username's profile", modifier = Modifier.testTag("usernameText"))
                  }
              Spacer(modifier = Modifier.height(16.dp))
              Row(modifier = modifier.fillMaxWidth().padding(8.dp)) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = modifier.padding(start = 12.dp, top = 15.dp).testTag("address"))
                LabeledText(
                    modifier = modifier.fillMaxWidth(), label = "Location", text = user.address)
              }

              val rank = user.rank
              val stars: String
              if (rank == "") {
                stars = "No trust yet"
              } else {
                val rating = round(rank.toFloat() * 100) / 100
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
              Row(modifier = modifier.fillMaxWidth().padding(8.dp)) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = modifier.padding(start = 12.dp, top = 15.dp).testTag("rating"))
                LabeledText(modifier = modifier.fillMaxWidth(), label = "Trust", text = stars)
              }
              Spacer(modifier = Modifier.height(16.dp))
              Row(
                  modifier = Modifier.fillMaxWidth().padding(8.dp, 0.dp).testTag("actionButtons"),
                  horizontalArrangement = Arrangement.Absolute.Center) {
                    Button(
                        onClick = { navigationActions.navigateTo(Route.INVENTORY) },
                        modifier = Modifier.weight(1f).testTag("inventoryButton")) {
                          Text("See inventory")
                        }
                  }
              Spacer(modifier = Modifier.height(16.dp))
              val commentList = uiState.value.comments
              if (commentList.isEmpty()) {
                Text("No comments yet", modifier = Modifier.testTag("noComments"))
              } else {
                LazyColumn {
                  items(commentList.size) { index ->
                    val comment = commentList[index]
                    UserComment(comment.first, comment.second, userViewModel, navigationActions)
                  }
                }
              }
            }
      }
}
