package com.android.partagix.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.partagix.R

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAccount() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My profile" /*TODO: get username to display custom message*/) },
                modifier = Modifier.fillMaxWidth(),
                )
        },
        modifier = Modifier.fillMaxWidth()){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter =
                painterResource(
                    id = R.drawable.ic_launcher_background) /*TODO: get profile picture*/,
                contentDescription = null,
                alignment = Alignment.BottomCenter)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("username" /*TODO: get username*/)
                Text("'s profile")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                Text("location" /*TODO: get user location / NPA*/)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Trust ")
                var rating = 4.82 /*TODO: get user rating*/
                val roundedRating = (2*rating).toInt()
                when (roundedRating) {
                    0 -> {
                        Text("☆☆☆☆☆")
                    }
                    1 -> {
                        Text("☆☆☆☆")
                    }
                    2 -> {
                        Text("★☆☆☆☆")
                    }
                    4 -> {
                        Text("★★☆☆☆")
                    }
                    6 -> {
                        Text("★★★☆☆")
                    }
                    8 -> {
                        Text("★★★★☆")
                    }
                    10 -> {
                        Text("★★★★★")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /*TODO: navigate to inventory */ }) {
            Text("See inventory")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /*TODO: friends */ }) {
                Text("Add as friend")
            }
        }

    }

}
