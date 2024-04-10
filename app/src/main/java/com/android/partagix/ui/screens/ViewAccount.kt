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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.android.partagix.R
import com.android.partagix.model.UserViewModel
import kotlin.math.round

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAccount(
    modifier: Modifier = Modifier,
    userViewModel : UserViewModel = UserViewModel(),
) {
  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("My Account") },
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = {
              IconButton(onClick = { /*TODO: navigate to previous screen*/}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.width(48.dp))
              }
            })
      },
      modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(it).verticalScroll(rememberScrollState())) {
              Image(
                  painter =
                      painterResource(
                          id = R.drawable.ic_launcher_background) /*TODO: get profile picture*/,
                  contentDescription = null,
                  modifier = Modifier.fillMaxWidth(),
                  alignment = Alignment.Center)
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.Absolute.SpaceAround) {
                    val username = userViewModel.uiState.collectAsState().value.user.name
                    Text("$username's profile")
                  }
              Spacer(modifier = Modifier.height(16.dp))

              TextField(
                  value = userViewModel.uiState.collectAsState().value.user.address,
                  onValueChange = {},
                  label = { Text("Location") },
                  colors =
                      TextFieldDefaults.colors(
                          focusedIndicatorColor = Color.Gray,
                          disabledIndicatorColor = Color.Gray,
                          unfocusedIndicatorColor = Color.Gray,
                          focusedContainerColor = Color.Transparent,
                          unfocusedContainerColor = Color.Transparent,
                          disabledContainerColor = Color.Transparent),
                  modifier = Modifier.fillMaxWidth().padding(8.dp),
                  readOnly = true,
                  leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) })
              val rank = userViewModel.uiState.collectAsState().value.user.rank
              val rating = round(rank.toFloat() * 100) / 100
              val roundedRating = round(rating).toInt()
              val stars =
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
                      "c'est tout cassé mon reuf"
                    }
                  }
              TextField(
                  value = stars,
                  onValueChange = {},
                  label = { Text("Trust") },
                  colors =
                      TextFieldDefaults.colors(
                          focusedIndicatorColor = Color.Gray,
                          disabledIndicatorColor = Color.Gray,
                          unfocusedIndicatorColor = Color.Gray,
                          focusedContainerColor = Color.Transparent,
                          unfocusedContainerColor = Color.Transparent,
                          disabledContainerColor = Color.Transparent),
                  modifier = Modifier.fillMaxWidth().padding(8.dp),
                  readOnly = true,
                  leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) })
              Spacer(modifier = Modifier.height(16.dp))
              Row(modifier = Modifier.fillMaxWidth().padding(8.dp, 0.dp)) {
                Button(
                    onClick = { /*TODO: navigate to inventory */},
                    modifier = Modifier.fillMaxWidth(0.5f)) {
                      Text("See inventory")
                    }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { /*TODO: friends */}, modifier = Modifier.fillMaxWidth()) {
                  Text("Add as friend")
                }
              }
            }
      }
}
