package com.android.partagix.ui.components

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.utils.dateFormat
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
    navigationActions: NavigationActions,
    itemViewModel: ItemViewModel = ItemViewModel(),
    updateExpanded: (Int, Boolean) -> Unit = { i, expanded ->
      manageLoanViewModel.updateExpanded(i, expanded)
    },
    index: Int = 0,
) {
  val uiState by itemViewModel.uiState.collectAsStateWithLifecycle()
  var expanded by remember { mutableStateOf(expandState) }

  val available = uiState.unavailableDates.contains(Date())
  var availability = "Now available"

  if (!available) {
    availability =
        "Unavailable until " +
            dateFormat(uiState.unavailableDates.sorted()[uiState.unavailableDates.size])
  }

  if (isLender) {
    availability = "Accepted but not started"
  }
  if (isLender && (loan.state == LoanState.ONGOING)) { // normal case of borrowed item
    availability = "Available on " + dateFormat(loan.endDate)
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
                  updateExpanded(index, expanded)
                })
  }

  Column(horizontalAlignment = Alignment.Start, modifier = mainColumnModifier) {

    // Core row
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
        modifier = mainRowModifier) {
          Column(modifier = Modifier.weight(weight = 1f).fillMaxWidth()) {

            // Item name
            Row(modifier = Modifier.fillMaxWidth().height(23.dp)) {
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

            Spacer(modifier = Modifier.weight(1f).height(1.dp))

            // Details
            Row(modifier = Modifier.fillMaxWidth().height(15.dp)) {

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

            Spacer(modifier = Modifier.height(1.dp).weight(1f))

            // Owner
            if (!isOwner) {
              Row(modifier = ownerModifier.height(15.dp)) {

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
                RankingStars(
                    rank = user.rank, modifier = Modifier.padding(top = 3.dp).height(13.dp))
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
      Spacer(modifier = Modifier.height(8.dp))
      if (loan.state == LoanState.ACCEPTED || loan.state == LoanState.ONGOING) {
        var u by remember { mutableStateOf(emptyUser) }
        if (loan.idLender == user.id)
            manageLoanViewModel.getUser(
                loan.idLender,
            ) {
              u = it
            }
        else manageLoanViewModel.getUser(loan.idBorrower) { u = it }

        // favorite contacts
        if (u.favorite != listOf(false, false, false)) {
          val email = if (u.favorite?.get(0) == true && u.email != "") "Email: ${u.email}" else ""
          val phone =
              if (u.favorite?.get(1) == true && u.phoneNumber != "") "Phone: ${u.phoneNumber}"
              else ""
          val telegram =
              if (u.favorite?.get(2) == true && u.telegram != "") "Telegram: ${u.telegram}" else ""
          listOf(email, phone, telegram).filter { it.isNotEmpty() }
          listOf(email, phone, telegram)
              .filter { it.isNotEmpty() }
              .forEach { contact -> ClickableText(contact, true) }
        }
        // other contacts
        if (u.favorite != listOf(false, false, false)) {
          val email = if (u.favorite?.get(0) == false && u.email != "") "Email: ${u.email}" else ""
          val phone =
              if (u.favorite?.get(1) == false && u.phoneNumber != "") "Phone: ${user.phoneNumber}"
              else ""
          val telegram =
              if (u.favorite?.get(2) == false && u.telegram != "") "Telegram: ${user.telegram}"
              else ""
          listOf(email, phone, telegram).filter { it.isNotEmpty() }
          listOf(email, phone, telegram)
              .filter { it.isNotEmpty() }
              .forEach { contact -> ClickableText(contact) }
        }
      }
      Spacer(modifier = Modifier.height(8.dp))

      Row(
          horizontalArrangement = Arrangement.Absolute.Right,
          modifier = Modifier.fillMaxWidth().testTag("manageLoanScreenItemCardExpanded")) {
            if (!isOutgoing) {
              Button(
                  onClick = { manageLoanViewModel.acceptLoan(loan, index) },
                  content = {
                    Icon(Icons.Default.Check, contentDescription = "validate", modifier = Modifier)
                    Spacer(Modifier.width(3.dp))
                    Text(text = "Validate")
                  },
                  modifier = Modifier.requiredWidth(100.dp).requiredHeight(32.dp),
                  contentPadding = PaddingValues(3.dp, 0.dp, 7.dp, 0.dp))
            }
            if (loan.state == LoanState.ONGOING || loan.state == LoanState.ACCEPTED) {
              Button(
                  onClick = {
                    itemViewModel.updateUiItem(item)
                    navigationActions.navigateTo(Route.VIEW_ITEM)
                  },
                  content = {
                    Icon(Icons.Default.Info, contentDescription = "see item", modifier = Modifier)
                    Spacer(Modifier.width(3.dp))
                    Text(text = "Infos")
                  },
                  modifier = Modifier.requiredWidth(100.dp).requiredHeight(32.dp),
                  contentPadding = PaddingValues(3.dp, 0.dp, 7.dp, 0.dp))
            }
            Spacer(modifier = Modifier.width(6.dp))
            if (loan.state != LoanState.ONGOING &&
                !(loan.state == LoanState.ACCEPTED && user.id == loan.idBorrower))
                Button(
                    onClick = { manageLoanViewModel.declineLoan(loan, index) },
                    content = {
                      Icon(Icons.Default.Close, contentDescription = "cancel", modifier = Modifier)
                      Spacer(Modifier.width(2.dp))
                      if ((loan.state == LoanState.PENDING && isLender) ||
                          loan.state == LoanState.ACCEPTED) {
                        Text(text = "Cancel")
                      } else {
                        Text(text = "Reject")
                      }
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

@Composable
fun ClickableText(contact: String, favorite: Boolean = false) {
  val context = LocalContext.current
  Text(
      text = contact,
      fontSize = 13.sp,
      lineHeight = 1.3.em,
      fontWeight = if (favorite) FontWeight.Bold else FontWeight.Normal,
      modifier =
          Modifier.clickable {
            val intent =
                when {
                  contact.startsWith("Email:") ->
                      Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${contact.substringAfter("Email: ")}")
                      }
                  contact.startsWith("Phone:") ->
                      Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("smsto:${contact.substringAfter("Phone: ")}")
                      }
                  contact.startsWith("Telegram:") -> {
                    var telegramUsername = contact.substringAfter("Telegram: ")
                    if (telegramUsername.startsWith("@")) {
                      telegramUsername = telegramUsername.removePrefix("@")
                    }
                    val telegramAppUri = Uri.parse("tg://resolve?domain=$telegramUsername")
                    val telegramWebUri = Uri.parse("https://t.me/$telegramUsername")

                    // Try to open the Telegram app
                    val telegramIntent = Intent(Intent.ACTION_VIEW, telegramAppUri)
                    if (isAppInstalled(context, "org.telegram.messenger")) {
                      telegramIntent.setPackage("org.telegram.messenger")
                    } else {
                      // Fallback to web if Telegram app is not installed
                      telegramIntent.data = telegramWebUri
                    }
                    telegramIntent
                  }
                  else -> null
                }
            intent?.let { ContextCompat.startActivity(context, it, null) }
          })
}

// Utility function to check if an app is installed
fun isAppInstalled(context: android.content.Context, packageName: String): Boolean {
  val packageManager: PackageManager = context.packageManager
  return try {
    packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    true
  } catch (e: PackageManager.NameNotFoundException) {
    false
  }
}
