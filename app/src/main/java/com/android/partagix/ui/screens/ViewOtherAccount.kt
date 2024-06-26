package com.android.partagix.ui.screens

import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.user.User
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.LabeledText
import com.android.partagix.ui.components.RankingStars
import com.android.partagix.ui.components.UserComment
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import kotlin.math.round

private const val TAG = "ViewOtherAccount"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOtherAccount(
    modifier: Modifier = Modifier,
    navigationActions: NavigationActions,
    userViewModel: UserViewModel,
    otherUserViewModel: UserViewModel,
) {
  val uiState = otherUserViewModel.uiState.collectAsState()
  val user = uiState.value.user
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("viewAccount"),
      topBar = {
        TopAppBar(
            modifier = Modifier.fillMaxWidth().testTag("topBar"),
            title = {
              if (!uiState.value.loading) {
                Text(
                    user.name + "'s Profile",
                    modifier = Modifier.fillMaxWidth().testTag("title"),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
              }
            },
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
        if (uiState.value.loading) {
          Box(
              modifier = Modifier.fillMaxSize().testTag("Loading"),
              contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
              }
          return@Scaffold
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.fillMaxHeight()
                    .padding(it)
                    .verticalScroll(rememberScrollState())
                    .testTag("mainContent")) {
              Spacer(modifier = Modifier.height(16.dp))
              Box(
                  modifier = Modifier.height(150.dp).width(150.dp).testTag("userImageBox"),
                  contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = user.imageId.absolutePath,
                        contentDescription = "image",
                        contentScale = ContentScale.Inside,
                        modifier = Modifier.fillMaxHeight().testTag("userImage"),
                        alignment = Alignment.Center,
                    )
                  }
              Row(
                  modifier = Modifier.fillMaxWidth().padding(top = 4.dp).testTag("username"),
                  horizontalArrangement = Arrangement.Absolute.SpaceAround) {
                    val username = user.name
                    Text("$username's profile", modifier = Modifier.testTag("usernameText"))
                  }
              Row(modifier = modifier.fillMaxWidth().padding(8.dp)) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = modifier.padding(start = 12.dp, top = 16.dp).testTag("address"))
                LabeledText(
                    modifier = modifier.fillMaxWidth(), label = "Location", text = user.address)
              }

              val rank = user.rank
              val stars: String
              if (rank == "") {
                stars = ""
              } else {
                val rating = round(rank.toFloat() * 100) / 100
                val roundedRating = round(rating).toInt()
                stars =
                    when (roundedRating) {
                      0 -> {
                        "($rating/5)"
                      }
                      1 -> {
                        "($rating/5)"
                      }
                      2 -> {
                        "($rating/5)"
                      }
                      3 -> {
                        "($rating/5)"
                      }
                      4 -> {
                        "($rating/5)"
                      }
                      5 -> {
                        "($rating/5)"
                      }
                      else -> {
                        ""
                      }
                    }
              }
              Row(modifier = modifier.fillMaxWidth().padding(8.dp)) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = modifier.padding(start = 12.dp, top = 12.dp).testTag("rating"))
                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                  Text(
                      modifier = Modifier.testTag("label"),
                      text = "Trust",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onBackground,
                  )
                  Spacer(modifier = Modifier.height(3.dp))
                  Row(modifier = Modifier.height(20.dp)) {
                    RankingStars(rank = rank, modifier = Modifier.padding(start = 6.dp, top = 3.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stars,
                        modifier = Modifier.padding(0.dp).testTag("text"),
                        fontSize = 15.sp,
                    )
                  }
                }
              }
              Spacer(modifier = Modifier.height(16.dp))

              val commentList = uiState.value.comments

              if (commentList.isEmpty() && !uiState.value.loadComment) {
                Text(
                    "No comments yet",
                    modifier = Modifier.padding(12.dp, 0.dp).testTag("noComments"))
              } else if (uiState.value.loadComment) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                  CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                }
              } else {
                Column(modifier = Modifier.padding(12.dp, 0.dp).testTag("comments")) {
                  Text(
                      text = "Comments",
                      style = MaterialTheme.typography.titleMedium,
                      modifier = Modifier.testTag("commentsTitle"))
                  commentList.forEach { comment ->
                    val onClick: (User) -> Unit =
                        if (comment.first.id != userViewModel.getLoggedUserId()) {
                          // When the author of the comment is different from the current user
                          {
                            Log.d(TAG, "UserComment (other user): $comment")
                            otherUserViewModel.setUser(comment.first)
                            navigationActions.navigateTo(Route.OTHER_ACCOUNT)
                          }
                        } else {
                          // When the author of the comment is the current user
                          {
                            Log.d(TAG, "UserComment (current user): $comment")
                            navigationActions.navigateTo(Route.ACCOUNT)
                          }
                        }

                    UserComment(comment.first, comment.second, onClick)
                  }
                }
                Spacer(modifier = Modifier.height(8.dp))
              }
            }
      }
}
