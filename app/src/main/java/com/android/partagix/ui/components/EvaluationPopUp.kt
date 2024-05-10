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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.partagix.model.EvaluationViewModel
import com.android.partagix.model.loan.Loan

/**
 * EvaluationPopUp is a composable function that displays a dialog to rate and comment on a loan.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param loan Loan to be evaluated.
 * @param viewModel EvaluationViewModel to handle the evaluation.
 */
@Composable
fun EvaluationPopUp(
    modifier: Modifier = Modifier,
    loan: Loan,
    userId: String,
    viewModel: EvaluationViewModel,
    onClose: () -> Unit = {}
) {
  val openDialog = remember { mutableStateOf(true) }
  val haveRated = remember { mutableStateOf(false) }
  val haveCommented = remember { mutableStateOf(false) }
  var comment by remember { mutableStateOf("") }
  var rating by remember { mutableDoubleStateOf(0.0) }
  viewModel.updateUIState(loan)

  if (userId == loan.idLender && loan.reviewLender.isNotEmpty()) {
    rating = loan.reviewLender.toDouble()
    haveRated.value = true
  }
  if (userId == loan.idBorrower && loan.reviewBorrower.isNotEmpty()) {
    rating = loan.reviewBorrower.toDouble()
    haveRated.value = true
  }
  if (userId == loan.idLender && loan.commentLender.isNotEmpty()) {
    comment = loan.commentLender
    haveCommented.value = true
  }
  if (userId == loan.idBorrower && loan.commentBorrower.isNotEmpty()) {
    comment = loan.commentBorrower
    haveCommented.value = true
  }

  if (openDialog.value) {
    Dialog(
        onDismissRequest = {
          if (haveRated.value) {
            viewModel.reviewLoan(loan, rating, comment, userId)
          }
          openDialog.value = false
          onClose()
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)) {
          Surface(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier =
                    modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 0.dp, bottom = 16.dp, top = 0.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .testTag("evaluationPopUp")) {
                  Row(
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = modifier.fillMaxWidth()
                      // .padding(start= 10.dp, end = 26.dp)
                      ) {
                        Text(
                            text = "Rate your loan :",
                            fontSize = 25.sp,
                            modifier =
                                modifier
                                    .padding(start = 10.dp, end = 26.dp, top = 16.dp)
                                    .testTag("rateText"))
                        IconButton(
                            modifier = modifier.testTag("closeButton"),
                            onClick = {
                              if (haveRated.value) {
                                viewModel.reviewLoan(loan, rating, comment, userId)
                              }
                              onClose()
                              openDialog.value = false
                            }) {
                              Icon(imageVector = Icons.Default.Close, contentDescription = "")
                            }
                      }

                  Spacer(modifier.height(16.dp))

                  Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.Center,
                      modifier =
                          modifier
                              .fillMaxWidth()
                              .padding(start = 10.dp, end = 26.dp)
                              .testTag("rateStars")) {
                        repeat(5) { index ->
                          val isSelected = index + 1 <= rating
                          val isHalfSelected = index + 0.5 == rating

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
                                                  if (isHalfSelected) index.toDouble()
                                                  else index.toFloat() + 0.5
                                              else -> index.toDouble() + 1
                                            }
                                      }
                                      .size(50.dp)
                                      .testTag("star$index"))
                        }
                      }

                  Spacer(modifier.height(20.dp))
                  if (!haveRated.value) {
                    Button(
                        onClick = { haveRated.value = true },
                        enabled = rating > 0f && !haveRated.value,
                        modifier =
                            Modifier.padding(end = 16.dp)
                                .fillMaxWidth(0.6f)
                                .align(Alignment.CenterHorizontally)
                                .testTag("validateButton")) {
                          Text(text = "Validate", fontSize = 18.sp)
                        }
                  } else {
                    Row(
                        modifier = modifier.padding(end = 16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center) {
                          Text(text = "You have already rated this loan.")
                        }
                  }

                  Spacer(modifier.height(30.dp))

                  Text(
                      text = "Leave a comment :",
                      fontSize = 25.sp,
                      modifier =
                          modifier.padding(end = 16.dp).padding(10.dp, 0.dp).testTag("commentText"))

                  Spacer(modifier.height(16.dp))

                  OutlinedTextField(
                      value = comment,
                      onValueChange = { comment = it },
                      modifier =
                          modifier
                              .padding(end = 16.dp)
                              .fillMaxWidth()
                              .padding(10.dp, 0.dp)
                              .testTag("commentField"),
                      minLines = 8,
                      textStyle = TextStyle(fontSize = 16.sp),
                      enabled = !haveCommented.value)

                  Spacer(modifier.height(16.dp))
                  if (!haveCommented.value) {
                    Button(
                        onClick = { haveCommented.value = true },
                        enabled = comment.isNotEmpty() && !haveCommented.value,
                        modifier =
                            Modifier.padding(end = 16.dp)
                                .fillMaxWidth(0.6f)
                                .align(Alignment.CenterHorizontally)
                                .testTag("commentButton")) {
                          Text(text = "Comment", fontSize = 18.sp)
                        }
                  } else {
                    Row(
                        modifier = modifier.padding(end = 16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically) {
                          Text(text = "You already left a comment.", textAlign = TextAlign.Center)
                        }
                  }

                  Spacer(modifier.height(8.dp))
                }
          }
        }
  }
}
