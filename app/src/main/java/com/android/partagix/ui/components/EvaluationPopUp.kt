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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.partagix.model.EvaluationViewModel
import com.android.partagix.model.loan.Loan

/**
 * EvaluationPopUp is a composable function that displays a dialog to rate and comment on a loan.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param loan Loan to be evaluated.
 * @param userId Id of the user evaluating the loan.
 * @param viewModel EvaluationViewModel to handle the evaluation.
 * @param onClose Function to be called when the dialog is closed.
 */
@Composable
fun EvaluationPopUp(
    modifier: Modifier = Modifier,
    loan: Loan,
    userId: String,
    viewModel: EvaluationViewModel,
    onClose: (Loan) -> Unit = {}
) {
  val openDialog = remember { mutableStateOf(true) }
  var comment by remember { mutableStateOf("") }
  var rating by remember { mutableDoubleStateOf(0.0) }
  var idReviewed by remember { mutableStateOf("") }
  viewModel.updateUIState(loan)

  if (userId == loan.idLender) {
    idReviewed = loan.idBorrower
    rating = loan.reviewBorrower.toDoubleOrNull() ?: 0.0
    comment = loan.commentBorrower
  }

  if (userId == loan.idBorrower) {
    idReviewed = loan.idLender
    rating = loan.reviewLender.toDoubleOrNull() ?: 0.0
    comment = loan.commentLender
  }

  if (openDialog.value) {
    Dialog(
        onDismissRequest = {
          onClose(newLoan(loan, userId, comment, rating))
          openDialog.value = false
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
                      modifier = modifier.fillMaxWidth()) {
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
                              onClose(newLoan(loan, userId, comment, rating))
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
                                      .clickable {
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
                      textStyle = TextStyle(fontSize = 16.sp))

                  Spacer(modifier.height(16.dp))

                  Button(
                      onClick = {
                        if (rating != 0.0 || comment.isNotEmpty()) {
                          viewModel.reviewLoan(loan, rating, comment, idReviewed)
                          onClose(newLoan(loan, userId, comment, rating))
                          openDialog.value = false
                        }
                        onClose(newLoan(loan, userId, comment, rating))
                      },
                      enabled = (rating != 0.0 || comment.isNotEmpty()),
                      modifier =
                          Modifier.padding(end = 16.dp)
                              .fillMaxWidth(0.6f)
                              .align(Alignment.CenterHorizontally)
                              .testTag("evaluationButton")) {
                        Text(text = "Evaluate", fontSize = 18.sp)
                      }

                  Spacer(modifier.height(8.dp))
                }
          }
        }
  }
}

fun newLoan(loan: Loan, userId: String, comment: String, rating: Double): Loan {
  var l = loan.copy()
  if (userId == loan.idLender) {
    l = loan.copy(commentBorrower = comment, reviewBorrower = rating.toString())
  }
  if (userId == loan.idBorrower) {
    l = loan.copy(commentLender = comment, reviewLender = rating.toString())
  }
  return l
}
