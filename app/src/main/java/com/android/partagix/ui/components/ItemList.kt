package com.android.partagix.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.ui.navigation.NavigationActions
import java.util.Date

/**
 * ItemList composable to display a scrollable list of items.
 *
 * @param modifier Modifier to apply to this layout.
 * @param itemList a list of items to display.
 * @param users a list of users linked to an item.
 * @param loan a list of loans linked to an item.
 * @param isExpandable a boolean to determine if the item is expandable.
 * @param isOutgoing a boolean to determine if the item is an outgoing loan and set according
 *   buttons when expanded.
 * @param isOwner a boolean to determine if the user is the owner of the item, to hide its name.
 * @param isLender a boolean to determine if the user is the lender of the item, to hide the
 *   availability.
 * @param expandState a boolean to determine if the item is expanded.
 * @param wasExpanded a list of booleans to determine which item was expanded.
 * @param onItemClick a lambda to handle item click events.
 * @param onUserClick a lambda to handle owner's name click events.
 * @param manageLoanViewModel a ManageLoanViewModel to handle loan management.
 * @param stickyHeader a lambda to display a sticky header.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)
@Composable
fun ItemList(
    modifier: Modifier = Modifier,
    itemList: List<Item>,
    users: List<User>,
    loan: List<Loan>,
    isExpandable: Boolean,
    isOutgoing: Boolean,
    isOwner: Boolean = false,
    isLender: Boolean = false,
    expandState: Boolean = false,
    wasExpanded: List<Boolean>,
    onItemClick: (Item) -> Unit,
    onUserClick: ((Loan) -> Unit)? = null,
    manageLoanViewModel: ManageLoanViewModel = ManageLoanViewModel(),
    itemViewModel: ItemViewModel = ItemViewModel(),
    navigationActions: NavigationActions,
    updateExpanded: (Int, Boolean) -> Unit = { i, expanded ->
      manageLoanViewModel.updateExpanded(i, expanded)
    },
    stickyHeader: @Composable (() -> Unit)? = null,
) {
  LazyColumn(modifier = modifier.fillMaxSize()) {
    if (stickyHeader != null) {
      stickyHeader { stickyHeader() }
    }
    items(itemList.size) { index ->
      val item = itemList[index]
      val user =
          if (users.isEmpty()) {
            User("", "noname", "", "0", Inventory("", emptyList()))
          } else {
            if (users.size <= index) {
              User("", "noname", "", "0", Inventory("", emptyList()))
            } else {
              users[index]
            }
          }

      /*      val onUserClick = {
        if (user.id.isNotEmpty()) {
          userViewModel.setUser(user)
          navigationActions.navigateTo(Route.OTHER_ACCOUNT)
        }
      }*/

      ItemUi(
          item = item,
          user = user,
          loan =
              if (loan.isEmpty()) {
                Loan("", "", "", "", Date(), Date(), "", "", "", "", LoanState.CANCELLED)
              } else {
                if (loan.size <= index) {
                  Loan("", "", "", "", Date(), Date(), "", "", "", "", LoanState.CANCELLED)
                } else {
                  loan[index]
                }
              },
          isExpandable = isExpandable,
          index = index,
          isOutgoing = isOutgoing,
          isOwner = isOwner,
          isLender = isLender,
          onItemClick = onItemClick,
          onUserClick = onUserClick,
          manageLoanViewModel = manageLoanViewModel,
          itemViewModel = itemViewModel,
          navigationActions = navigationActions,
          expandState = if (isExpandable) wasExpanded[index] else expandState,
          updateExpanded = updateExpanded,
          modifier = modifier.testTag("ItemListItem"))

      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}
