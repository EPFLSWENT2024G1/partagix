package com.android.partagix.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.user.User
import com.android.partagix.ui.navigation.NavigationActions

/**
 * ItemListColumn is a composable function that displays a list of items in a column. which is the
 * function that will be called when we want to see a list of items.
 *
 * @param list a list of items.
 * @param users a list of the users from the items
 * @param loan a list of loans associated to the items
 * @param title a string that represents the title of the column.
 * @param corner a string that represents the corner of the column.
 * @param isCornerClickable is a boolean that makes the corner text clickable or not
 * @param onItemClick a function that takes an item and returns a Unit.
 * @param onClickCorner a function that returns a Unit.
 * @param modifier a Modifier.
 */
@Composable
fun ItemListColumn(
    modifier: Modifier = Modifier,
    list: List<Item>,
    users: List<User>,
    loan: List<Loan>,
    availability: List<Boolean> = emptyList(),
    title: String,
    corner: String,
    isCornerClickable: Boolean = false,
    onItemClick: (Item) -> Unit = {},
    isClickable: Boolean,
    onClickCorner: () -> Unit = {},
    wasExpanded: List<Boolean> = emptyList(),
    expandState: Boolean = false,
    isOutgoing: Boolean,
    isOwner: Boolean = false,
    isBorrower: Boolean = false,
    onUserClick: (Loan) -> Unit = {},
    manageLoanViewModel: ManageLoanViewModel = ManageLoanViewModel(),
    itemViewModel: ItemViewModel = ItemViewModel(),
    navigationActions: NavigationActions,
    updateExpanded: (Int, Boolean) -> Unit = { i, expanded ->
      manageLoanViewModel.updateExpanded(i, expanded)
    },
    isExpandable: Boolean,
) {
  Column(modifier = modifier) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
          text = title,
          style =
              TextStyle(
                  fontSize = 18.sp,
                  fontWeight = FontWeight(800),
              ),
          modifier = Modifier.fillMaxWidth(0.7f).padding(horizontal = 10.dp))
      Text(
          text = corner,
          textAlign = TextAlign.Right,
          modifier =
              if (isCornerClickable) {
                Modifier.fillMaxWidth().padding(end = 10.dp).clickable { onClickCorner() }
              } else {
                Modifier.fillMaxWidth().padding(end = 10.dp)
              })
    }

    if (list.isEmpty()) {
      val emptyText =
          if (title.isBlank()) {
            "Empty"
          } else {
            "$title is empty"
          }
      Text(
          text = emptyText,
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp))
    } else {
      HorizontalDivider(
          color = MaterialTheme.colorScheme.outlineVariant,
          modifier = Modifier.height(0.5.dp).fillMaxWidth())

      ItemList(
          itemList = list,
          users = users,
          loan = loan,
          availability = availability,
          onItemClick =
              if (isClickable) {
                onItemClick
              } else {
                {}
              },
          isExpandable = isExpandable,
          wasExpanded = wasExpanded,
          isOutgoing = isOutgoing,
          isOwner = isOwner,
          isBorrower = isBorrower,
          onUserClick = onUserClick,
          manageLoanViewModel = manageLoanViewModel,
          modifier = Modifier.fillMaxSize(),
          expandState = expandState,
          navigationActions = navigationActions,
          updateExpanded = updateExpanded,
          itemViewModel = itemViewModel)
    }
  }
}
