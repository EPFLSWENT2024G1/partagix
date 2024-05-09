package com.android.partagix.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.android.partagix.R
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.user.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

/**
 * Composable function to display an item, in a rectangle form.
 *
 * @param item an Item instance to display.
 * @param user the user of the item
 * @param loan the possible loan of the item
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ItemUi(
    item: Item,
    user: User,
    loan: Loan,
    isExpandable: Boolean,
    expandState: Boolean = false,
    manageLoanViewModel: ManageLoanViewModel = ManageLoanViewModel(),
    index: Int,
) {
  val date: Date =
      if (loan.startDate.before(Date())) {
        loan.endDate
      } else {
        loan.startDate
      }
  var expandables by remember { mutableStateOf(expandState) }
  val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
  if (isExpandable) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier =
            Modifier.fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFF939393),
                    shape = RoundedCornerShape(size = 4.dp))
                .animateContentSize(
                    animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing))
                .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 4.dp))
                .padding(PaddingValues(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp))
                .clickable(onClick = { expandables = !expandables })
                .testTag("manageLoanScreenItemCard")) {
          Row(
              horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
              modifier = Modifier.fillMaxWidth().height(61.dp)) {
                Column(modifier = Modifier.weight(weight = 1f).fillMaxWidth()) {
                  Row(modifier = Modifier.fillMaxHeight(0.5f)) {
                    Text(text = user.rank, modifier = Modifier.fillMaxWidth(0.15f))

                    Text(
                        text = user.name,
                        color = Color(0xff49454f),
                        lineHeight = 1.33.em,
                        style =
                            TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF000000),
                                textAlign = TextAlign.Left,
                            ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(0.85f))
                  }
                  Text(
                      text =
                          if (loan.idItem.equals("")) {
                            "not borrowed"
                          } else {
                            if (loan.startDate.before(Date())) {
                              "available until ${LocalDateTime.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault()).format(formatter)}"
                            } else {
                              "borrowed until ${LocalDateTime.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault()).format(formatter)}"
                            }
                          },
                      lineHeight = 1.43.em,
                      style = TextStyle(fontSize = 13.sp, letterSpacing = 0.25.sp),
                      modifier = Modifier.fillMaxWidth())
                }
                Column(modifier = Modifier.requiredHeight(height = 64.dp)) {
                  Text(
                      text = item.name,
                      textAlign = TextAlign.End,
                      lineHeight = 1.45.em,
                      style =
                          TextStyle(
                              fontSize = 18.sp,
                              fontWeight = FontWeight(500),
                              color = Color(0xFF000000),
                              textAlign = TextAlign.Right,
                          ),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      modifier =
                          Modifier.fillMaxWidth(0.3f).fillMaxHeight(0.5f).padding(top = 10.dp))
                  Text(
                      text = "Quantity: " + item.quantity.toString(),
                      style =
                          TextStyle(
                              fontSize = 9.sp,
                              textAlign = TextAlign.Right,
                          ),
                      textAlign = TextAlign.End,
                      lineHeight = 0.8.em,
                      modifier =
                          Modifier.fillMaxWidth(0.3f).fillMaxHeight(0.5f).padding(top = 5.dp))
                }
                Image(
                    painter = painterResource(id = R.drawable.mutliprise),
                    contentDescription = "fds",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxWidth(0.3f).border(1.dp, Color.Black))
              }
          if (expandables) {

            Text(
                text = "Stay in touch:",
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth())
            Row(
                horizontalArrangement = Arrangement.Absolute.Right,
                modifier = Modifier.fillMaxWidth().testTag("manageLoanScreenItemCardExpanded")) {
                  Button(
                      onClick = { manageLoanViewModel.acceptLoan(loan, index) },
                      content = {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "cancel",
                            modifier = Modifier,
                            Color.Green)
                        Text(text = "validate")
                      },
                      border = BorderStroke(1.dp, Color.Green),
                      modifier = Modifier.fillMaxWidth(0.35f))
                  Button(
                      onClick = { manageLoanViewModel.declineLoan(loan, index) },
                      content = {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = "cancel",
                            modifier = Modifier,
                            Color.Red)
                        Text(text = "cancel")
                      },
                      border = BorderStroke(1.dp, Color.Red),
                      modifier = Modifier.fillMaxWidth(0.5f))
                }
          }
        }
  } else {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier =
            Modifier.fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFF939393),
                    shape = RoundedCornerShape(size = 4.dp))
                .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 4.dp))
                .padding(PaddingValues(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp))) {
          Row(
              horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
              modifier = Modifier.fillMaxWidth().height(61.dp)) {
                Column(modifier = Modifier.weight(weight = 1f).fillMaxWidth()) {
                  Row(
                      modifier = Modifier.fillMaxHeight(0.5f),
                      verticalAlignment = Alignment.CenterVertically) {
                        Text(text = user.rank, style = TextStyle(fontWeight = FontWeight(500)))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = user.name,
                            color = Color(0xff49454f),
                            style =
                                TextStyle(
                                    fontWeight = FontWeight(500),
                                    color = Color(0xFF000000),
                                    textAlign = TextAlign.Left,
                                ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(0.95f))
                      }
                  Text(
                      text =
                          if (loan.idItem == "") {
                            "not borrowed"
                          } else {
                            if (loan.startDate.before(Date())) {
                              "available until ${LocalDateTime.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault()).format(formatter)}"
                            } else {
                              "borrowed until ${LocalDateTime.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault()).format(formatter)}"
                            }
                          },
                      style = TextStyle(fontSize = 12.sp, letterSpacing = 0.25.sp),
                      modifier = Modifier.fillMaxWidth())
                }
                Column(
                    modifier = Modifier.requiredHeight(height = 64.dp),
                    horizontalAlignment = Alignment.End) {
                      Text(
                          text = item.name,
                          textAlign = TextAlign.End,
                          style =
                              TextStyle(
                                  color = Color(0xFF000000),
                                  textAlign = TextAlign.Right,
                              ),
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis,
                          modifier = Modifier.fillMaxWidth(.3f).padding(top = 10.dp))
                      Text(
                          text = "Quantity: " + item.quantity.toString(),
                          style =
                              TextStyle(
                                  fontSize = 11.sp,
                                  textAlign = TextAlign.Right,
                              ),
                          textAlign = TextAlign.End,
                          lineHeight = 0.8.em,
                          modifier = Modifier.fillMaxWidth(0.2f).padding(top = 5.dp))
                    }
                Image(
                    painter = painterResource(id = R.drawable.mutliprise),
                    contentDescription = "fds",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxWidth(0.3f).border(1.dp, Color.Black))
              }
        }
  }
}
