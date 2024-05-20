package com.android.partagix.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import java.util.Date

/**
 * ItemList composable to display a scrollable list of items, which can execute the onClick when
 * clicked.
 *
 * @param itemList a list of items to display.
 * @param onClick a lambda to handle item click events.
 * @param modifier Modifier to apply to this layout.
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
    expandState: Boolean = false,
    wasExpanded: List<Boolean>,
    onClick: (Item) -> Unit,
    manageLoanViewModel: ManageLoanViewModel = ManageLoanViewModel(),
    stickyHeader: @Composable() (() -> Unit)? = null,
) {
  println("----- wasExpanded: $wasExpanded")
  LazyColumn(modifier = modifier.fillMaxSize()) {
    if (stickyHeader != null) {
      stickyHeader { stickyHeader() }
    }
    items(itemList.size) { index ->
      val item = itemList[index]
      Box(
          modifier =
              Modifier.fillMaxSize()
                  .clickable {
                    if (!isExpandable) {
                      onClick(item)
                    }
                  }
                  .testTag("ItemListItem")) {
            ItemUi(
                isExpandable = isExpandable,
                item = item,
                user =
                    if (users.isEmpty()) {
                      User("", "noname", "", "norank", Inventory("", emptyList()))
                    } else {
                      if (users.size <= index) {
                        User("", "noname", "", "norank", Inventory("", emptyList()))
                      } else {
                        users[index]
                      }
                    },
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
                index = index,
                isOutgoing = isOutgoing,
                manageLoanViewModel = manageLoanViewModel,
                expandState = if (isExpandable) wasExpanded[index] else  expandState,
            )
          }
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}
