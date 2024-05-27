package com.android.partagix.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import java.time.format.DateTimeFormatter
import java.util.Date

/**
 * Composable function to display an item, as a row.
 *
 * @param item an Item instance to display.
 * @param user the user of the item
 * @param loan the possible loan of the item
 * @param modifier Modifier only used to add parameters when calling
 * @param isOutgoing Boolean to know if the loan is outgoing, and set according buttons when
 *   expanded
 * @param isOwner Boolean to know if the user is the owner of the item, and hide its name if so
 * @param isLender Boolean to know if the user is the lender of the item, and hide the availability
 * @param isExpandable Boolean to know if the item can be expanded
 * @param expandState Boolean to know if the item is expanded
 * @param onItemClick Function to be called when the item is clicked
 * @param onUserClick Function to be called when the owner's name is clicked
 * @param manageLoanViewModel ManageLoanViewModel to handle the loan
 * @param index Int to know the index of the item
 */
@Composable
fun ItemUi(
    item: Item,
    user: User,
    loan: Loan,
    modifier: Modifier = Modifier, // useful when calling the composable
    isOutgoing: Boolean = false,
    isOwner: Boolean = false,
    isLender: Boolean = false,
    isExpandable: Boolean = false,
    expandState: Boolean = false,
    onItemClick: (Item) -> Unit = {},
    onUserClick: (Item) -> Unit = {},
    manageLoanViewModel: ManageLoanViewModel = ManageLoanViewModel(),
    index: Int = 0,
) {
  //  val modifier = modifier
  val date: Date =
      if (loan.startDate.before(Date())) {
        loan.endDate
      } else {
        loan.startDate
      }
  var expanded by remember { mutableStateOf(expandState) }
  val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

  val available = true // TODO
  var availability = "Now available"

  if (!available) {
    availability = "Unavailable"
  }

  if (isLender) {
    availability = "Accepted but not started"
  }
  if (isLender && (loan.state == LoanState.ONGOING)) { // normal case of borrowed item
    availability = ""
  }

  val itemHeight = 62.dp
  val nameFontSize = 18.sp
  val smallerFontSize = 13.sp

  var mainRowModifier = modifier.fillMaxWidth().height(itemHeight)
  if (!isExpandable && onItemClick != {}) {
    mainRowModifier = mainRowModifier.clickable { onItemClick(item) }
  }

  var ownerModifier = Modifier.padding(end = 1.dp)
  if (onUserClick != {}) {
    ownerModifier =
        ownerModifier.clickable { onUserClick(item) } // todo make sure of the type of onUserClick
  }

  var mainColumnModifier =
      modifier
          //          .fillMaxWidth()
          //          .requiredHeight(itemHeight)
          .fillMaxSize()
          .border(
              width = 1.dp,
              color = MaterialTheme.colorScheme.outlineVariant,
              shape = RoundedCornerShape(size = 4.dp))
          .animateContentSize(
              animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing))
          .background(
              color = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(size = 4.dp))
          .padding(PaddingValues(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp))
          .testTag("manageLoanScreenItemCard")

  if (isExpandable) {
    mainColumnModifier =
        mainColumnModifier
            .testTag("ItemUiNotExpanded")
            .clickable(
                onClick = {
                  expanded = !expanded
                  manageLoanViewModel.updateExpanded(index, expanded)
                })
  }

  Column(horizontalAlignment = Alignment.Start, modifier = mainColumnModifier) {

    // Core row
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
        modifier = mainRowModifier) {
          Column(modifier = Modifier.weight(weight = 1f).fillMaxWidth()) {

            // Item name
            Row(modifier = Modifier.fillMaxWidth()) {
              Text(
                  text = item.name,
                  style =
                      TextStyle(
                          fontSize = nameFontSize,
                          fontWeight = FontWeight(500),
                          textAlign = TextAlign.Left,
                      ),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.fillMaxWidth(),
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Details
            Row(modifier = Modifier.fillMaxWidth()) {

              // Availability
              Row(modifier = Modifier.fillMaxWidth(0.7f)) {
                Text(
                    text = availability,
                    style =
                        TextStyle(
                            fontSize = smallerFontSize,
                            textAlign = TextAlign.Left,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
              }

              // Quantity
              Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Text(
                    text = "Qty: ${item.quantity}",
                    style =
                        TextStyle(
                            fontSize = smallerFontSize,
                            textAlign = TextAlign.Right,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth())
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Owner
            if (!isOwner) {
              Row(modifier = ownerModifier) {

                // Owner name
                Text(
                    text = user.name,
                    style =
                        TextStyle(
                            fontSize = smallerFontSize,
                            textAlign = TextAlign.Left,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.width(5.dp))

                // Owner rank
                Row(modifier = Modifier.padding(top = 1.dp).height(13.dp)) {
                  RankStars(rank = user.rank)
                }
              }
            }
          }

          Box {
            AsyncImage(
                model = item.imageId.absolutePath,
                contentDescription = "fds",
                contentScale = ContentScale.Inside,
                modifier =
                    Modifier.height(itemHeight)
                        .width(itemHeight)
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .testTag("ItemUiImage"))
          }
        } // End of core row

    if (expanded) {
      Spacer(modifier = Modifier.height(2.dp))

      if (loan.state == LoanState.ACCEPTED || loan.state == LoanState.ONGOING) {
        var u by remember { mutableStateOf(emptyUser) }
        if (loan.idLender == user.id)
            manageLoanViewModel.getUser(
                loan.idLender,
            ) {
              u = it
            }
        else manageLoanViewModel.getUser(loan.idBorrower) { u = it }
        Text("Preferred contact: ", fontSize = smallerFontSize, lineHeight = 1.3.em)
        if (u.favorite != listOf(false, false, false)) {
          val email = if (u.favorite?.get(0) == true) "Email : ${u.email}" else ""
          val phone = if (u.favorite?.get(1) == true) "Phone : ${u.phoneNumber}" else ""
          val telegram = if (u.favorite?.get(2) == true) "Telegram : ${u.telegram}" else ""
          if (email.isEmpty() && phone.isEmpty() && telegram.isEmpty())
              Text("No preferred contact", fontSize = smallerFontSize, lineHeight = 1.3.em)
          else {
            listOf(email, phone, telegram)
                .filter { it.isNotEmpty() }
                .forEach { contact -> Text(contact, fontSize = 11.sp, lineHeight = 1.3.em) }
          }
        } else {
          Text("No preferred contact", fontSize = 11.sp, lineHeight = 1.3.em)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text("Other contact: ", fontSize = smallerFontSize, lineHeight = 1.3.em)
        if (u.favorite != listOf(false, false, false)) {
          val email = if (u.favorite?.get(0) == false && u.email != "") "Email : ${u.email}" else ""
          val phone =
              if (u.favorite?.get(1) == false && u.phoneNumber != "") "Phone : ${user.phoneNumber}"
              else ""
          val telegram =
              if (u.favorite?.get(2) == false && u.telegram != "") "Telegram : ${user.telegram}"
              else ""
          if (email.isEmpty() && phone.isEmpty() && telegram.isEmpty())
              Text("No other contact", fontSize = 11.sp, lineHeight = 1.3.em)
          else {
            listOf(email, phone, telegram)
                .filter { it.isNotEmpty() }
                .forEach { contact -> Text(contact, fontSize = 11.sp, lineHeight = 1.3.em) }
          }
        } else {
          Text("No other contact", fontSize = smallerFontSize)
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.Absolute.Right,
            modifier = Modifier.fillMaxWidth().testTag("manageLoanScreenItemCardExpanded")) {
              if (!isOutgoing) {
                Button(
                    onClick = { manageLoanViewModel.acceptLoan(loan, index) },
                    content = {
                      Icon(
                          Icons.Default.Check, contentDescription = "validate", modifier = Modifier)
                      Spacer(Modifier.width(3.dp))
                      Text(text = "Validate")
                    },
                    modifier = Modifier.requiredWidth(100.dp).requiredHeight(32.dp),
                    contentPadding = PaddingValues(3.dp, 0.dp, 7.dp, 0.dp))
              }
              Spacer(modifier = Modifier.width(6.dp))
              Button(
                  onClick = { manageLoanViewModel.declineLoan(loan, index) },
                  content = {
                    Icon(Icons.Default.Close, contentDescription = "cancel", modifier = Modifier)
                    Spacer(Modifier.width(2.dp))
                    Text(text = "Reject")
                  },
                  colors =
                      ButtonColors(
                          containerColor = MaterialTheme.colorScheme.error,
                          contentColor = MaterialTheme.colorScheme.onError,
                          disabledContainerColor = MaterialTheme.colorScheme.error,
                          disabledContentColor = MaterialTheme.colorScheme.onError),
                  modifier = Modifier.requiredWidth(100.dp).requiredHeight(32.dp),
                  contentPadding = PaddingValues(3.dp, 0.dp, 7.dp, 0.dp))
            }
      }
    }
  }
  /*} else {
    Column(
      horizontalAlignment = Alignment.Start, modifier = Modifier
        .fillMaxWidth()
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.outlineVariant,
          shape = RoundedCornerShape(size = 4.dp)
        )
        .background(
          color = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(size = 4.dp)
        )
        .padding(PaddingValues(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp))
        .testTag("ItemUiNotExpanded")
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
        modifier = Modifier
          .fillMaxWidth()
          .height(61.dp)
      ) {
        Column(
          modifier = Modifier
            .weight(weight = 1f)
            .fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.fillMaxHeight(0.5f), verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = user.rank, style = TextStyle(fontWeight = FontWeight(500)))
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = user.name,
              style = TextStyle(
                fontWeight = FontWeight(500),
                textAlign = TextAlign.Left,
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.fillMaxWidth(0.95f)
            )
          }
          Text(
            text = if (loan.idItem == "") {
              "not borrowed"
            } else {
              if (loan.startDate.before(Date())) {
                "available until ${
                  LocalDateTime.ofInstant(
                    date.toInstant(), java.time.ZoneId.systemDefault()
                  ).format(formatter)
                }"
              } else {
                "borrowed until ${
                  LocalDateTime.ofInstant(
                    date.toInstant(), java.time.ZoneId.systemDefault()
                  ).format(formatter)
                }"
              }
            },
            style = TextStyle(fontSize = 12.sp, letterSpacing = 0.25.sp),
            modifier = Modifier.fillMaxWidth()
          )
        }
        Column(
          modifier = Modifier.requiredHeight(height = 64.dp), horizontalAlignment = Alignment.End
        ) {
          Text(
            text = item.name,
            textAlign = TextAlign.End,
            style = TextStyle(textAlign = TextAlign.Right),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
              .fillMaxWidth(.3f)
              .padding(top = 10.dp)
          )
          Text(
            text = "Quantity: " + item.quantity.toString(),
            style = TextStyle(
              fontSize = 11.sp,
              textAlign = TextAlign.Right,
            ),
            textAlign = TextAlign.End,
            lineHeight = 0.8.em,
            modifier = Modifier
              .fillMaxWidth(0.2f)
              .padding(top = 5.dp)
          )
        }
        Box(
          modifier = Modifier
            .fillMaxWidth(0.3f)
            .fillMaxHeight()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
          AsyncImage(
            model = item.imageId.absolutePath,
            contentDescription = "fds",
            contentScale = ContentScale.Inside,
            modifier = Modifier
              .fillMaxHeight()
              .align(Alignment.Center)
              .testTag("ItemUiImage")
          )
        }
      }
    }
  }*/
}
