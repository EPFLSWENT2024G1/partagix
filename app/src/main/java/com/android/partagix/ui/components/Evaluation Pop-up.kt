package com.android.partagix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * EvaluationPopUp is a composable function that displays a dialog to rate and comment on a loan.
 */
@Composable
fun EvaluationPopUp(modifier: Modifier) {
  val openDialog = remember { mutableStateOf(true) }
  val haveRated = remember { mutableStateOf(false) }
  val haveCommented = remember { mutableStateOf(false) }
  if (openDialog.value) {
    Dialog(
        onDismissRequest = { openDialog.value = false },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)) {
          Column(
              modifier =
                  modifier
                      .fillMaxWidth()
                      .padding(16.dp)
                      .background(MaterialTheme.colorScheme.background)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.fillMaxWidth().padding(10.dp, 0.dp)) {
                      Text(text = "Rate your loan :", fontSize = 25.sp)
                      IconButton(onClick = { openDialog.value = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "")
                      }
                    }

                Spacer(modifier.height(16.dp))

                var rating by remember { mutableStateOf(0f) }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = modifier.fillMaxWidth().padding(10.dp, 0.dp)) {
                      repeat(5) { index ->
                        val isSelected = index + 1 <= rating
                        val isHalfSelected = index + 0.5f == rating

                        Icon(
                            imageVector =
                                when {
                                  isSelected -> Icons.Default.Star
                                  isHalfSelected -> Icons.AutoMirrored.Default.StarHalf
                                  else -> Icons.Default.StarBorder
                                },
                            contentDescription = "",
                            modifier =
                                modifier
                                    .clickable(enabled = !haveRated.value) {
                                      rating =
                                          when {
                                            isSelected ->
                                                if (isHalfSelected) index.toFloat()
                                                else index.toFloat() + 0.5f
                                            else -> index.toFloat() + 1f
                                          }
                                    }
                                    .size(50.dp))
                      }
                    }

                Spacer(modifier.height(20.dp))

                Button(
                    onClick = {
                      haveRated.value = true
                      /*TODO: maybe put rating to DB*/
                    },
                    enabled = rating > 0f && !haveRated.value,
                    modifier = Modifier.fillMaxWidth(0.6f).align(Alignment.CenterHorizontally)) {
                      Text(text = "Validate", fontSize = 18.sp)
                    }

                Spacer(modifier.height(30.dp))

                Text(
                    text = "Leave a comment :",
                    fontSize = 25.sp,
                    modifier = modifier.padding(10.dp, 0.dp))

                Spacer(modifier.height(16.dp))

                var comment by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    modifier = modifier.fillMaxWidth().padding(10.dp, 0.dp),
                    minLines = 8,
                    textStyle = TextStyle(fontSize = 16.sp),
                    enabled = !haveCommented.value)

                Spacer(modifier.height(16.dp))

                Button(
                    onClick = { haveCommented.value = true /*TODO: maybe put comment to DB*/ },
                    enabled = comment.isNotEmpty() && !haveCommented.value,
                    modifier = Modifier.fillMaxWidth(0.6f).align(Alignment.CenterHorizontally)) {
                      Text(text = "Comment", fontSize = 18.sp)
                    }

                Spacer(modifier.height(8.dp))
              }
        }
  }

  if (haveRated.value && haveCommented.value) {
    openDialog.value = false
  }
}
